// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.defaults;

import com.appslandia.common.utils.StringUtils;
import com.appslandia.plum.base.AppConfig;
import com.appslandia.plum.base.AppLogger;
import com.appslandia.plum.base.TraceIdParser;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class DefaultTraceIdParser implements TraceIdParser {

  private static final String X_TRACE_ID_HEADER = "X-Trace-Id";
  public static final String CONFIG_USE_X_TRACE_ID_HEADER = "config.use_x_trace_id_header";

  @Inject
  protected AppConfig appConfig;

  @Inject
  protected AppLogger appLogger;

  protected boolean xTraceIdHeader;

  @PostConstruct
  protected void initialize() {
    xTraceIdHeader = appConfig.getBool(CONFIG_USE_X_TRACE_ID_HEADER, false);

    appLogger.info("{}: {}", CONFIG_USE_X_TRACE_ID_HEADER, xTraceIdHeader);
  }

  @Override
  public String parse(HttpServletRequest request) {
    if (xTraceIdHeader) {
      var traceId = StringUtils.trimToNull(request.getHeader(X_TRACE_ID_HEADER));

      if (traceId == null) {
        throw new IllegalStateException("Missing required header: " + X_TRACE_ID_HEADER);
      }
      return traceId;
    }
    return request.getRequestId();
  }
}
