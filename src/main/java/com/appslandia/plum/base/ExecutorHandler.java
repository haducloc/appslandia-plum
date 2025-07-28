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

import com.appslandia.common.json.JsonProcessor;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.MimeTypes;
import com.appslandia.common.utils.StringUtils;
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
  protected HeaderPolicyProvider headerPolicyProvider;

  @Inject
  protected ActionInvoker actionInvoker;

  @Inject
  protected ControllerProvider controllerProvider;

  @Inject
  protected CaptchaManager captchaManager;

  @Inject
  protected CsrfManager csrfManager;

  @Inject
  protected TempDataManager tempDataManager;

  @Inject
  protected ActionFilterProvider actionFilterProvider;

  @Inject
  protected AppConfig appConfig;

  protected void doRequest(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext)
      throws ServletException, IOException {
    try {
      // DEBUG
      if (this.appConfig.isEnableDebug()) {
        ServletUtils.testErrorStatus(request, "__test_error_status2");
        ServletUtils.testOutStream(request, response, "__test_out_stream2");
      }

      getFilterChain(requestContext).doFilter(request, response, requestContext);

    } catch (RuntimeException | ServletException | IOException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new ServletException(ex);
    }
  }

  protected ActionFilterChain getFilterChain(RequestContext requestContext) {
    var actionFilters = (requestContext.getActionDesc().getEnableFilters() != null)
        ? requestContext.getActionDesc().getEnableFilters().value()
        : StringUtils.EMPTY_ARRAY;

    return new ActionFilterChain(actionFilters, this.actionFilterProvider) {

      @Override
      protected void execute(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext)
          throws Exception {
        onActionInvoking(request, response, requestContext);

        // Invoke Action
        var controller = ExecutorHandler.this.controllerProvider
            .getController(requestContext.getActionDesc().getControllerClass());
        var result = ExecutorHandler.this.actionInvoker.invokeAction(request, response, requestContext, controller);

        onResultExecuting(request, response, requestContext, result);

        // Execute Result
        if (ActionResult.class.isAssignableFrom(requestContext.getActionDesc().getMethod().getReturnType())) {
          Asserts.notNull(result);
          ((ActionResult) result).execute(request, response, requestContext);

        } else if (requestContext.getActionDesc().getMethod().getReturnType() != void.class) {
          writeJsonResult(response, result);
        }

        // DEBUG
        if (ExecutorHandler.this.appConfig.isEnableDebug()) {
          ServletUtils.testErrorStatus(request, "__test_error_status3");
          ServletUtils.testOutStream(request, response, "__test_out_stream3");
        }
      }
    };
  }

  protected void onActionInvoking(HttpServletRequest request, HttpServletResponse response,
      RequestContext requestContext) throws Exception {

    // TempData
    if (requestContext.isGetOrHead()) {
      this.tempDataManager.loadTempData(request, response);
    }
    // CSRF
    if ((requestContext.getActionDesc().getEnableCsrf() != null) && request.getMethod().equals(HttpMethod.POST)) {
      this.csrfManager.verifyCsrf(request, true);
    }
    // CAPTCHA
    if ((requestContext.getActionDesc().getEnableCaptcha() != null) && request.getMethod().equals(HttpMethod.POST)) {
      this.captchaManager.verifyCaptcha(request);
    }
  }

  protected void onResultExecuting(HttpServletRequest request, HttpServletResponse response,
      RequestContext requestContext, Object result) throws Exception {

    // @CacheControl
    if ((requestContext.getActionDesc().getCacheControl() != null) && requestContext.isGetOrHead()) {
      var headerPolicy = this.headerPolicyProvider
          .getHeaderPolicy(requestContext.getActionDesc().getCacheControl().value());

      headerPolicy.writePolicy(request, response, requestContext);
    }
  }

  protected void writeJsonResult(HttpServletResponse response, Object result) throws Exception {
    response.setContentType(MimeTypes.APP_JSON);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    this.jsonProcessor.write(response.getWriter(), result);
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
