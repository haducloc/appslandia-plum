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

import com.appslandia.common.base.UncheckedException;
import com.appslandia.common.utils.NormalizeUtils;
import com.appslandia.plum.base.AppLogger.LogLevel;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.inject.Inject;
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
public class AppFilterHandler extends HttpFilter {
  private static final long serialVersionUID = 1L;

  @Inject
  protected AppConfig appConfig;

  @Inject
  @AccessLog
  protected AppLogger accessLogger;

  @Inject
  protected AppPolicyProvider appPolicyProvider;

  @Inject
  protected RequestContextParser requestContextParser;

  @Inject
  protected ExceptionHandler exceptionHandler;

  @Inject
  protected InitialRequestGuard initialRequestGuard;

  @Override
  protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    final var start = System.nanoTime();
    final var requestContext = requestContextParser.initRequestContext(request, response);

    try {
      // DEBUG
      if (appConfig.isEnableDebug()) {
        ServletUtils.testErrorStatus(request, "__test_error_status");
        ServletUtils.testOutStream(request, response, "__test_out_stream");
      }

      // Header Policies
      appPolicyProvider.getHeaderPolicies().forEach(p -> {

        if (p.matches(requestContext.getRelativePath())) {
          p.writePolicy(request, response, requestContext);
        }
      });

      // Accept-Encoding
      if (requestContext.getActionDesc() != null) {
        if (appPolicyProvider.getCompressPolicies().stream()
            .anyMatch(p -> p.matches(requestContext.getRelativePath()))) {
          response.setHeader("Vary", "Accept-Encoding");
        }
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
        var actionDesc = requestContext.getActionDesc();
        var controller = (actionDesc != null) ? actionDesc.getController() : null;
        var action = (actionDesc != null) ? actionDesc.getAction() : null;

        var problem = (Problem) request.getAttribute(Problem.class.getName());
        var status = (problem != null) ? problem.getStatus() : response.getStatus();
        var userAgent = getUserAgent(request, 512);

        accessLogger.info(String.format(
            "HTTP %s %s | module=%s | controller=%s | action=%s | status=%d | duration=%d ms | client-ip=%s | client-ua=%s",
            request.getMethod(), requestContext.getRelativePath(), requestContext.getModule(), controller, action,
            status, durationMs, requestContext.getClientIp(), userAgent));
      }
    }
  }

  static String getUserAgent(HttpServletRequest request, int maxLen) {
    var ua = request.getHeader("User-Agent");
    ua = NormalizeUtils.normalizeString(ua);

    if (ua != null && ua.length() > maxLen) {
      ua = ua.substring(0, maxLen);
    }
    return ua;
  }
}
