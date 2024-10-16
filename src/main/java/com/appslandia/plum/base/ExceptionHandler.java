// The MIT License (MIT)
// Copyright © 2015 AppsLandia. All rights reserved.

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
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import com.appslandia.common.base.AppLogger;
import com.appslandia.common.cdi.Json;
import com.appslandia.common.cdi.Json.Profile;
import com.appslandia.common.json.JsonProcessor;
import com.appslandia.common.utils.ExceptionUtils;
import com.appslandia.common.utils.MimeTypes;
import com.appslandia.common.utils.ObjectUtils;
import com.appslandia.common.utils.STR;
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
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@ApplicationScoped
public class ExceptionHandler {

  @Inject
  protected AppLogger appLogger;

  @Inject
  protected AppConfig appConfig;

  @Inject
  @Json(Profile.PRETTY)
  protected JsonProcessor jsonProcessor;

  public void handleException(HttpServletRequest request, HttpServletResponse response, Throwable exception)
      throws ServletException, IOException {
    if (exception.getClass().getDeclaredAnnotation(NotLog.class) == null) {
      this.appLogger.error(exception);
    }

    if (this.appConfig.isEnableDebug()) {
      this.appLogger.debug(STR.fmt("ExceptionHandler.handleException: response={}", ObjectUtils.toIdHash(response)));
    }

    // Already committed?
    if (response.isCommitted()) {
      return;
    }
    writeException(request, response, exception);
  }

  protected void writeException(HttpServletRequest request, HttpServletResponse response, Throwable exception)
      throws ServletException, IOException {

    RequestContext requestContext = ServletUtils.getRequestContext(request);
    Problem problem = getProblem(request, exception);

    if (ActionDescUtils.isJsonError(requestContext.getActionDesc())) {

      response.reset();
      this.writeJsonError(request, response, problem.getStatus(), problem);

    } else {
      request.setAttribute(Problem.class.getName(), problem);
      response.sendError(problem.getStatus(), problem.getTitle());
    }
  }

  public Problem getProblem(HttpServletRequest request, Throwable exception) {
    RequestContext requestContext = ServletUtils.getRequestContext(request);
    Problem problem = new Problem().setException(exception);

    // ProblemSupport
    if (exception instanceof ProblemSupport) {
      Problem prob = ((ProblemSupport) exception).getProblem();
      if (prob != null) {
        problem.setStatus(prob.getStatus()).setTitle(prob.getTitle()).setTitleKey(prob.getTitleKey())
            .setDetail(prob.getDetail()).setDetailKey(prob.getDetailKey()).setType(prob.getType())
            .setInstance(prob.getInstance()).setExtensions(prob.getExtensions());
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
    ModelState modelState = (ModelState) request.getAttribute(ModelState.REQUEST_ATTRIBUTE_ID);
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

    PrintWriter out = response.getWriter();
    this.jsonProcessor.write(out, problem);

    out.flush();
  }

  public void writeSimpleHtml(HttpServletRequest request, HttpServletResponse response, int status, String message)
      throws ServletException, IOException {

    response.setContentType(MimeTypes.TEXT_HTML);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    response.setStatus(status);

    PrintWriter out = response.getWriter();
    RequestContext requestContext = ServletUtils.getRequestContext(request);

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
    out.format(" <h3 role=\"alert\">HTTP Status %d - %s</h3>", status, XmlEscaper.escapeXml(message));
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
    switch (status) {
    case HttpServletResponse.SC_BAD_REQUEST:
      return Resources.ERROR_BAD_REQUEST;

    case HttpServletResponse.SC_UNAUTHORIZED:
      return Resources.ERROR_UNAUTHORIZED;

    case HttpServletResponse.SC_FORBIDDEN:
      return Resources.ERROR_FORBIDDEN;

    case HttpServletResponse.SC_NOT_FOUND:
      return Resources.ERROR_NOT_FOUND;

    case HttpServletResponse.SC_METHOD_NOT_ALLOWED:
      return Resources.ERROR_METHOD_NOT_ALLOWED;

    case HttpServletResponse.SC_PRECONDITION_FAILED:
      return Resources.ERROR_PRECONDITION_FAILED;

    case HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE:
      return Resources.ERROR_UNSUPPORTED_MEDIA_TYPE;

    case TooManyRequestsException.SC_TOO_MANY_REQUESTS:
      return Resources.ERROR_TOO_MANY_REQUESTS;

    case HttpServletResponse.SC_SERVICE_UNAVAILABLE:
      return Resources.ERROR_SERVICE_UNAVAILABLE;

    default:
      return Resources.ERROR_INTERNAL_SERVER_ERROR;
    }
  }
}
