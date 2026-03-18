// The MIT License (MIT)
// Copyright © 2015 Loc Ha

// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:

// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.

// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

package com.appslandia.plum.base;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.appslandia.common.base.UncheckedException;
import com.appslandia.common.json.JsonProcessor;
import com.appslandia.common.utils.ContentTypes;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
public class ExecutorHandler extends HttpServlet {
  private static final long serialVersionUID = 1L;

  @Inject
  protected JsonProcessor jsonProcessor;

  @Inject
  protected ActionInvoker actionInvoker;

  @Inject
  protected ControllerProvider controllerProvider;

  @Inject
  protected ActionFilterProvider actionFilterProvider;

  @Inject
  protected CaptchaManager captchaManager;

  @Inject
  protected CsrfManager csrfManager;

  @Inject
  protected TempDataManager tempDataManager;

  @Inject
  protected AppConfig appConfig;

  protected void doRequest(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext)
      throws ServletException, IOException {
    try {

      // DEBUG
      if (appConfig.isEnableDebug()) {
        ServletUtils.testErrorStatus(request, "__test_error_status2");
        ServletUtils.testOutStream(request, response, "__test_out_stream2");
      }

      var filters = actionFilterProvider.getActionFilters(requestContext.getRelativePath());
      var filterChain = new ActionFilterChain(filters) {

        @Override
        protected void executeAction(HttpServletRequest request, HttpServletResponse response,
            RequestContext requestContext) throws Exception {

          ExecutorHandler.this.executeAction(request, response, requestContext);
        }
      };
      filterChain.doFilter(request, response, requestContext);

    } catch (RuntimeException | ServletException | IOException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new UncheckedException(ex);
    }
  }

  protected void executeAction(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext)
      throws Exception {

    // DEBUG
    if (appConfig.isEnableDebug()) {
      ServletUtils.testErrorStatus(request, "__test_error_status3");
      ServletUtils.testOutStream(request, response, "__test_out_stream3");
    }

    // TempData
    if (requestContext.isGetOrHead()) {
      tempDataManager.loadTempData(request, response);
    }

    // CSRF
    if ((requestContext.getActionDesc().getEnableCsrf() != null) && request.getMethod().equals(HttpMethod.POST)) {
      csrfManager.verifyCsrf(request, true);
    }

    // CAPTCHA
    if ((requestContext.getActionDesc().getEnableCaptcha() != null) && request.getMethod().equals(HttpMethod.POST)) {
      captchaManager.verifyCaptcha(request);
    }

    // Invoke Action
    var actionDesc = requestContext.getActionDesc();
    var controller = controllerProvider.getController(actionDesc.getControllerClass());
    var result = actionInvoker.invokeAction(request, response, requestContext, controller);

    // Execute Result
    if (ActionResult.class.isAssignableFrom(actionDesc.getMethod().getReturnType())) {
      if (result != null) {
        ((ActionResult) result).execute(request, response, requestContext);
      }
    } else if (Void.class != actionDesc.getMethod().getReturnType()) {
      writeJsonResult(response, result);
    }

    // DEBUG
    if (appConfig.isEnableDebug()) {
      ServletUtils.testErrorStatus(request, "__test_error_status4");
      ServletUtils.testOutStream(request, response, "__test_out_stream4");
    }
  }

  protected void writeJsonResult(HttpServletResponse response, Object result) throws Exception {
    response.setContentType(ContentTypes.APP_JSON);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    jsonProcessor.write(response.getWriter(), result);
  }

  @Override
  protected void service(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    if (request.getMethod().equals(HttpMethod.PATCH)) {
      doPatch(request, response);
    } else {
      super.service(request, response);
    }
  }

  @Override
  protected void doPatch(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    doRequest(request, response, ServletUtils.getRequestContext(request));
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doRequest(request, response, ServletUtils.getRequestContext(request));
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doRequest(request, response, ServletUtils.getRequestContext(request));
  }

  @Override
  protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doRequest(request, response, ServletUtils.getRequestContext(request));
  }

  @Override
  protected void doDelete(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    doRequest(request, response, ServletUtils.getRequestContext(request));
  }

  @Override
  protected void doOptions(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    throw new UnsupportedOperationException();
  }

  public boolean isMockContext() {
    return false;
  }
}
