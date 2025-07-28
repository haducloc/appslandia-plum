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

import com.appslandia.common.base.LruCache;
import com.appslandia.common.utils.ObjectUtils;
import com.appslandia.plum.utils.ServletUtils;

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

  protected int getCacheSize() {
    return this.appConfig.getInt(CONFIG_CSRF_STORE_SIZE, 3);
  }

  @Override
  protected void saveCsrf(HttpServletRequest request, String csrfId) {
    var session = request.getSession();
    synchronized (ServletUtils.getMutex(session)) {
      LruCache<String, String> cache = ObjectUtils.cast(session.getAttribute(SESSION_ATTRIBUTE_CSRF_STORE));
      if (cache == null) {
        cache = new LruCache<>(getCacheSize());
      }
      cache.put(csrfId, null);
      session.setAttribute(SESSION_ATTRIBUTE_CSRF_STORE, cache);
    }
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
