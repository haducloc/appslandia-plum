// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.util.UUID;

import com.appslandia.common.base.LruCache;
import com.appslandia.common.utils.ObjectUtils;
import com.appslandia.common.utils.STR;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;

/**
 *
 * @author Loc Ha
 *
 */
public abstract class SessionCsrfManager extends SimpleCsrfManager {

  public static final String SESSION_ATTRIBUTE_CSRF_STORE = "__csrf_store";

  public static final String CONFIG_CSRF_STORE_SIZE = SessionCsrfManager.class.getName() + ".store_size";

  @Inject
  protected AppConfig appConfig;

  @Inject
  protected AppLogger appLogger;

  private int cacheSize;

  @PostConstruct
  protected void initialize() {
    cacheSize = appConfig.getInt(CONFIG_CSRF_STORE_SIZE, 3);

    appLogger.info(STR.fmt("{}: {}", CONFIG_CSRF_STORE_SIZE, cacheSize));
  }

  protected String generateCsrfId() {
    return UUID.randomUUID().toString();
  }

  @Override
  protected String saveCsrf(HttpServletRequest request) {
    var session = request.getSession();
    var csrfId = generateCsrfId();

    synchronized (ServletUtils.getMutex(session)) {
      LruCache<String, String> cache = ObjectUtils.cast(session.getAttribute(SESSION_ATTRIBUTE_CSRF_STORE));
      if (cache == null) {
        cache = new LruCache<>(cacheSize);
      }
      cache.put(csrfId, null);
      session.setAttribute(SESSION_ATTRIBUTE_CSRF_STORE, cache);
    }
    return csrfId;
  }

  @Override
  public boolean verifyCsrf(HttpServletRequest request, String csrfId, boolean remove) {
    var session = request.getSession(false);
    if (session == null) {
      return false;
    }
    synchronized (ServletUtils.getMutex(session)) {
      LruCache<String, String> cache = ObjectUtils.cast(session.getAttribute(SESSION_ATTRIBUTE_CSRF_STORE));
      if (cache == null) {
        return false;
      }
      var hasCsrf = cache.contains(csrfId);

      if (hasCsrf && remove) {
        cache.remove(csrfId);
        session.setAttribute(SESSION_ATTRIBUTE_CSRF_STORE, cache);
      }
      return hasCsrf;
    }
  }
}
