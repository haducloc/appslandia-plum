// The MIT License (MIT)
// Copyright © 2015 AppsLandia. All rights reserved.

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

import java.util.Map;

import com.appslandia.plum.base.ActionResult;
import com.appslandia.plum.base.RequestContext;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public abstract class ViewResult implements ActionResult {

  protected final String path;
  protected final Map<String, Object> model;
  protected String resolvedPath;

  public ViewResult() {
    this(null, null);
  }

  public ViewResult(String path) {
    this(path, null);
  }

  public ViewResult(Map<String, Object> model) {
    this(null, model);
  }

  public ViewResult(String path, Map<String, Object> model) {
    this.path = path;
    this.model = model;
  }

  public abstract String getSuffix();

  protected abstract String getViewDir(ServletContext servletContext);

  @Override
  public void execute(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext)
      throws Exception {
    String viewDir = getViewDir(request.getServletContext());
    StringBuilder viewBase = new StringBuilder(viewDir.length() + 80).append(viewDir);

    // Build resolvedPath
    if (this.path == null) {
      this.resolvedPath = viewBase.append("/").append(requestContext.getActionDesc().getController()).append("/")
          .append(requestContext.getActionDesc().getAction()).append(getSuffix()).toString();

    } else {
      this.resolvedPath = viewBase.append(this.path).append(getSuffix()).toString();
    }

    doExecute(request, response, requestContext);
  }

  protected abstract void doExecute(HttpServletRequest request, HttpServletResponse response,
      RequestContext requestContext) throws Exception;
}
