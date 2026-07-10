// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.defaults;

import com.appslandia.common.utils.StringUtils;
import com.appslandia.plum.base.AppConfig;
import com.appslandia.plum.base.TraceIdParser;

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

  @Inject
  protected AppConfig appConfig;

  @Override
  public String parse(HttpServletRequest request) {
    if (appConfig.getBool("config.trace_id_header", false)) {

      var traceId = StringUtils.trimToNull(request.getHeader("X-Trace-Id"));
      if (traceId == null) {
        throw new IllegalStateException("No X-Trace-Id header value.");
      }
      return traceId;
    }
    return request.getRequestId();
  }
}
