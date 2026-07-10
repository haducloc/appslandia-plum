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
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
public abstract class SessionTempDataManager extends TempDataManager {

  public static final String SESSION_ATTRIBUTE_TEMP_DATA_STORE = "__temp_data_store";

  public static final String CONFIG_TEMP_DATA_STORE_SIZE = SessionTempDataManager.class.getName() + ".store_size";

  @Inject
  protected AppConfig appConfig;

  @Inject
  protected AppLogger appLogger;

  private int cacheSize;

  @PostConstruct
  protected void initialize() {
    cacheSize = appConfig.getInt(CONFIG_TEMP_DATA_STORE_SIZE, 3);

    appLogger.info(STR.fmt("{}: {}", CONFIG_TEMP_DATA_STORE_SIZE, cacheSize));
  }

  protected String generateTempDataId() {
    return UUID.randomUUID().toString();
  }

  @Override
  protected String doSaveTempData(HttpServletRequest request, HttpServletResponse response, TempData tempData) {
    var session = request.getSession();
    var tempDataId = generateTempDataId();

    synchronized (ServletUtils.getMutex(session)) {
      LruCache<String, TempData> cache = ObjectUtils.cast(session.getAttribute(SESSION_ATTRIBUTE_TEMP_DATA_STORE));
      if (cache == null) {
        cache = new LruCache<>(cacheSize);
      }
      cache.put(tempDataId, tempData);
      session.setAttribute(SESSION_ATTRIBUTE_TEMP_DATA_STORE, cache);
    }
    return tempDataId;
  }

  @Override
  protected TempData doLoadTempData(HttpServletRequest request, HttpServletResponse response, String tempDataId) {
    var session = request.getSession(false);
    if (session == null) {
      return null;
    }
    synchronized (ServletUtils.getMutex(session)) {
      LruCache<String, TempData> cache = ObjectUtils.cast(session.getAttribute(SESSION_ATTRIBUTE_TEMP_DATA_STORE));
      if (cache != null) {

        var tempData = cache.remove(tempDataId);
        if (tempData != null) {
          session.setAttribute(SESSION_ATTRIBUTE_TEMP_DATA_STORE, cache);
        }
        return tempData;
      }
      return null;
    }
  }
}
