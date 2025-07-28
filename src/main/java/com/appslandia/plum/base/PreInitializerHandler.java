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

import java.io.IOException;

import com.appslandia.common.base.AppLogger;
import com.appslandia.common.utils.STR;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
@Priority(Integer.MIN_VALUE)
@WebFilter(filterName = "PreInitializerHandler", urlPatterns = "/*", dispatcherTypes = {
    DispatcherType.REQUEST }, asyncSupported = true)
public class PreInitializerHandler extends HttpFilter {
  private static final long serialVersionUID = 1L;

  @Inject
  protected AppConfig appConfig;

  @Inject
  protected AppLogger appLogger;

  @Inject
  protected RequestContextParser requestContextParser;

  @Override
  public void init(FilterConfig config) throws ServletException {
    super.init(config);
    this.appLogger.info(STR.fmt(
        "Initializing '{}', filterName='{}', urlPatterns='/*', dispatcherTypes=DispatcherType.REQUEST, asyncSupported=true.",
        getClass().getName(), config.getFilterName()));
  }

  @Override
  protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    // RequestContext
    this.requestContextParser.initRequestContext(request, response);

    // DEBUG
    if (this.appConfig.isEnableDebug()) {
      testError(request, "__test_error_status");
      ServletUtils.testOutStream(request, response, "__test_out_stream");
    }

    // Next filter
    chain.doFilter(request, response);
  }

  static void testError(HttpServletRequest request, String parameterName) throws ServletException {
    var value = request.getParameter(parameterName);
    if (value != null) {
      throw new ServletException("PreInitializerHandler: This is a test exception.");
    }
  }
}
