// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
@Controller("csp-report")
public class CspReportController extends ControllerBase {

  @Inject
  protected CspReportToHandler cspReportToHandler;

  @HttpPost
  @ConsumeType("application/csp-report")
  public void index(HttpRequestFacade request, HttpServletResponse response) throws Exception {
    cspReportToHandler.handle(request.getReader());
  }
}
