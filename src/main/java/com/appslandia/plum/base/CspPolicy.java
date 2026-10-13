// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.util.LinkedHashMap;
import java.util.Map;

import com.appslandia.common.utils.Arguments;

/**
 *
 * @author Loc Ha
 *
 */
public class CspPolicy {

  private String csp;
  private String reportingEndpoints;

  final Map<String, String> envParams = new LinkedHashMap<>();

  public CspPolicy setCsp(String multilineCsp) {
    csp = multilineCsp;
    return this;
  }

  public CspPolicy setReportingEndpoints(String multilineReportingEndpoints) {
    reportingEndpoints = multilineReportingEndpoints;
    return this;
  }

  public CspPolicy setEnvParams(String env, String multilineEnvParams) {
    envParams.put(env, multilineEnvParams);
    return this;
  }

  public void registerTo(HtmlHeaderPolicy htmlPolicy) {
    Arguments.notNull(csp, "csp is required.");

    htmlPolicy.setDirectiveHeader("Content-Security-Policy", csp, envParams, ';');

    if (reportingEndpoints != null) {
      htmlPolicy.setDirectiveHeader("Reporting-Endpoints", reportingEndpoints, envParams, ',');
    }
  }
}
