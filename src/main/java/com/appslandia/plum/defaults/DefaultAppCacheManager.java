// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.defaults;

import com.appslandia.common.caching.AppCache;
import com.appslandia.common.caching.AppCacheManager;

import jakarta.enterprise.context.ApplicationScoped;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class DefaultAppCacheManager implements AppCacheManager {

  @Override
  public <K, V> AppCache<K, V> getCache(String cacheName) {
    throw new UnsupportedOperationException("DefaultAppCacheManager.getCache(cacheName)");
  }

  @Override
  public boolean clearCache(String cacheName) {
    throw new UnsupportedOperationException("DefaultAppCacheManager.clearCache(cacheName)");
  }

  @Override
  public boolean destroyCache(String cacheName) {
    throw new UnsupportedOperationException("DefaultAppCacheManager.destroyCache(cacheName)");
  }

  @Override
  public Iterable<String> getCacheNames() {
    throw new UnsupportedOperationException("DefaultAppCacheManager.getCacheNames()");
  }

  @Override
  public void close() {
    throw new UnsupportedOperationException("DefaultAppCacheManager.close()");
  }
}
