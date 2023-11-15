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

package com.appslandia.plum.base;

import java.util.HashMap;
import java.util.Map;

import com.appslandia.common.base.MapAccessor;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.STR;
import com.appslandia.common.utils.StringUtils;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class Resources implements MapAccessor<String, String> {

  // ${0}
  public static final String PARAM_FIELD_DN = "0";

  public static final String ERROR_BAD_REQUEST = "errors.bad_request";
  public static final String ERROR_METHOD_NOT_ALLOWED = "errors.method_not_allowed";
  public static final String ERROR_UNAUTHORIZED = "errors.unauthorized";

  public static final String ERROR_UNSUPPORTED_MEDIA_TYPE = "errors.unsupported_media_type";
  public static final String ERROR_SERVICE_UNAVAILABLE = "errors.service_unavailable";

  public static final String ERROR_NOT_FOUND = "errors.not_found";
  public static final String ERROR_FORBIDDEN = "errors.forbidden";
  public static final String ERROR_FORBIDDEN_CORS = "errors.forbidden_cors";

  public static final String ERROR_INTERNAL_SERVER_ERROR = "errors.internal_server_error";
  public static final String ERROR_PRECONDITION_FAILED = "errors.precondition_failed";

  public static final String ERROR_TOO_MANY_REQUESTS = "errors.too_many_requests";
  public static final String ERROR_MAX_ACCESSES = "errors.max_accesses";
  public static final String ERROR_RATE_LIMIT_1 = "errors.rate_limit_1";
  public static final String ERROR_RATE_LIMIT_2 = "errors.rate_limit_2";

  public static final String ERROR_CSRF_FAILED = "errors.csrf_failed";
  public static final String ERROR_CAPTCHA_FAILED = "errors.captcha_failed";

  public static final String ERROR_FIELD_INVALID = "errors.field_invalid";
  public static final String ERROR_FIELD_REQUIRED = "errors.field_required";

  final String language;
  final Map<String, String> resources = new HashMap<>();

  public Resources(String language) {
    this.language = language;
  }

  public void putResources(Map<String, String> resMap) {
    for (Map.Entry<String, String> res : resMap.entrySet()) {
      if (!StringUtils.isNullOrEmpty(res.getKey())) {
        this.resources.put(res.getKey(), res.getValue());
      }
    }
  }

  public String getLanguage() {
    return this.language;
  }

  @Override
  public String get(Object key) {
    String msg = this.resources.get(key);
    if (msg == null) {
      return this.language + ":" + key;
    }
    return msg;
  }

  @Override
  public String getOrDefault(Object key, String defaultValue) {
    String msg = this.resources.get(key);
    if (msg == null) {
      return defaultValue;
    }
    return msg;
  }

  public String get(String key, Object... params) {
    String msg = this.resources.get(key);
    if (msg == null) {
      return this.language + ":" + key + "[]";
    }
    return STR.format(msg, params);
  }

  public String get(String key, Map<String, Object> params) {
    Asserts.notNull(params);

    String msg = this.resources.get(key);
    if (msg == null) {
      return this.language + ":" + key + "{}";
    }
    return STR.format(msg, params);
  }
}
