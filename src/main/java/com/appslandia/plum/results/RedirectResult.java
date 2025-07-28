// The MIT License (MIT)
// Copyright © 2015 Loc Ha

// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:

// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.

// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

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
    if (this.params == null) {
      this.params = new LinkedHashMap<>();
    }
    return this.params;
  }

  @Override
  public void execute(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext)
      throws Exception {
    var appConfig = ServletUtils.getAppScoped(request, AppConfig.class);
    var tempDataManager = ServletUtils.getAppScoped(request, TempDataManager.class);

    // Controller/action
    Arguments.notNull(this.action);
    if (this.controller == null) {
      this.controller = requestContext.getActionDesc().getController();
    }

    // TempData
    var tempDataId = tempDataManager.saveTempData(request, response);
    if (tempDataId != null) {
      query(TempDataManager.PARAM_TEMP_DATA_ID, tempDataId);
    }

    // URL
    var actionParser = ServletUtils.getAppScoped(request, ActionParser.class);
    var url = actionParser.toActionUrl(request, this.controller, this.action, this.params, false);
    sendRedirect(response, url, appConfig.isEnableSession());
  }

  protected void sendRedirect(HttpServletResponse response, String url, boolean enableSession) throws Exception {
    if (this.status == HttpServletResponse.SC_MOVED_TEMPORARILY) {
      response.sendRedirect(enableSession ? response.encodeRedirectURL(url) : url);
    } else {
      ServletUtils.sendRedirect(response, enableSession ? response.encodeRedirectURL(url) : url, this.status);
    }
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
      response.sendRedirect(appConfig.isEnableSession() ? response.encodeRedirectURL(url.toString()) : url.toString());
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
      response.sendRedirect(appConfig.isEnableSession() ? response.encodeRedirectURL(url.toString()) : url.toString());
    }
  };
}
