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
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean clearCache(String cacheName) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean destroyCache(String cacheName) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Iterable<String> getCacheNames() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void close() {
    throw new UnsupportedOperationException();
  }
}
