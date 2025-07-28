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

import com.appslandia.common.base.AppLogger;
import com.appslandia.common.base.ToStringBuilder;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.MimeTypes;
import com.appslandia.common.utils.STR;
import com.appslandia.plum.results.ViewResult;
import com.appslandia.plum.utils.ServletUtils;
import com.appslandia.plum.utils.WebBeanTSPolicy;

import jakarta.inject.Inject;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
public class ErrorServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;

  public static final String CONFIG_ERROR_DEV = ErrorServlet.class.getName() + ".error_dev";

  @Inject
  protected AppLogger appLogger;

  @Inject
  protected AppConfig appConfig;

  @Inject
  protected ExceptionHandler exceptionHandler;

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    this.appLogger.info(STR.fmt("Initializing '{}', servletName='{}'.", getClass().getName(), config.getServletName()));
  }

  private void doRequest(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    // Throwable
    var exception = getException(request);
    if ((exception != null) && (exception.getClass().getDeclaredAnnotation(NotLog.class) == null)) {
      this.appLogger.error(exception);
    }

    // Already Committed?
    if (response.isCommitted()) {
      return;
    }

    // DispatcherType.REQUEST?
    if (request.getDispatcherType() == DispatcherType.REQUEST) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      response.getWriter().print("HTTP Status 404 - Not Found");
      return;
    }

    // Problem
    var problem = (Problem) request.getAttribute(Problem.class.getName());
    if (problem == null) {
      problem = this.exceptionHandler.getProblem(request, exception);
      request.setAttribute(Problem.class.getName(), problem);
    }

    // HttpHeaderApply
    if (exception instanceof HttpHeaderApply) {
      ((HttpHeaderApply) exception).apply(response);
    }

    // NO Cache
    var requestContext = ServletUtils.getRequestContext(request);
    NoCachePolicy.INSTANCE.writePolicy(request, response, requestContext);

    // Write Error
    if (ActionDescUtils.isJsonError(requestContext.getActionDesc())) {
      this.exceptionHandler.writeJsonError(request, response, problem.getStatus(), problem);

    } else {
      if (this.appConfig.getBool(CONFIG_ERROR_DEV, false)) {
        writeErrorDev(request, response, requestContext);
      } else {
        try {
          writeErrorProd(request, response, requestContext);

        } catch (Exception ex) {
          this.appLogger.error(exception);

          if (!response.isCommitted()) {
            response.resetBuffer();

            this.exceptionHandler.writeSimpleHtml(request, response, problem.getStatus(), problem.getTitle());
          }
        }
      }
    }
  }

  protected Throwable getException(HttpServletRequest request) {
    var ex = (Throwable) request.getAttribute(ExceptionHandler.REQUEST_ATTRIBUTE_EXCEPTION);
    if (ex != null) {
      return ex;
    }
    return (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
  }

  protected String getErrorViewPath(HttpServletRequest request, RequestContext requestContext) {
    return "/error";
  }

  protected void writeErrorProd(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext)
      throws ServletException, IOException {

    var viewPath = getErrorViewPath(request, requestContext);
    var viewLocation = ViewResult.resolveViewLocation(viewPath, request);
    Asserts.notNull(viewLocation);

    ServletUtils.forward(request, response, viewLocation);
  }

  protected void writeErrorDev(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext)
      throws ServletException, IOException {
    response.setContentType(MimeTypes.TEXT_PLAIN);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());

    var problem = (Problem) request.getAttribute(Problem.class.getName());
    Asserts.notNull(problem);

    var out = response.getWriter();

    out.append("Error Exception: ").append(String.valueOf(getException(request)));
    out.println();
    out.append("Error Exception Type: ")
        .append(String.valueOf(request.getAttribute(RequestDispatcher.ERROR_EXCEPTION_TYPE)));
    out.println();
    out.append("Error Message: ").append(String.valueOf(request.getAttribute(RequestDispatcher.ERROR_MESSAGE)));
    out.println();
    out.append("Error Request URI: ").append(String.valueOf(request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI)));
    out.println();
    out.append("Error Servlet Name: ")
        .append(String.valueOf(request.getAttribute(RequestDispatcher.ERROR_SERVLET_NAME)));
    out.println();
    out.append("Error Status Code: ").append(String.valueOf(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)));
    out.println();

    out.println();
    out.append("Problem Status: ").append(String.valueOf(problem.getStatus()));
    out.println();
    out.append("Problem Title: ").append(problem.getTitle());
    out.println();
    out.append("Problem Detail: ").append(problem.getDetail());
    out.println();
    out.append("Problem Type: ").append(problem.getType());
    out.println();
    out.append("Problem Instance: ").append(problem.getInstance());
    out.println();
    out.append("Problem ModelState: ").append(new ToStringBuilder(3).toString(problem.getModelState()));
    out.println();
    out.append("Problem Extensions: ").append(new ToStringBuilder(3).toString(problem.getExtensions()));
    out.println();

    if (problem.getException() != null) {
      out.append("Problem Exception: ");
      problem.getException().printStackTrace(out);
      out.println();
    }

    out.println();
    out.append("Request Info: ")
        .append(new ToStringBuilder(4).setTSPolicy(new WebBeanTSPolicy(true, true, true, false)).toString(request));
    out.println();

    out.flush();
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    doRequest(req, resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    doRequest(req, resp);
  }

  @Override
  protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    doRequest(req, resp);
  }

  @Override
  protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    doRequest(req, resp);
  }

  @Override
  protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    doRequest(req, resp);
  }

  @Override
  protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    throw new UnsupportedOperationException();
  }
}
