// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.results;

import java.util.LinkedHashMap;
import java.util.Map;

import com.appslandia.common.utils.Arguments;
import com.appslandia.plum.base.ActionParser;
import com.appslandia.plum.base.ActionResult;
import com.appslandia.plum.base.AppConfig;
import com.appslandia.plum.base.LanguageProvider;
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

  private String controller;
  private String action;
  private Map<String, Object> params;
  private int status = HttpServletResponse.SC_MOVED_TEMPORARILY;

  public RedirectResult() {
  }

  public RedirectResult(String action) {
    this.action = action;
  }

  public RedirectResult(String action, String controller) {
    this.action = action;
    this.controller = controller;
  }

  public RedirectResult status(int status) {
    this.status = status;
    return this;
  }

  public RedirectResult query(String name, Object value) {
    getParams().put(name, value);
    return this;
  }

  private Map<String, Object> getParams() {
    if (params == null) {
      params = new LinkedHashMap<>();
    }
    return params;
  }

  @Override
  public void execute(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext)
      throws Exception {
    var appConfig = ServletUtils.getAppScoped(request, AppConfig.class);
    var tempDataManager = ServletUtils.getAppScoped(request, TempDataManager.class);

    // Controller/action
    Arguments.notNull(action);
    if (controller == null) {
      controller = requestContext.getActionDesc().getController();
    }

    // TempData
    var tempDataId = tempDataManager.saveTempData(request, response);
    if (tempDataId != null) {
      query(TempDataManager.PARAM_TEMP_DATA_ID, tempDataId);
    }

    // URL
    var actionParser = ServletUtils.getAppScoped(request, ActionParser.class);
    var url = actionParser.toActionUrl(request, controller, action, params, false);

    var targetUrl = appConfig.isEnableSession() ? response.encodeRedirectURL(url) : url;
    response.sendRedirect(targetUrl, status);
  }

  public static final ActionResult INDEX = new ActionResult() {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext)
        throws Exception {
      var appConfig = ServletUtils.getAppScoped(request, AppConfig.class);
      var tempDataManager = ServletUtils.getAppScoped(request, TempDataManager.class);
      var languageProvider = ServletUtils.getAppScoped(request, LanguageProvider.class);

      // URL
      var url = new StringBuilder();
      url.append(request.getContextPath());

      // Language
      if (languageProvider.isMultiLanguages()) {
        url.append('/').append(requestContext.getLanguageId());
      }

      // Controller
      url.append('/').append(requestContext.getActionDesc().getController());

      // TempData
      var tempDataId = tempDataManager.saveTempData(request, response);
      if (tempDataId != null) {
        url.append('?').append(TempDataManager.PARAM_TEMP_DATA_ID).append('=').append(tempDataId);
      }

      var targetUrl = appConfig.isEnableSession() ? response.encodeRedirectURL(url.toString()) : url.toString();
      response.sendRedirect(targetUrl);
    }
  };

  public static final ActionResult ROOT = new ActionResult() {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext)
        throws Exception {
      var appConfig = ServletUtils.getAppScoped(request, AppConfig.class);
      var tempDataManager = ServletUtils.getAppScoped(request, TempDataManager.class);
      var languageProvider = ServletUtils.getAppScoped(request, LanguageProvider.class);

      // URL
      var url = new StringBuilder();
      url.append(request.getContextPath());

      // Language
      if (languageProvider.isMultiLanguages()) {
        url.append('/').append(requestContext.getLanguageId());
      }

      // TempData
      var tempDataId = tempDataManager.saveTempData(request, response);
      if (tempDataId != null) {
        url.append('?').append(TempDataManager.PARAM_TEMP_DATA_ID).append('=').append(tempDataId);
      }

      var targetUrl = appConfig.isEnableSession() ? response.encodeRedirectURL(url.toString()) : url.toString();
      response.sendRedirect(targetUrl);
    }
  };
}
