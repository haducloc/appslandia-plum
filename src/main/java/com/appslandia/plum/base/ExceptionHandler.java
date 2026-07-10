// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.appslandia.common.json.JsonProcessor;
import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.ContentTypes;
import com.appslandia.common.utils.ValueUtils;
import com.appslandia.common.utils.XmlEscaper;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
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

  @Inject
  protected AppConfig appConfig;

  @Inject
  protected AppLogger appLogger;

  @Inject
  protected JsonProcessor jsonProcessor;

  public void handleException(HttpServletRequest request, HttpServletResponse response, Throwable exception)
      throws ServletException, IOException {
    Asserts.notNull(exception);

    // Log Exception
    if (exception.getClass().getDeclaredAnnotation(NotLog.class) == null) {
      appLogger.error(exception);
    }

    if (response.isCommitted()) {
      return;
    }

    // Problem
    var problem = resolveProblem(request, exception);
    request.setAttribute(Problem.class.getName(), problem);

    // Backup Headers
    var backupHeaders = new ResponseHeaders(response, h -> {

      // CORS
      if (CorsPolicy.isCorsResponseHeader(h)) {
        return true;
      }
      return false;
    });

    // response.reset();

    request.setAttribute(ResponseHeaders.class.getName(), backupHeaders);
    response.sendError(problem.getStatus(), problem.getTitle());
  }

  protected Problem resolveProblem(HttpServletRequest request, Throwable exception) {
    Arguments.notNull(exception);
    var requestContext = ServletUtils.getRequestContext(request);

    // ProblemException
    if (exception instanceof ProblemException pe) {
      var status = ValueUtils.valueOrAlt(pe.getStatus(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

      var title = pe.getTitleKey() != null ? requestContext.res(pe.getTitleKey())
          : requestContext.res(Resources.getMsgKey(status));

      String detail = null;

      if (pe.getDetailKey() != null) {
        detail = requestContext.res(pe.getDetailKey());
      }
      return new Problem(status, title, detail, pe.getType(), pe.getErrors(), pe.getExts(),
          requestContext.getRelativePath(), requestContext.getTraceId(), exception);
    }

    var status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
    var title = requestContext.res(Resources.getMsgKey(status));

    return new Problem(status, title, null, null, null, null, requestContext.getRelativePath(),
        requestContext.getTraceId(), exception);
  }

  public void writeJsonError(HttpServletRequest request, HttpServletResponse response, int errorStatus, Problem problem)
      throws ServletException, IOException {

    response.setStatus(errorStatus);
    response.setContentType(ContentTypes.APP_JSON_PROBLEM);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());

    var out = response.getWriter();
    jsonProcessor.write(out, problem);

    out.flush();
  }

  public void writeSimpleHtml(HttpServletRequest request, HttpServletResponse response, int status, String message)
      throws ServletException, IOException {

    response.setContentType(ContentTypes.TEXT_HTML);
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
}
