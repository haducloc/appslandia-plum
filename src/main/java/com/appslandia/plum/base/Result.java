// Licensed under the MIT License.
// See LICENSE file in the project root for details.

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
    return code;
  }

  public Result setCode(String code) {
    this.code = code;
    return this;
  }

  public String getMessage() {
    return message;
  }

  public Result setMessage(String message) {
    this.message = message;
    return this;
  }

  public String getMsgKey() {
    return msgKey;
  }

  public Result setMsgKey(String msgKey) {
    this.msgKey = msgKey;
    return this;
  }

  public String getLink() {
    return link;
  }

  public Result setLink(String link) {
    this.link = link;
    return this;
  }

  public String getException() {
    return exception;
  }

  public Result setException(Throwable exception) {
    if (exception != null) {
      this.exception = ExceptionUtils.toStackTrace(exception);
    }
    return this;
  }

  public Object getData() {
    return data;
  }

  public Result setData(Object data) {
    this.data = data;
    return this;
  }
}
