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
public interface RemMePostHandler {

  void onRemMeSuccess(HttpServletRequest request, HttpServletResponse response, String identity, String module)
      throws Exception;
}
