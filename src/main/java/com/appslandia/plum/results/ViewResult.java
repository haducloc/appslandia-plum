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

import java.io.IOException;
import java.util.Map;

import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.Asserts;
import com.appslandia.plum.base.ActionResult;
import com.appslandia.plum.base.AppConfig;
import com.appslandia.plum.base.RequestContext;
import com.appslandia.plum.base.ViewHandlerProvider;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
public class ViewResult implements ActionResult {

  protected final String path;
  protected final Map<String, Object> model;

  public ViewResult() {
    this(null, null);
  }

  /**
   * Constructs a {@code ViewResult} with the specified view path.
   *
   * @param path the view path, which must start with "/", excluding the view file extension (e.g., ".jsp", ".xhtml").
   */
  public ViewResult(String path) {
    this(path, null);
  }

  /**
   * Constructs a {@code ViewResult} with the specified view model.
   *
   * @param model the model to be passed to the view.
   */
  public ViewResult(Map<String, Object> model) {
    this(null, model);
  }

  /**
   * Constructs a {@code ViewResult} with the specified view path and model.
   *
   * @param path  the view path, which must start with "/", excluding the view file extension (e.g., ".jsp", ".xhtml").
   * @param model the model to be passed to the view.
   */
  public ViewResult(String path, Map<String, Object> model) {
    if (path != null) {
      Arguments.isTrue(path.startsWith("/"), "The given path must start with '/'.");
    }

    this.path = path;
    this.model = model;
  }

  protected StringBuilder getViewBase(AppConfig appConfig) {
    return appConfig.getViewBase();
  }

  @Override
  public void execute(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext)
      throws Exception {

    // viewBase
    var appConfig = ServletUtils.getAppScoped(request, AppConfig.class);
    var viewBase = getViewBase(appConfig);
    var baseLen = viewBase.length();

    String viewLocation = null;
    String viewSuffix = null;

    var path = this.path;
    if (path == null) {
      path = "/" + requestContext.getActionDesc().getController() + "/" + requestContext.getActionDesc().getAction();
    }

    for (String suffix : appConfig.getViewSuffixes()) {
      viewLocation = viewBase.append(path).append(suffix).toString();

      if (request.getServletContext().getResource(viewLocation) != null) {
        viewSuffix = suffix;
        break;
      } else {
        viewBase.setLength(baseLen);
      }
    }

    // ViewHandler
    Asserts.notNull(viewSuffix, "Could not resolve a view suffix for path '{}'", path);

    // ViewHandlerProvider
    var viewHandlerProvider = ServletUtils.getAppScoped(request, ViewHandlerProvider.class);
    var viewHandler = viewHandlerProvider.getViewHandler(viewSuffix);

    viewHandler.execute(viewLocation, this.model, request, response, requestContext);
  }

  public static final ViewResult DEFAULT = new ViewResult();

  public static String resolveViewLocation(String viewPath, HttpServletRequest request) throws IOException {

    // viewBase
    var appConfig = ServletUtils.getAppScoped(request, AppConfig.class);
    var viewBase = appConfig.getViewBase();
    var lenBase = viewBase.length();

    // ViewHandlerProvider
    var viewHandlerProvider = ServletUtils.getAppScoped(request, ViewHandlerProvider.class);

    String viewLocation = null;
    for (String suffix : viewHandlerProvider.getViewSuffixes()) {
      viewLocation = viewBase.append(viewPath).append(suffix).toString();

      if (request.getServletContext().getResource(viewLocation) != null) {
        return viewLocation;
      }
      viewBase.setLength(lenBase);
    }
    return null;
  }
}
