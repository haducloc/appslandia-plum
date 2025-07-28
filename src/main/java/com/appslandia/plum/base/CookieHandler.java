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

import java.util.function.Consumer;

import com.appslandia.plum.utils.ServletUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class CookieHandler {

  @Inject
  protected ServletContext servletContext;

  public void saveCookie(HttpServletResponse response, String cookieName, String cookieValue, int maxAge,
      Consumer<Cookie> cookieInit) {
    var cookie = new Cookie(cookieName, cookieValue);
    cookie.setMaxAge(maxAge);

    var domain = this.servletContext.getSessionCookieConfig().getDomain();
    if (domain != null) {
      cookie.setDomain(domain);
    }
    cookie.setPath(ServletUtils.getCookiePath(this.servletContext));

    if (cookieInit != null) {
      cookieInit.accept(cookie);
    }

    // SameSite
    if (cookie.getAttribute("SameSite") == null) {
      cookie.setAttribute("SameSite", "Lax");
    }
    response.addCookie(cookie);
  }

  public void removeCookie(HttpServletResponse response, String cookieName) {
    ServletUtils.removeCookie(response, cookieName, this.servletContext.getSessionCookieConfig().getDomain(),
        ServletUtils.getCookiePath(this.servletContext));
  }

  public String getCookieValue(HttpServletRequest request, String cookieName) {
    return ServletUtils.getCookieValue(request, cookieName);
  }
}
