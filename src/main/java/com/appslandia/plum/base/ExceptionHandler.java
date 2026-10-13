// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.io.IOException;

import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.ValueUtils;
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
          requestContext.getHandlingPath(), requestContext.getTraceId(), exception);
    }

    var status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
    var title = requestContext.res(Resources.getMsgKey(status));

    return new Problem(status, title, null, null, null, null, requestContext.getHandlingPath(),
        requestContext.getTraceId(), exception);
  }
}
