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

import jakarta.servlet.http.Cookie;

/**
 *
 * @author Loc Ha
 *
 */
public class CookieImpl extends Cookie {
  private static final long serialVersionUID = 1L;

  private static final String SAME_SITE = "SameSite";

  public CookieImpl(String name, String value) {
    super(name, value);
  }

  public SameSite getSameSite() {
    String value = getAttribute(SAME_SITE);
    return (value != null) ? SameSite.valueOf(value) : null;
  }

  public void setSameSite(SameSite sameSite) {
    setAttribute(SAME_SITE, sameSite != null ? sameSite.value() : null);
  }

  public enum SameSite {
    STRICT("Strict"), LAX("Lax"), NONE("None");

    final String value;

    private SameSite(String value) {
      this.value = value;
    }

    public String value() {
      return this.value;
    }
  }
}
