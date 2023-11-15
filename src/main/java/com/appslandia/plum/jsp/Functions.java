// The MIT License (MIT)
// Copyright © 2015 AppsLandia. All rights reserved.

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

package com.appslandia.plum.jsp;

import com.appslandia.common.base.DeployEnv;
import com.appslandia.common.utils.URLEncoding;
import com.appslandia.common.utils.XmlEscaper;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.servlet.http.HttpServletRequest;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class Functions {

  @Function
  public static String hidden(boolean b) {
    return b ? "hidden=\"hidden\"" : null;
  }

  @Function
  public static String disabled(boolean b) {
    return b ? "disabled=\"disabled\"" : null;
  }

  @Function
  public static String readonly(boolean b) {
    return b ? "readonly=\"readonly\"" : null;
  }

  @Function
  public static String required(boolean b) {
    return b ? "required=\"required\"" : null;
  }

  @Function
  public static String checked(boolean b) {
    return b ? "checked=\"checked\"" : null;
  }

  @Function
  public static String selected(boolean b) {
    return b ? "selected=\"selected\"" : null;
  }

  @Function
  public static String hardWrap(boolean b) {
    return b ? "wrap=\"hard\"" : null;
  }

  @Function
  public static String multiple(boolean b) {
    return b ? "multiple=\"multiple\"" : null;
  }

  @Function
  public static String novalidate(boolean b) {
    return b ? "novalidate=\"novalidate\"" : null;
  }

  @Function
  public static String escXml(Object value) {
    if (value != null) {
      return XmlEscaper.escapeXml(value.toString());
    }
    return null;
  }

  public static String ifEscXml(boolean b, Object value) {
    if (b) {
      if (value != null) {
        return XmlEscaper.escapeXml(value.toString());
      }
    }
    return null;
  }

  @Function
  public static String iifEscXml(boolean b, Object trueValue, Object falseValue) {
    if (b) {
      if (trueValue != null) {
        return XmlEscaper.escapeXml(trueValue.toString());
      }
    } else {
      if (falseValue != null) {
        return XmlEscaper.escapeXml(falseValue.toString());
      }
    }
    return null;
  }

  @Function
  public static String encParam(Object value) {
    if (value != null) {
      return URLEncoding.encodeParam(value.toString());
    }
    return null;
  }

  @Function
  public static String encPath(Object value) {
    if (value != null) {
      return URLEncoding.encodePath(value.toString());
    }
    return null;
  }

  @Function
  public static long nowMs() {
    return System.currentTimeMillis();
  }

  @Function
  public static String envName() {
    return DeployEnv.getCurrent().getName();
  }

  @Function
  public static String ifClass(boolean b, String clazz) {
    if (b) {
      return clazz;
    }
    return TagUtils.CSS_NOOP;
  }

  @Function
  public static boolean hasCookie(HttpServletRequest request, String cookieName) {
    return ServletUtils.getCookieValue(request, cookieName) != null;
  }
}
