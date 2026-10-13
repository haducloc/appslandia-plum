// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.caching;

import com.appslandia.common.caching.AppCache;
import com.appslandia.common.caching.AppCacheManager;
import com.appslandia.common.cdi.CDIEventListener;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.inject.Inject;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class CacheChangeListener implements CDIEventListener<CacheChange> {

  @Inject
  protected AppCacheManager appCacheManager;

  @Override
  public void onEvent(@Observes CacheChange event) {
    clearCache(event);
  }

  void clearCache(CacheChange event) {
    AppCache<Object, Object> cache = appCacheManager.getCache(event.getCacheName());

    if (event.getKeys().isEmpty()) {
      cache.clear();
    } else {
      for (String key : event.getKeys()) {
        cache.remove(key);
      }
    }
  }

  @Override
  public void onEventAsync(@ObservesAsync CacheChange event) {
    throw new UnsupportedOperationException();
  }
}
