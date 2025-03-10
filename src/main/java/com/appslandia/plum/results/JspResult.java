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

import com.appslandia.plum.base.AppConfig;
import com.appslandia.plum.base.RequestContext;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class JspResult extends ViewResult {

  public JspResult() {
    super();
  }

  public JspResult(String path) {
    super(path);
  }

  public JspResult(Map<String, Object> model) {
    super(model);
  }

  public JspResult(String path, Map<String, Object> model) {
    super(path, model);
  }

  @Override
  public String getSuffix() {
    return ".jsp";
  }

  @Override
  protected String getViewDir(ServletContext servletContext) {
    AppConfig appConfig = ServletUtils.getAppScoped(servletContext, AppConfig.class);
    return appConfig.getJspDir();
  }

  @Override
  protected void doExecute(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext)
      throws Exception {
    if (this.model != null) {
      for (Map.Entry<String, Object> variable : this.model.entrySet()) {
        request.setAttribute(variable.getKey(), variable.getValue());
      }
    }
    RequestDispatcher requestDispatcher = ServletUtils.getRequestDispatcher(request, this.resolvedPath);

    if (requestContext.getActionDesc().getChildAction() == null) {
      requestDispatcher.forward(request, response);
    } else {
      requestDispatcher.include(request, response);
    }
  }

  public static final JspResult DEFAULT = new JspResult();
}
