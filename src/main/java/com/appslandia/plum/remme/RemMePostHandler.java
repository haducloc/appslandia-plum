// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.remme;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
public interface RemMePostHandler {

  void onRemMeSuccess(HttpServletRequest request, HttpServletResponse response, String module, String identity)
      throws Exception;
}
