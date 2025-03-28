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

package com.appslandia.plum.base;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.appslandia.common.base.InitializeObject;
import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.MimeTypes;
import com.appslandia.plum.pebble.PebbleUtils;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
public class ViewHandlerProvider extends InitializeObject {

  private Map<String, ViewHandler> viewHandlerMap = new HashMap<>();

  @Override
  protected void init() throws Exception {
    Map<String, ViewHandler> viewHandlerMap = this.viewHandlerMap;

    viewHandlerMap.putIfAbsent(".jsp", new ForwardViewHandler());
    viewHandlerMap.putIfAbsent(".jspx", new ForwardViewHandler());
    viewHandlerMap.putIfAbsent(".xhtml", new ForwardViewHandler());
    viewHandlerMap.putIfAbsent(".peb", new PebbleViewHandler());

    this.viewHandlerMap = Collections.unmodifiableMap(viewHandlerMap);
  }

  public ViewHandler getViewHandler(String viewSuffix) {
    this.initialize();
    ViewHandler impl = this.viewHandlerMap.get(viewSuffix);
    return Arguments.notNull(impl);
  }

  public void addViewHandler(String viewSuffix, ViewHandler impl) {
    this.assertNotInitialized();
    this.viewHandlerMap.put(viewSuffix, impl);
  }

  public Set<String> getViewSuffixes() {
    this.initialize();
    return this.viewHandlerMap.keySet();
  }

  static class ForwardViewHandler implements ViewHandler {

    @Override
    public void execute(String viewLocation, Map<String, Object> model, HttpServletRequest request,
        HttpServletResponse response, RequestContext requestContext) throws Exception {

      if (model != null) {
        for (Map.Entry<String, Object> variable : model.entrySet()) {
          request.setAttribute(variable.getKey(), variable.getValue());
        }
      }

      // requestDispatcher
      RequestDispatcher requestDispatcher = ServletUtils.getRequestDispatcher(request, viewLocation);
      requestDispatcher.forward(request, response);
    }
  }

  static class PebbleViewHandler implements ViewHandler {

    @Override
    public void execute(String viewLocation, Map<String, Object> model, HttpServletRequest request,
        HttpServletResponse response, RequestContext requestContext) throws Exception {

      // FIXED
      response.setContentType(MimeTypes.TEXT_HTML);
      response.setCharacterEncoding(StandardCharsets.UTF_8.name());

      PebbleUtils.executePebble(request, response, response.getWriter(), viewLocation, model,
          requestContext.getLanguage().getLocale());
    }
  }
}
