// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import jakarta.servlet.http.HttpServletRequest;

/**
 *
 * @author Loc Ha
 *
 */
public interface InitialRequestGuard {

  boolean allowRequest(HttpServletRequest request, RequestContext context);
}
