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
public class TooManyRequestsException extends HttpException implements HttpHeaderApply {
  private static final long serialVersionUID = 1L;

  public static final int SC_TOO_MANY_REQUESTS = 429;

  private String reason;
  private Long retryAfter;

  public TooManyRequestsException() {
    super(SC_TOO_MANY_REQUESTS);
  }

  public TooManyRequestsException(String message) {
    super(SC_TOO_MANY_REQUESTS, message);
  }

  public TooManyRequestsException(String message, Throwable cause) {
    super(SC_TOO_MANY_REQUESTS, message, cause);
  }

  public TooManyRequestsException(Throwable cause) {
    super(SC_TOO_MANY_REQUESTS, cause);
  }

  @Override
  public void apply(HttpServletResponse response) {
    if (this.reason != null) {
      response.setHeader("TMR-Reason", this.reason);
    }
    if (this.retryAfter != null) {
      response.setDateHeader("Retry-After", this.retryAfter);
    }
  }

  public String getReason() {
    return this.reason;
  }

  public TooManyRequestsException setReason(String reason) {
    this.reason = reason;
    return this;
  }

  public Long getRetryAfter() {
    return this.retryAfter;
  }

  public TooManyRequestsException setRetryAfter(Long retryAfter) {
    this.retryAfter = retryAfter;
    return this;
  }

  @Override
  public TooManyRequestsException setTitleKey(String titleKey) {
    super.setTitleKey(titleKey);
    return this;
  }

  @Override
  public TooManyRequestsException setTitleKey(ResKey titleKey) {
    super.setTitleKey(titleKey);
    return this;
  }

  @Override
  public TooManyRequestsException setDetailKey(String detailKey) {
    super.setDetailKey(detailKey);
    return this;
  }

  @Override
  public TooManyRequestsException setDetailKey(ResKey detailKey) {
    super.setDetailKey(detailKey);
    return this;
  }
}
