// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.util.Map;

import com.appslandia.common.base.MapAccessor;

import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
public interface Resources extends MapAccessor<String, String> {

  // HTTP Message Keys
  public static final String ERROR_BAD_REQUEST = "errors.bad_request";
  public static final String ERROR_UNAUTHORIZED = "errors.unauthorized";
  public static final String ERROR_FORBIDDEN = "errors.forbidden";
  public static final String ERROR_NOT_FOUND = "errors.not_found";
  public static final String ERROR_METHOD_NOT_ALLOWED = "errors.method_not_allowed";

  public static final String ERROR_PRECONDITION_FAILED = "errors.precondition_failed";
  public static final String ERROR_UNSUPPORTED_MEDIA_TYPE = "errors.unsupported_media_type";
  public static final String ERROR_TOO_MANY_REQUESTS = "errors.too_many_requests";
  public static final String ERROR_INTERNAL_SERVER_ERROR = "errors.internal_server_error";
  public static final String ERROR_SERVICE_UNAVAILABLE = "errors.service_unavailable";

  // Other
  public static final String ERROR_CSRF_FAILED = "errors.csrf_failed";
  public static final String ERROR_CAPTCHA_FAILED = "errors.captcha_failed";

  public static final String ERROR_FIELD_INVALID = "errors.field_invalid";
  public static final String ERROR_FIELD_REQUIRED = "errors.field_required";

  public static final String ERROR_ASYNC_TIMED_OUT = "errors.async_timed_out";

  // ResKeys
  public static final ResKey RES_KEY_FIELDS_INVALID = new ResKey("errors.fields_invalid");

  @Override
  String get(Object key);

  String get(String key, Object... params);

  String get(String key, Map<String, Object> params);

  public static String getMsgKey(int status) {
    return switch (status) {
    case HttpServletResponse.SC_BAD_REQUEST -> ERROR_BAD_REQUEST;
    case HttpServletResponse.SC_UNAUTHORIZED -> ERROR_UNAUTHORIZED;
    case HttpServletResponse.SC_FORBIDDEN -> ERROR_FORBIDDEN;
    case HttpServletResponse.SC_NOT_FOUND -> ERROR_NOT_FOUND;
    case HttpServletResponse.SC_METHOD_NOT_ALLOWED -> ERROR_METHOD_NOT_ALLOWED;
    case HttpServletResponse.SC_PRECONDITION_FAILED -> ERROR_PRECONDITION_FAILED;
    case HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE -> ERROR_UNSUPPORTED_MEDIA_TYPE;
    case TooManyRequestsException.STATUS -> ERROR_TOO_MANY_REQUESTS;
    case HttpServletResponse.SC_INTERNAL_SERVER_ERROR -> ERROR_INTERNAL_SERVER_ERROR;
    case HttpServletResponse.SC_SERVICE_UNAVAILABLE -> ERROR_SERVICE_UNAVAILABLE;
    default -> ERROR_INTERNAL_SERVER_ERROR;
    };
  }
}
