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

    htmlPolicy.setHeader("Content-Security-Policy", csp, envParams, ';');

    if (reportingEndpoints != null) {
      htmlPolicy.setHeader("Reporting-Endpoints", reportingEndpoints, envParams, '\0');
    }
  }
}
