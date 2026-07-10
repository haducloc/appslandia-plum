// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
@FunctionalInterface
public interface ActionResult {

  void execute(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext)
      throws Exception;

  public static ActionResult EMPTY = new ActionResult() {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext)
        throws Exception {
    }
  };
}
