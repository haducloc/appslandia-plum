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

package com.appslandia.plum.base;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.ObjectUtils;
import com.appslandia.common.utils.StringUtils;
import com.appslandia.common.utils.URLUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@ApplicationScoped
public class PrefCookieHandler {

  public static final String DEFAULT_COOKIE_NAME = "pref";
  public static final int DEFAULT_COOKIE_AGE = (int) TimeUnit.SECONDS.convert(360, TimeUnit.DAYS);

  public static final String CONFIG_COOKIE_NAME = PrefCookieHandler.class.getName() + ".cookie_name";
  public static final String CONFIG_COOKIE_AGE = PrefCookieHandler.class.getName() + ".cookie_age";

  @Inject
  protected AppConfig appConfig;

  @Inject
  protected CookieHandler cookieHandler;

  protected String getCookieName() {
    return this.appConfig.getString(CONFIG_COOKIE_NAME, DEFAULT_COOKIE_NAME);
  }

  protected int getCookieAge() {
    return this.appConfig.getInt(CONFIG_COOKIE_AGE, DEFAULT_COOKIE_AGE);
  }

  protected String encode(PrefCookie prefCookie) {
    return URLUtils.toQueryParams(ObjectUtils.cast(prefCookie));
  }

  protected PrefCookie decode(String prefCookie) {
    try {
      Map<String, Object> map = URLUtils.parseParams(prefCookie, new LinkedHashMap<>(), false);
      return new PrefCookie(Collections.unmodifiableMap(ObjectUtils.cast(map)));

    } catch (IllegalArgumentException ex) {
      return PrefCookie.EMPTY;
    }
  }

  public void savePrefCookie(HttpServletRequest request, HttpServletResponse response, PrefCookie prefCookie) {
    Asserts.notNull(prefCookie);
    String cookieValue = encode(prefCookie);

    if (StringUtils.isNullOrEmpty(cookieValue)) {
      if (this.cookieHandler.getCookieValue(request, getCookieName()) != null) {
        this.cookieHandler.removeCookie(response, getCookieName());
      }
    } else {
      this.cookieHandler.saveCookie(response, getCookieName(), cookieValue, getCookieAge(), c -> c.setHttpOnly(false));
    }
  }

  public PrefCookie loadPrefCookie(HttpServletRequest request, HttpServletResponse response) {
    String cookieValue = this.cookieHandler.getCookieValue(request, getCookieName());
    PrefCookie prefCookie = null;

    if (cookieValue == null) {
      prefCookie = PrefCookie.EMPTY;
    } else {
      prefCookie = decode(cookieValue);

      if (prefCookie == PrefCookie.EMPTY) {
        this.cookieHandler.removeCookie(response, getCookieName());
      }
    }
    request.setAttribute(PrefCookie.REQUEST_ATTRIBUTE_ID, prefCookie);
    return prefCookie;
  }

  public void savePrefCookie(HttpServletRequest request, HttpServletResponse response, Consumer<PrefCookie> consumer) {
    PrefCookie prefCookie = (PrefCookie) request.getAttribute(PrefCookie.REQUEST_ATTRIBUTE_ID);
    PrefCookie newPrefCookie = (prefCookie != null) ? prefCookie.clone() : new PrefCookie();
    consumer.accept(newPrefCookie);

    savePrefCookie(request, response, newPrefCookie);
  }
}
