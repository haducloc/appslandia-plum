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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
public abstract class ActionFilterChain {

  final String[] filters;
  final ActionFilterProvider actionFilterProvider;

  private int index = -1;

  public ActionFilterChain(String[] filters, ActionFilterProvider actionFilterProvider) {
    this.filters = filters;
    this.actionFilterProvider = actionFilterProvider;
  }

  public void doFilter(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext)
      throws Exception {
    this.index++;

    if (this.index == this.filters.length) {
      execute(request, response, requestContext);

    } else {
      var filterName = this.filters[this.index];
      var actionFilter = this.actionFilterProvider.getActionFilter(filterName);

      actionFilter.doFilter(request, response, requestContext, this);
    }
  }

  protected abstract void execute(HttpServletRequest request, HttpServletResponse response,
      RequestContext requestContext) throws Exception;
}
