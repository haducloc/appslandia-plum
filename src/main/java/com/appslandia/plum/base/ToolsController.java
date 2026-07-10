// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.util.Date;

import com.appslandia.plum.results.ContentResult;
import com.appslandia.plum.utils.DebugUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class ToolsController extends ControllerBase {

  @HttpGet
  public ActionResult toDateTime(long timeInMillis) {
    return new ContentResult(new Date(timeInMillis).toString());
  }

  @HttpGet
  public ActionResult curTimeInMs() {
    return new ContentResult(String.valueOf(System.currentTimeMillis()));
  }

  @HttpGet
  public void requestLocales(HttpServletRequest request, HttpServletResponse response) throws Exception {
    DebugUtils.writeRequestLocales(request, response);
  }

  @HttpGet
  public void sysDateTime(HttpServletRequest request, HttpServletResponse response) throws Exception {
    DebugUtils.writeSysDateTime(request, response);
  }
}
