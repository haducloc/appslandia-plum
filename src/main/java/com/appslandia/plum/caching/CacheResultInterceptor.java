// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.caching;

import java.io.Serializable;

import com.appslandia.common.caching.AppCache;
import com.appslandia.common.caching.AppCacheManager;
import com.appslandia.common.utils.Asserts;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

/**
 *
 * @author Loc Ha
 *
 */
@CacheResult
@Interceptor
@Priority(Interceptor.Priority.LIBRARY_BEFORE + 150)
public class CacheResultInterceptor implements Serializable {
  private static final long serialVersionUID = 1L;

  @Inject
  protected AppCacheManager appCacheManager;

  @AroundInvoke
  public Object intercept(InvocationContext context) throws Exception {

    // @CacheResult
    var cacheResult = context.getMethod().getAnnotation(CacheResult.class);
    if (cacheResult == null) {
      return context.proceed();
    }
    Asserts.isTrue(!cacheResult.cacheName().isEmpty());
    Asserts.isTrue(!cacheResult.key().isEmpty());

    // Build cacheKey
    var cacheKey = CacheUtils.toKey(cacheResult.key(), context.getParameters());

    // AppCache
    AppCache<String, Object> cache = appCacheManager.getCache(cacheResult.cacheName());

    // Get value
    var value = cache.get(cacheKey);
    if (value != null) {
      return value;
    }

    // Invoke method
    value = context.proceed();

    if (value == null) {
      return value;
    }
    cache.put(cacheKey, value);
    return value;
  }
}
