// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.ObjectUtils;
import com.appslandia.common.utils.STR;
import com.appslandia.common.utils.StringUtils;
import com.appslandia.common.utils.URLUtils;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class PrefCookieHandler {

  public static final String CONFIG_COOKIE_NAME = PrefCookieHandler.class.getName() + ".cookie_name";
  public static final String CONFIG_COOKIE_AGE = PrefCookieHandler.class.getName() + ".cookie_age";

  public static final String CONFIG_COOKIE_SECURE = PrefCookieHandler.class.getName() + ".cookie_secure";
  public static final String CONFIG_COOKIE_HTTPONLY = PrefCookieHandler.class.getName() + ".cookie_httponly";

  @Inject
  protected AppConfig appConfig;

  @Inject
  protected AppLogger appLogger;

  protected String cookieName;
  protected int cookieAge;
  protected boolean cookieSecure;
  protected boolean cookieHttpOnly;

  @PostConstruct
  protected void initialize() {
    cookieName = appConfig.getString(CONFIG_COOKIE_NAME, "__pref_cookie");
    cookieAge = appConfig.getInt(CONFIG_COOKIE_AGE, (int) TimeUnit.SECONDS.convert(360, TimeUnit.DAYS));

    cookieSecure = appConfig.getBool(CONFIG_COOKIE_SECURE, true);
    cookieHttpOnly = appConfig.getBool(CONFIG_COOKIE_HTTPONLY, true);

    appLogger.info(STR.fmt("{}: {}", CONFIG_COOKIE_NAME, cookieName));
    appLogger.info(STR.fmt("{}: {}", CONFIG_COOKIE_AGE, cookieAge));
    appLogger.info(STR.fmt("{}: {}", CONFIG_COOKIE_SECURE, cookieSecure));
    appLogger.info(STR.fmt("{}: {}", CONFIG_COOKIE_HTTPONLY, cookieHttpOnly));
  }

  protected String encode(PrefCookie prefCookie) {
    return URLUtils.toQueryParams(ObjectUtils.cast(prefCookie));
  }

  protected PrefCookie decode(String value) {
    value = StringUtils.trimToNull(value);
    if (value == null) {
      return PrefCookie.EMPTY;
    }

    try {
      var map = URLUtils.parseParams(value, new LinkedHashMap<>(), false);
      return new PrefCookie(ObjectUtils.cast(map));

    } catch (IllegalArgumentException ex) {
      return PrefCookie.EMPTY;
    }
  }

  public void savePrefCookie(HttpServletRequest request, HttpServletResponse response, PrefCookie prefCookie) {
    Arguments.notNull(prefCookie);
    var cookieValue = encode(prefCookie);

    if (cookieValue != null) {
      ServletUtils.saveCookie(request, response, cookieName, cookieValue, cookieAge, null, (c) -> {
        c.setSecure(cookieSecure);
        c.setHttpOnly(cookieHttpOnly);
      });
    }
  }

  public PrefCookie loadPrefCookie(HttpServletRequest request, HttpServletResponse response) {
    var cookie = ServletUtils.getCookie(request, cookieName);

    PrefCookie prefCookie = null;
    if (cookie == null) {
      return PrefCookie.EMPTY;

    }
    prefCookie = decode(cookie.getValue());
    if (prefCookie == PrefCookie.EMPTY) {
      ServletUtils.removeCookie(request, response, cookieName, null);
    }
    return prefCookie;
  }
}
