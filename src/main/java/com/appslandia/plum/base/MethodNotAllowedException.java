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

import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
@NotLog
public class MethodNotAllowedException extends HttpException implements HttpHeaderApply {
  private static final long serialVersionUID = 1L;

  private String allow;

  public MethodNotAllowedException() {
    super(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
  }

  public MethodNotAllowedException(String message) {
    super(HttpServletResponse.SC_METHOD_NOT_ALLOWED, message);
  }

  public MethodNotAllowedException(String message, Throwable cause) {
    super(HttpServletResponse.SC_METHOD_NOT_ALLOWED, message, cause);
  }

  public MethodNotAllowedException(Throwable cause) {
    super(HttpServletResponse.SC_METHOD_NOT_ALLOWED, cause);
  }

  public MethodNotAllowedException setAllow(String allow) {
    this.allow = allow;
    return this;
  }

  @Override
  public void apply(HttpServletResponse response) {
    if (allow != null) {
      response.setHeader("Allow", allow);
    }
  }
}
