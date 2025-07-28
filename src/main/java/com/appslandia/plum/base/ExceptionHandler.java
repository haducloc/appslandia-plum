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
import com.appslandia.common.utils.ExceptionUtils;
import com.appslandia.common.utils.MimeTypes;
import com.appslandia.common.utils.XmlEscaper;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class ExceptionHandler {

  public static final String REQUEST_ATTRIBUTE_EXCEPTION = ExceptionHandler.class.getName() + ".exception";

  @Inject
  protected AppConfig appConfig;

  @Inject
  protected JsonProcessor jsonProcessor;

  public void handleException(HttpServletRequest request, HttpServletResponse response, Throwable exception)
      throws ServletException, IOException {

    // Already committed?
    if (response.isCommitted()) {
      return;
    }

    // Problem
    var problem = getProblem(request, exception);
    request.setAttribute(Problem.class.getName(), problem);
    request.setAttribute(REQUEST_ATTRIBUTE_EXCEPTION, exception);

    // Send Error
    response.sendError(problem.getStatus(), problem.getTitle());
  }

  public Problem getProblem(HttpServletRequest request, Throwable exception) {
    var requestContext = ServletUtils.getRequestContext(request);
    var problem = new Problem().setException(exception);

    // ProblemSupport
    if (exception instanceof ProblemSupport ps) {
      var p = ps.getProblem();
      if (p != null) {
        problem.setStatus(p.getStatus()).setTitle(p.getTitle()).setTitleKey(p.getTitleKey()).setDetail(p.getDetail())
            .setDetailKey(p.getDetailKey()).setType(p.getType()).setInstance(p.getInstance())
            .setExtensions(p.getExtensions());
      }
    }

    // Status
    if (problem.getStatus() == null) {
      problem.setStatus((Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE));
    }
    if (problem.getStatus() == null) {
      problem.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    // Title
    if (problem.getTitleKey() != null) {
      problem.setTitle(problem.getTitleKey().getRes(requestContext.getResources()));
    }
    if (problem.getTitle() == null) {
      problem.setTitle(getErrorMessage(problem.getStatus(), exception, requestContext.getResources()));
    }

    // Detail
    if (problem.getDetailKey() != null) {
      problem.setDetail(problem.getDetailKey().getRes(requestContext.getResources()));
    }

    // exception
    if (this.appConfig.isEnableDebug()) {
      if (exception != null) {
        problem.setStackTrace(ExceptionUtils.toStackTrace(exception));
      }
    }

    // modelState
    var modelState = (ModelState) request.getAttribute(ModelState.REQUEST_ATTRIBUTE_ID);
    if ((modelState != null) && !modelState.isValid()) {
      problem.setModelState(modelState);
    }
    return problem;
  }

  public void writeJsonError(HttpServletRequest request, HttpServletResponse response, int status, Problem problem)
      throws ServletException, IOException {

    response.setContentType(MimeTypes.APP_JSON_PROBLEM);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    response.setStatus(status);

    var out = response.getWriter();
    this.jsonProcessor.write(out, problem);

    out.flush();
  }

  public void writeSimpleHtml(HttpServletRequest request, HttpServletResponse response, int status, String message)
      throws ServletException, IOException {

    response.setContentType(MimeTypes.TEXT_HTML);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    response.setStatus(status);

    var out = response.getWriter();
    var requestContext = ServletUtils.getRequestContext(request);

    out.println("<!DOCTYPE html>");
    out.format("<html lang=\"%s\">", requestContext.getLanguageId());
    out.println();
    out.println("<head>");
    out.println(" <meta charset=\"utf-8\">");
    out.println(" <meta name=\"viewport\" content=\"width=device-width, initial-scale=1, shrink-to-fit=no\">");
    out.format(" <title>HTTP Status %d</title>", status);
    out.println();
    out.println("</head>");
    out.println("<body>");
    out.format(" <h3 role=\"alert\">HTTP Status %d - %s</h3>", status, XmlEscaper.escapeContent(message));
    out.println();
    out.println("</body>");
    out.print("</html>");

    out.flush();
  }

  protected String getErrorMessage(int status, Throwable exception, Resources resources) {
    if (exception == null) {
      return resources.get(getMsgKey(status));
    }
    if (this.appConfig.isEnableDebug()) {
      return String.format("%s (exception=%s)", resources.get(getMsgKey(status)),
          ExceptionUtils.buildMessage(exception));
    }
    if (exception.getMessage() != null) {
      return exception.getMessage();
    }
    return resources.get(getMsgKey(status));
  }

  protected String getMsgKey(int status) {
    return switch (status) {
    case HttpServletResponse.SC_BAD_REQUEST -> Resources.ERROR_BAD_REQUEST;
    case HttpServletResponse.SC_UNAUTHORIZED -> Resources.ERROR_UNAUTHORIZED;
    case HttpServletResponse.SC_FORBIDDEN -> Resources.ERROR_FORBIDDEN;
    case HttpServletResponse.SC_NOT_FOUND -> Resources.ERROR_NOT_FOUND;
    case HttpServletResponse.SC_METHOD_NOT_ALLOWED -> Resources.ERROR_METHOD_NOT_ALLOWED;
    case HttpServletResponse.SC_PRECONDITION_FAILED -> Resources.ERROR_PRECONDITION_FAILED;
    case HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE -> Resources.ERROR_UNSUPPORTED_MEDIA_TYPE;
    case TooManyRequestsException.SC_TOO_MANY_REQUESTS -> Resources.ERROR_TOO_MANY_REQUESTS;
    case HttpServletResponse.SC_INTERNAL_SERVER_ERROR -> Resources.ERROR_INTERNAL_SERVER_ERROR;
    case HttpServletResponse.SC_SERVICE_UNAVAILABLE -> Resources.ERROR_SERVICE_UNAVAILABLE;
    default -> Resources.ERROR_REQUEST_HANDLING_ERROR;
    };
  }
}
