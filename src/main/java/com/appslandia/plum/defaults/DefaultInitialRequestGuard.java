// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.defaults;

import com.appslandia.plum.base.InitialRequestGuard;
import com.appslandia.plum.base.RequestContext;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletRequest;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class DefaultInitialRequestGuard implements InitialRequestGuard {

  @Override
  public boolean allowRequest(HttpServletRequest request, RequestContext context) {
    return true;
  }
}
