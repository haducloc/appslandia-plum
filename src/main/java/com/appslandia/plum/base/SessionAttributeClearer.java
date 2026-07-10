// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import jakarta.servlet.http.HttpSession;

/**
 *
 * @author Loc Ha
 *
 */
public interface SessionAttributeClearer {

  void clearSession(HttpSession session);
}
