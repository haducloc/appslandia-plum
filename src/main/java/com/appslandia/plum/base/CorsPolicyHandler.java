// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import com.appslandia.common.utils.Asserts;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class CorsPolicyHandler {

  public CorsResult handleRequest(HttpServletRequest request, HttpServletResponse response, String crossOrigin,
      CorsPolicy corsPolicy) {
    // Origin
    if (!corsPolicy.allowOrigin(crossOrigin)) {
      return CorsResult.NOT_ALLOWED_ORIGIN;
    }

    // Allow-Origin
    response.setHeader(CorsPolicy.HEADER_AC_ALLOW_ORIGIN, corsPolicy.isAnyOrigin() ? CorsPolicy.CORS_ANY : crossOrigin);
    if (!corsPolicy.isAnyOrigin()) {
      response.addHeader(CorsPolicy.HEADER_VARY, CorsPolicy.HEADER_ORIGIN);
    }

    // Allow-Credentials
    if (!corsPolicy.isAnyOrigin() && corsPolicy.isAllowCredentials()) {
      response.setHeader(CorsPolicy.HEADER_AC_ALLOW_CREDENTIALS, Boolean.toString(true));
    }

    // Expose-Headers
    if (corsPolicy.getExposeHeadersString() != null) {
      response.setHeader(CorsPolicy.HEADER_AC_EXPOSE_HEADERS, corsPolicy.getExposeHeadersString());
    }
    return CorsResult.ALLOWED;
  }

  public CorsResult handlePreflight(HttpServletRequest request, HttpServletResponse response, String origin,
      CorsPolicy corsPolicy) {
    // Origin
    if (!corsPolicy.allowOrigin(origin)) {
      return CorsResult.NOT_ALLOWED_ORIGIN;
    }
    var requestContext = ServletUtils.getRequestContext(request);

    // Request Method
    var requestMethod = Asserts.notNull(request.getHeader(CorsPolicy.HEADER_AC_REQUEST_METHOD));
    if (!requestContext.getActionDesc().getHttpMethods().contains(requestMethod)) {
      return CorsResult.NOT_ALLOWED_METHOD;
    }

    // Request Headers
    var requestHeaders = request.getHeader(CorsPolicy.HEADER_AC_REQUEST_HEADERS);
    if (requestHeaders != null) {
      if (!corsPolicy.allowHeaders(requestHeaders)) {
        return CorsResult.NOT_ALLOWED_HEADER;
      }
    }

    // Allow-Origin
    response.setHeader(CorsPolicy.HEADER_AC_ALLOW_ORIGIN, corsPolicy.isAnyOrigin() ? CorsPolicy.CORS_ANY : origin);
    if (!corsPolicy.isAnyOrigin()) {
      response.addHeader(CorsPolicy.HEADER_VARY, CorsPolicy.HEADER_ORIGIN);
    }

    // Allow-Methods
    response.setHeader(CorsPolicy.HEADER_AC_ALLOW_METHODS, requestContext.getActionDesc().getMethodsAsString());

    // Allow-Headers
    if (corsPolicy.getAllowHeadersAsString() != null) {
      response.setHeader(CorsPolicy.HEADER_AC_ALLOW_HEADERS, corsPolicy.getAllowHeadersAsString());
    }

    // Allow-Credentials
    if (!corsPolicy.isAnyOrigin() && corsPolicy.isAllowCredentials()) {
      response.setHeader(CorsPolicy.HEADER_AC_ALLOW_CREDENTIALS, Boolean.toString(true));
    }

    // Max-Age
    response.setHeader(CorsPolicy.HEADER_AC_MAX_AGE, Integer.toString(corsPolicy.getMaxAge()));
    return CorsResult.ALLOWED;
  }

  public static enum CorsResult {
    ALLOWED, NOT_ALLOWED_ORIGIN, NOT_ALLOWED_METHOD, NOT_ALLOWED_HEADER, NO_CORS_POLICY
  }
}
