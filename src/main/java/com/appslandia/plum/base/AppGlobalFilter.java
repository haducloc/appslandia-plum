// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.io.IOException;

import com.appslandia.common.base.UncheckedException;
import com.appslandia.common.utils.Asserts;
import com.appslandia.plum.base.AppLogger.LogLevel;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.inject.Inject;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
public class AppGlobalFilter extends HttpFilter {
  private static final long serialVersionUID = 1L;

  @Inject
  protected AppConfig appConfig;

  @Inject
  @AccessLog
  protected AppLogger accessLogger;

  @Inject
  protected ExceptionHandler exceptionHandler;

  @Inject
  protected HeaderPolicyProvider headerPolicyProvider;

  @Inject
  protected RequestContextParser requestContextParser;

  @Inject
  protected InitialRequestGuard initialRequestGuard;

  @Inject
  protected HttpSessionIdDigester httpSessionIdDigester;

  @Override
  protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    Asserts.isTrue(request.getDispatcherType() == DispatcherType.REQUEST);

    final var start = System.nanoTime();
    final var requestContext = requestContextParser.initRequestContext(request, response);
    final var actionDesc = requestContext.getActionDesc();

    try {
      // DEBUG
      if (appConfig.isEnableDebug()) {
        ServletUtils.testErrorStatus(request, "__test_error_status");
      }

      // Header Policies
      headerPolicyProvider.getHeaderPolicies().forEach(p -> {

        if (p.matches(requestContext.getRelativePath())) {
          p.writePolicy(request, response, requestContext);
        }
      });

      // Accept-Encoding
      if (actionDesc != null && actionDesc.getEnableCompress() != null) {
        response.setHeader("Vary", "Accept-Encoding");
      }

      // Initial Request Guard
      if (!initialRequestGuard.allowRequest(request, requestContext)) {
        throw new ForbiddenException(requestContext.res(Resources.ERROR_FORBIDDEN));
      }

      // Next filter
      chain.doFilter(request, response);

    } catch (Exception ex) {

      var handleEx = (ex instanceof UncheckedException uncheckedEx) ? uncheckedEx.getCause() : ex;
      exceptionHandler.handleException(request, response, handleEx);

    } finally {
      if (accessLogger.isLoggable(LogLevel.INFO)) {

        var durationMs = (System.nanoTime() - start) / 1_000_000;
        var controller = (actionDesc != null) ? actionDesc.getController() : null;
        var action = (actionDesc != null) ? actionDesc.getAction() : null;
        var async = request.isAsyncStarted();

        var problem = (Problem) request.getAttribute(Problem.class.getName());
        var status = (problem != null) ? problem.getStatus() : response.getStatus();
        var userAgent = ServletUtils.getNormUserAgent(request);
        var sessionIdDigest = httpSessionIdDigester.getSessionIdDigest(request);

        // @formatter:off
        accessLogger.info(String.format(
            "HTTP %s %s | status=%d | duration=%d ms | module=%s | controller=%s | action=%s | client-ip=%s | sessionId=%s | traceId=%s | remote-user=%s | client-ua=%s | async=%b",
            request.getMethod(),
            requestContext.getRelativePath(),
            status,
            durationMs,
            requestContext.getModule(),
            controller,
            action,
            requestContext.getClientAddress(),
            sessionIdDigest,
            requestContext.getTraceId(),
            request.getRemoteUser(),
            userAgent,
            async));
        // @formatter:on
      }
    }
  }
}
