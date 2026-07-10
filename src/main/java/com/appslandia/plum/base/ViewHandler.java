// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
public interface ViewHandler {

  void execute(String viewLocation, Map<String, Object> model, HttpServletRequest request, HttpServletResponse response,
      RequestContext requestContext) throws Exception;
}
