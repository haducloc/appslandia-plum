// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.defaults;

import com.appslandia.plum.remme.RemMePostHandler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class DefaultRemMePostHandler implements RemMePostHandler {

  @Override
  public void onRemMeSuccess(HttpServletRequest request, HttpServletResponse response, String module, String identity)
      throws Exception {
  }
}
