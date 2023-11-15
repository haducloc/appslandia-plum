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
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
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
    CacheResult cacheResult = context.getMethod().getAnnotation(CacheResult.class);
    if (cacheResult == null) {
      return context.proceed();
    }
    Asserts.isTrue(!cacheResult.cacheName().isEmpty());
    Asserts.isTrue(!cacheResult.key().isEmpty());

    // Build cacheKey
    String cacheKey = CacheUtils.toKey(cacheResult.key(), context.getParameters());

    // AppCache
    AppCache<String, Object> cache = this.appCacheManager.getRequiredCache(cacheResult.cacheName());

    // Get value
    Object value = cache.get(cacheKey);
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
