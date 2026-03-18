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

import com.appslandia.common.base.MapAccessor;

/**
 *
 * @author Loc Ha
 *
 */
public interface Resources extends MapAccessor<String, String> {

  public static final String ERROR_BAD_REQUEST = "errors.bad_request";
  public static final String ERROR_METHOD_NOT_ALLOWED = "errors.method_not_allowed";
  public static final String ERROR_UNAUTHORIZED = "errors.unauthorized";

  public static final String ERROR_UNSUPPORTED_MEDIA_TYPE = "errors.unsupported_media_type";
  public static final String ERROR_SERVICE_UNAVAILABLE = "errors.service_unavailable";

  public static final String ERROR_NOT_FOUND = "errors.not_found";
  public static final String ERROR_FORBIDDEN = "errors.forbidden";
  public static final String ERROR_FORBIDDEN_CORS = "errors.forbidden_cors";

  public static final String ERROR_INTERNAL_SERVER_ERROR = "errors.internal_server_error";
  public static final String ERROR_REQUEST_HANDLING_ERROR = "errors.request_handling_error";

  public static final String ERROR_PRECONDITION_FAILED = "errors.precondition_failed";
  public static final String ERROR_TOO_MANY_REQUESTS = "errors.too_many_requests";

  public static final String ERROR_CSRF_FAILED = "errors.csrf_failed";
  public static final String ERROR_CAPTCHA_FAILED = "errors.captcha_failed";

  public static final String ERROR_FIELD_INVALID = "errors.field_invalid";
  public static final String ERROR_FIELD_REQUIRED = "errors.field_required";

  @Override
  String get(Object key);

  String get(String key, Object... params);
}
