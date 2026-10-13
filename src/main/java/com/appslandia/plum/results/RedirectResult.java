// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.results;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.URLUtils;
import com.appslandia.plum.base.ActionParser;
import com.appslandia.plum.base.ActionResult;
import com.appslandia.plum.base.RequestContext;
import com.appslandia.plum.base.TempDataManager;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
public class RedirectResult implements ActionResult {

  private final String controller;
  private final String action;
  private final Map<String, Object> params;
  private final int status;

  public RedirectResult(String action) {
    this(action, null, null, HttpServletResponse.SC_MOVED_TEMPORARILY);
  }

  public RedirectResult(String action, String controller) {
    this(action, controller, null, HttpServletResponse.SC_MOVED_TEMPORARILY);
  }

  public RedirectResult(String action, String controller, Map<String, Object> params) {
    this(action, controller, params, HttpServletResponse.SC_MOVED_TEMPORARILY);
  }

  public RedirectResult(String action, String controller, Map<String, Object> params, int status) {
    this.action = Arguments.notNull(action);
    this.controller = controller;
    this.params = (params != null) ? new LinkedHashMap<>(params) : null;
    this.status = status;
  }

  @Override
  public void execute(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext)
      throws Exception {
    var tempDataManager = ServletUtils.getAppScoped(request.getServletContext(), TempDataManager.class);

    // Controller/action
    var controller = this.controller;
    if (controller == null) {
      controller = requestContext.getActionDesc().getController();
    }

    // TempData
    var tempDataId = tempDataManager.saveTempData(request, response);

    // URL
    var actionParser = ServletUtils.getAppScoped(request.getServletContext(), ActionParser.class);
    String url = null;

    if (tempDataId != null) {
      if (params == null) {
        url = actionParser.toActionUrl(request, controller, action,
            Map.of(TempDataManager.PARAM_TEMP_DATA_ID, tempDataId), false);
      } else {
        params.put(TempDataManager.PARAM_TEMP_DATA_ID, tempDataId);
        url = actionParser.toActionUrl(request, controller, action, params, false);
      }
    } else {
      url = actionParser.toActionUrl(request, controller, action, params, false);
    }

    response.sendRedirect(response.encodeRedirectURL(url), status);
  }

  public static RedirectResult fromRelativeUri(String mvcRelativeUri, Map<String, Object> queryParams) {
    Arguments.notNull(mvcRelativeUri);

    if (!URLUtils.isRelativeUri(mvcRelativeUri)) {
      throw new IllegalArgumentException("mvcRelativeUri must be a relative URI: " + mvcRelativeUri);
    }

    var uri = URI.create(mvcRelativeUri);
    var path = uri.getPath();

    if (path == null || path.length() <= 1) {
      throw new IllegalArgumentException(
          "mvcRelativeUri must be '/{controller}' or '/{controller}/{action}': " + mvcRelativeUri);
    }

    var parts = path.substring(1).split("/");
    if (parts.length > 2) {
      throw new IllegalArgumentException(
          "mvcRelativeUri must be '/{controller}' or '/{controller}/{action}': " + mvcRelativeUri);
    }

    var controller = parts[0];
    var action = parts.length == 2 ? parts[1] : "index";

    Map<String, Object> params = null;
    if (uri.getRawQuery() != null) {
      params = URLUtils.parseParams(uri.getRawQuery(), new LinkedHashMap<>());
    }

    if (params == null) {
      return new RedirectResult(action, controller, queryParams);
    }
    if (queryParams == null || queryParams.isEmpty()) {
      return new RedirectResult(action, controller, params);
    }

    params.putAll(queryParams);
    return new RedirectResult(action, controller, params);
  }
}
