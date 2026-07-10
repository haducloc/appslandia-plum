// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.utils;

import java.lang.reflect.Field;

import com.appslandia.common.base.ToStringBuilder;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author Loc Ha
 *
 */
public class WebBeanTSPolicy extends ToStringBuilder.TSPolicy {

  final boolean printRequest;
  final boolean printResponse;
  final boolean printSession;
  final boolean printServletContext;

  public WebBeanTSPolicy() {
    this(false, false, false, false);
  }

  public WebBeanTSPolicy(boolean printRequest, boolean printResponse, boolean printSession,
      boolean printServletContext) {
    this.printRequest = printRequest;
    this.printResponse = printResponse;
    this.printSession = printSession;
    this.printServletContext = printServletContext;
  }

  @Override
  public boolean tsIdHash(Field field, Object value) {
    if (super.tsIdHash(field, value)) {
      return true;
    }
    if (value instanceof ServletRequest) {
      return !printRequest;
    }
    if (value instanceof ServletResponse) {
      return !printResponse;
    }
    if (value instanceof HttpSession) {
      return !printSession;
    }
    if (value instanceof ServletContext) {
      return !printServletContext;
    }
    if (value.getClass().getName().endsWith("_$$_Weld")) {
      return true;
    }
    return false;
  }
}
