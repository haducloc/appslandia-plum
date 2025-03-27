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

package com.appslandia.plum.mocks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServlet;

/**
 *
 * @author Loc Ha
 *
 */
public class MockFilterChain implements FilterChain {

  private List<Filter> filters = new ArrayList<>();
  private HttpServlet servlet;
  private int index = -1;

  @Override
  public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
    this.index++;
    request.setAttribute(ServletResponse.class.getName(), response);

    if (this.index == this.filters.size()) {
      if (this.servlet != null) {
        this.servlet.service(request, response);
      }
    } else {
      this.filters.get(this.index).doFilter(request, response, this);
    }
  }

  public MockFilterChain addFilter(Filter filter) {
    this.filters.add(filter);
    return this;
  }

  public MockFilterChain setServlet(HttpServlet servlet) {
    this.servlet = servlet;
    return this;
  }
}
