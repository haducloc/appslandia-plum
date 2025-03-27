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

import com.appslandia.common.utils.ExceptionUtils;

/**
 *
 * @author Loc Ha
 *
 */
public class Result {

  private String code;
  private String message;
  private String msgKey;

  private String link;
  private String exception;
  private Object data;

  public Result setCode(int code) {
    return setCode(Integer.toString(code));
  }

  public String getCode() {
    return this.code;
  }

  public Result setCode(String code) {
    this.code = code;
    return this;
  }

  public String getMessage() {
    return this.message;
  }

  public Result setMessage(String message) {
    this.message = message;
    return this;
  }

  public String getMsgKey() {
    return this.msgKey;
  }

  public Result setMsgKey(String msgKey) {
    this.msgKey = msgKey;
    return this;
  }

  public String getLink() {
    return this.link;
  }

  public Result setLink(String link) {
    this.link = link;
    return this;
  }

  public String getException() {
    return this.exception;
  }

  public Result setException(Throwable exception) {
    if (exception != null) {
      this.exception = ExceptionUtils.toStackTrace(exception);
    }
    return this;
  }

  public Object getData() {
    return this.data;
  }

  public Result setData(Object data) {
    this.data = data;
    return this;
  }
}
