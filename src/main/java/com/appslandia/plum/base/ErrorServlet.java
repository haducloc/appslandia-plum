// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.appslandia.common.base.ToStringBuilder;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.ContentTypes;
import com.appslandia.common.utils.ExceptionUtils;
import com.appslandia.common.utils.ObjectUtils;
import com.appslandia.common.utils.STR;
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

  public static final String CONFIG_ERROR_DEV = ErrorServlet.class.getName() + ".error_dev";

  @Inject
  protected AppLogger appLogger;

  @Inject
  protected AppConfig appConfig;

  @Inject
  protected ExceptionHandler exceptionHandler;

  @Inject
  protected HeaderPolicyProvider headerPolicyProvider;

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    appLogger.info(STR.fmt("Initializing '{}', servletName='{}'.", getClass().getName(), config.getServletName()));
  }

  protected void applyHeaders(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext) {
    // Global Headers
    headerPolicyProvider.getHeaderPolicies().forEach(p -> {

      if (p.isAllMatched()) {
        p.writePolicy(request, response, requestContext);
      }
    });

    // HTML
    if (!requestContext.isEnableJsonError()) {

      headerPolicyProvider.getHeaderPolicies().forEach(p -> {

        if (HtmlHeaderPolicy.class.isInstance(p) && p.matches(requestContext.getRelativePath())) {
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
    if (HttpMethod.TRACE.equalsIgnoreCase(request.getMethod())) {
      response.setStatus(problem.getStatus());
      return;
    }

    // JSON | HTML
    if (requestContext.isEnableJsonError()) {
      exceptionHandler.writeJsonError(request, response, problem.getStatus(), problem);
    } else {

      if (appConfig.getBool(CONFIG_ERROR_DEV, false)) {
        writeErrorDev(request, response, problem.getStatus(), requestContext);

      } else {
        try {
          writeErrorProd(request, response, problem.getStatus(), requestContext);

        } catch (Exception ex) {
          appLogger.error(ex);

          if (!response.isCommitted()) {
            response.resetBuffer();

            exceptionHandler.writeSimpleHtml(request, response, problem.getStatus(), problem.getTitle());
          }
        }
      }
    }
  }

  protected Problem resolveProblem(HttpServletRequest request) {
    var requestContext = ServletUtils.getRequestContext(request);

    var status = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
    Asserts.notNull(status, STR.fmt("No request attribute: {}", RequestDispatcher.ERROR_STATUS_CODE));

    var exception = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
    var title = requestContext.res(Resources.getMsgKey(status));

    return new Problem(status, title, null, null, null, null, requestContext.getRelativePath(),
        requestContext.getTraceId(), exception);
  }

  protected String getErrorViewPath(HttpServletRequest request, RequestContext requestContext) {
    return "/error";
  }

  protected void writeErrorProd(HttpServletRequest request, HttpServletResponse response, int errorStatus,
      RequestContext requestContext) throws ServletException, IOException {

    var viewPath = getErrorViewPath(request, requestContext);
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
      out.append("name: ").append(principal.getName());
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
