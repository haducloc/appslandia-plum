// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.appslandia.common.base.ToStringBuilder;
import com.appslandia.common.json.JsonProcessor;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.ContentTypes;
import com.appslandia.common.utils.ExceptionUtils;
import com.appslandia.common.utils.ObjectUtils;
import com.appslandia.common.utils.STR;
import com.appslandia.common.utils.XmlEscaper;
import com.appslandia.plum.results.ViewResult;
import com.appslandia.plum.utils.ServletUtils;

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

  public static final String CONFIG_ERROR_RESPONSE_DEV = "error_response.for_dev";

  @Inject
  protected AppLogger appLogger;

  @Inject
  protected AppConfig appConfig;

  @Inject
  protected JsonProcessor jsonProcessor;

  @Inject
  protected ExceptionHandler exceptionHandler;

  @Inject
  protected HeaderPolicyProvider headerPolicyProvider;

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    appLogger.info("Initializing '{}', servletName='{}'.", getClass().getName(), config.getServletName());
  }

  protected boolean isEnableJsonError(RequestContext requestContext) {
    if (requestContext.isEnableJsonError()) {
      return true;
    }
    return appConfig.isApiModule(requestContext.getModule());
  }

  protected void applyHeaders(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext) {
    // Global Headers
    headerPolicyProvider.getHeaderPolicies().forEach(p -> {

      if (p.isAllMatched()) {
        p.writePolicy(request, response, requestContext);
      }
    });

    // HTML
    if (!isEnableJsonError(requestContext)) {

      headerPolicyProvider.getHeaderPolicies().forEach(p -> {

        if (HtmlHeaderPolicy.class.isInstance(p) && p.matches(requestContext.getHandlingPath())) {
          p.writePolicy(request, response, requestContext);
        }
      });
    }

    // Restore Headers
    var backupHeaders = (ResponseHeaders) ServletUtils.removeAttribute(request, ResponseHeaders.class.getName());
    if (backupHeaders != null) {
      backupHeaders.restore(response);
    }
  }

  private void doRequest(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    // DispatcherType.ERROR
    if (request.getDispatcherType() != DispatcherType.ERROR) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      response.getWriter().print("HTTP Status 404 - Not Found");
      return;
    }

    // RequestContext
    var requestContext = ServletUtils.getRequestContext(request);

    // Apply Headers
    applyHeaders(request, response, requestContext);

    // No cache
    ServletUtils.writeNoCache(response);

    // Problem
    var problem = (Problem) request.getAttribute(Problem.class.getName());
    if (problem == null) {
      problem = resolveProblem(request);
      request.setAttribute(Problem.class.getName(), problem);
    }

    // HeaderWriterSupport
    if (problem.getException() instanceof HeaderWriterSupport hw) {
      if (hw.getHeaderWriter() != null) {
        hw.getHeaderWriter().accept(response);
      }
    }

    // TRACE
    if (HttpMethod.TRACE.equals(request.getMethod())) {
      response.setStatus(problem.getStatus());
      return;
    }

    // JSON | HTML
    if (isEnableJsonError(requestContext)) {
      writeJsonError(request, response, problem.getStatus(), problem);
    } else {

      if (appConfig.getBool(CONFIG_ERROR_RESPONSE_DEV, false)) {
        writeErrorDev(request, response, problem.getStatus(), requestContext);

      } else {
        try {
          writeErrorProd(request, response, problem.getStatus(), requestContext);

        } catch (Exception ex) {
          appLogger.error(ex);

          if (!response.isCommitted()) {
            response.resetBuffer();

            writeSimpleHtml(request, response, problem.getStatus(), problem.getTitle());
          }
        }
      }
    }
  }

  protected Problem resolveProblem(HttpServletRequest request) {
    var requestContext = ServletUtils.getRequestContext(request);

    var status = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
    Asserts.notNull(status, STR.fmt("No request attribute: {}", RequestDispatcher.ERROR_STATUS_CODE));

    var title = requestContext.res(Resources.getMsgKey(status));

    var exception = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
    return new Problem(status, title, null, null, null, null, requestContext.getHandlingPath(),
        requestContext.getTraceId(), exception);
  }

  /**
   * Determines the error view path for the given module. The path is relative to {@link AppConfig#getViewDir()}. The
   * default implementation returns {@code "/error"}.
   *
   * @param module the module
   * @return the error view path
   */
  protected String getErrorViewPath(String module) {
    return "/error";
  }

  protected void writeErrorProd(HttpServletRequest request, HttpServletResponse response, int errorStatus,
      RequestContext requestContext) throws ServletException, IOException {

    var viewPath = getErrorViewPath(requestContext.getModule());
    Asserts.notNull(viewPath, "getErrorViewPath(String module) must not return null.");

    var viewLocation = ViewResult.resolveViewLocation(viewPath, request);
    Asserts.notNull(viewLocation);

    response.setStatus(errorStatus);
    ServletUtils.forward(request, response, viewLocation);
  }

  protected void writeErrorDev(HttpServletRequest request, HttpServletResponse response, int errorStatus,
      RequestContext requestContext) throws ServletException, IOException {
    response.setStatus(errorStatus);
    response.setContentType(ContentTypes.TEXT_PLAIN);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());

    var problem = (Problem) request.getAttribute(Problem.class.getName());
    Asserts.notNull(problem);

    var out = response.getWriter();

    out.append("RequestDispatcher.ERROR_REQUEST_URI: ")
        .append(String.valueOf(request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI)));
    out.println();
    out.append("RequestDispatcher.ERROR_QUERY_STRING: ")
        .append(String.valueOf(request.getAttribute(RequestDispatcher.ERROR_QUERY_STRING)));
    out.println();
    out.append("RequestDispatcher.ERROR_SERVLET_NAME: ")
        .append(String.valueOf(request.getAttribute(RequestDispatcher.ERROR_SERVLET_NAME)));
    out.println();
    out.append("RequestDispatcher.ERROR_METHOD: ")
        .append(String.valueOf(request.getAttribute(RequestDispatcher.ERROR_METHOD)));
    out.println();
    out.append("RequestDispatcher.ERROR_STATUS_CODE: ")
        .append(String.valueOf(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)));
    out.println();

    out.append("RequestDispatcher.ERROR_MESSAGE: ")
        .append(String.valueOf(request.getAttribute(RequestDispatcher.ERROR_MESSAGE)));
    out.println();
    out.append("RequestDispatcher.ERROR_EXCEPTION_TYPE: ")
        .append(String.valueOf(request.getAttribute(RequestDispatcher.ERROR_EXCEPTION_TYPE)));
    out.println();

    var ex = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
    out.append("RequestDispatcher.ERROR_EXCEPTION: ").append(ex != null ? ExceptionUtils.buildMessage(ex) : "null");
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
    out.append("Problem TraceId: ").append(problem.getTraceId());
    out.println();
    out.append("Problem Exts: ").append(new ToStringBuilder(5).toString(problem.getExts()));
    out.println();

    if (problem.getException() != null) {
      out.println();
      out.append("Problem Exception: ");
      problem.getException().printStackTrace(out);
      out.println();
    }

    out.println();
    out.append("Request Context: ").append(new ToStringBuilder(4).toString(requestContext));
    out.println();

    if (request.getUserPrincipal() != null) {
      var principal = (AuthPrincipal) request.getUserPrincipal();
      out.println();
      out.append("request.getUserPrincipal(): ").append(ObjectUtils.toIdHash(request.getUserPrincipal()));
      out.println();
      out.append("principal.getName(): ").append(principal.getName());
      out.println();
      out.append("module: ").append(principal.getModule());
      out.println();
      out.append("storeId: ").append(principal.getStoreId());
      out.println();
    } else {
      out.println();
      out.append("request.getUserPrincipal(): null");
      out.println();
    }

    out.flush();
  }

  protected void writeJsonError(HttpServletRequest request, HttpServletResponse response, int errorStatus,
      Problem problem) throws ServletException, IOException {

    response.setStatus(errorStatus);
    response.setContentType(ContentTypes.APP_JSON_PROBLEM);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());

    var out = response.getWriter();
    jsonProcessor.write(out, problem);

    out.flush();
  }

  protected void writeSimpleHtml(HttpServletRequest request, HttpServletResponse response, int status, String message)
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
