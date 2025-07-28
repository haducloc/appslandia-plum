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
public abstract class SessionCaptchaManager extends SimpleCaptchaManager {

  public static final String SESSION_ATTRIBUTE_CAPTCHA_STORE = "__captcha_store";

  public static final String CONFIG_CAPTCHA_STORE_SIZE = SessionCaptchaManager.class.getName() + ".store_size";

  @Inject
  protected AppConfig appConfig;

  protected int getCacheSize() {
    return this.appConfig.getInt(CONFIG_CAPTCHA_STORE_SIZE, 3);
  }

  @Override
  protected void saveCaptcha(HttpServletRequest request, String captchaId, String captchaText) {
    var session = request.getSession();
    synchronized (ServletUtils.getMutex(session)) {
      LruCache<String, String> cache = ObjectUtils.cast(session.getAttribute(SESSION_ATTRIBUTE_CAPTCHA_STORE));
      if (cache == null) {
        cache = new LruCache<>(getCacheSize());
      }
      cache.put(captchaId, captchaText);
      session.setAttribute(SESSION_ATTRIBUTE_CAPTCHA_STORE, cache);
    }
  }

  @Override
  public String getCaptchaText(HttpServletRequest request, String captchaId) {
    var session = request.getSession(false);
    if (session == null) {
      return null;
    }
    synchronized (ServletUtils.getMutex(session)) {
      LruCache<String, String> cache = ObjectUtils.cast(session.getAttribute(SESSION_ATTRIBUTE_CAPTCHA_STORE));
      if (cache == null) {
        return null;
      }
      return cache.get(captchaId);
    }
  }

  @Override
  protected boolean doVerifyCaptcha(HttpServletRequest request, String captchaId, String captchaText) {
    var session = request.getSession(false);
    if (session == null) {
      return false;
    }
    synchronized (ServletUtils.getMutex(session)) {
      LruCache<String, String> cache = ObjectUtils.cast(session.getAttribute(SESSION_ATTRIBUTE_CAPTCHA_STORE));
      if (cache == null) {
        return false;
      }
      var words = cache.get(captchaId);
      if (words == null) {
        return false;
      }
      var hasCaptcha = captchaText.equalsIgnoreCase(words);
      if (hasCaptcha) {
        cache.remove(captchaId);
        session.setAttribute(SESSION_ATTRIBUTE_CAPTCHA_STORE, cache);
      }
      return hasCaptcha;
    }
  }
}
