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

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import com.appslandia.common.base.BaseEncoder;
import com.appslandia.common.utils.ArrayUtils;
import com.appslandia.plum.utils.HeaderUtils;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.Cookie;

/**
 *
 * @author Loc Ha
 *
 */
public class MockServletUtils {

  public static void addHeaderValues(Map<String, MockHttpHeader> headers, String name, String[] values) {
    var header = MockHttpHeader.getByName(headers, name);
    if (header == null) {
      header = new MockHttpHeader();
      headers.put(name, header);
    }
    header.addValues(values);
  }

  public static void setHeaderValues(Map<String, MockHttpHeader> headers, String name, String[] values) {
    var header = MockHttpHeader.getByName(headers, name);
    if (header == null) {
      header = new MockHttpHeader();
      headers.put(name, header);
    }
    header.setValues(values);
  }

  public static long getDateHeader(Map<String, MockHttpHeader> headers, String name) {
    var header = MockHttpHeader.getByName(headers, name);
    var value = (header != null) ? header.getValue() : null;
    if (value != null) {
      return HeaderUtils.parseDateHeader(value);
    }
    return -1L;
  }

  public static int getIntHeader(Map<String, MockHttpHeader> headers, String name) {
    var header = MockHttpHeader.getByName(headers, name);
    var value = (header != null) ? header.getValue() : null;
    if (value != null) {
      return Integer.parseInt(value);
    }
    return -1;
  }

  public static Cookie createCookie(ServletContext sc, String name, String value, int maxAge) {
    return createCookie(sc, name, value, maxAge, false, false, null, null);
  }

  public static Cookie createCookie(ServletContext sc, String name, String value, int maxAge, boolean secure,
      boolean httpOnly, String domain, String path) {
    var cookie = new Cookie(name, value);

    if (domain != null) {
      cookie.setDomain(domain);
    } else if (sc.getSessionCookieConfig().getDomain() != null) {
      cookie.setDomain(sc.getSessionCookieConfig().getDomain());
    }
    if (path != null) {
      cookie.setPath(path);
    } else if (sc.getSessionCookieConfig().getPath() != null) {
      cookie.setPath(sc.getSessionCookieConfig().getPath());
    } else {
      cookie.setPath("/");
    }
    if (maxAge >= 0) {
      cookie.setMaxAge(maxAge);
    }
    cookie.setSecure(secure);
    cookie.setHttpOnly(httpOnly);
    return cookie;
  }

  public static Cookie createSessionCookie(ServletContext sc, String value) {
    var cookie = new Cookie(sc.getSessionCookieConfig().getName(), value);
    if (sc.getSessionCookieConfig().getDomain() != null) {
      cookie.setDomain(sc.getSessionCookieConfig().getDomain());
    }
    cookie.setPath(sc.getSessionCookieConfig().getPath());

    cookie.setSecure(sc.getSessionCookieConfig().isSecure());
    cookie.setHttpOnly(sc.getSessionCookieConfig().isHttpOnly());
    return cookie;
  }

  public static Cookie getCookie(List<Cookie> cookies, String name) {
    for (Cookie cookie : cookies) {
      if (name.equals(cookie.getName())) {
        return cookie;
      }
    }
    return null;
  }

  public static void addParameter(Map<String, String[]> parameterMap, String name, String... moreValues) {
    var values = parameterMap.get(name);
    if (values == null) {
      parameterMap.put(name, moreValues);
    } else {
      parameterMap.put(name, ArrayUtils.append(values, moreValues));
    }
  }

  public static String createBasicCredential(String userName, String password) {
    var credential = userName + ":" + password;
    return BaseEncoder.BASE64.encode(credential.getBytes(StandardCharsets.UTF_8));
  }
}
