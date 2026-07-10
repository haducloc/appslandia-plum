// Licensed under the MIT License.
// See LICENSE file in the project root for details.

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
    index++;
    request.setAttribute(ServletResponse.class.getName(), response);

    if (index == filters.size()) {
      if (servlet != null) {
        servlet.service(request, response);
      }
    } else {
      filters.get(index).doFilter(request, response, this);
    }
  }

  public MockFilterChain addFilter(Filter filter) {
    filters.add(filter);
    return this;
  }

  public MockFilterChain setServlet(HttpServlet servlet) {
    this.servlet = servlet;
    return this;
  }
}
