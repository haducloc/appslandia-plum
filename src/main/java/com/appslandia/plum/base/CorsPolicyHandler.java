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

  public static final String HEADER_AC_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
  public static final String HEADER_AC_ALLOW_METHODS = "Access-Control-Allow-Methods";
  public static final String HEADER_AC_ALLOW_HEADERS = "Access-Control-Allow-Headers";
  public static final String HEADER_AC_EXPOSE_HEADERS = "Access-Control-Expose-Headers";

  public static final String HEADER_AC_MAX_AGE = "Access-Control-Max-Age";
  public static final String HEADER_AC_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";

  public static final String HEADER_ORIGIN = "Origin";
  public static final String HEADER_VARY = "Vary";

  public static final String HEADER_AC_REQUEST_METHOD = "Access-Control-Request-Method";
  public static final String HEADER_AC_REQUEST_HEADERS = "Access-Control-Request-Headers";

  public String getCrossOrigin(HttpServletRequest request) {
    var origin = request.getHeader(HEADER_ORIGIN);
    if (origin == null) {
      return null;
    }
    var url = ServletUtils.absHostBase(request);

    if (origin.equals(url.toString())) {
      return null;
    }
    return origin;
  }

  public CorsResult handleCors(HttpServletRequest request, HttpServletResponse response, String crossOrigin,
      CorsPolicy corsPolicy) {
    // Origin
    if (!corsPolicy.allowOrigin(crossOrigin)) {
      return CorsResult.NOT_ALLOWED_ORIGIN;
    }

    // Allow-Origin
    response.setHeader(HEADER_AC_ALLOW_ORIGIN, corsPolicy.getAllowOrigin(crossOrigin));
    if (corsPolicy.isVaryOrigin()) {
      response.addHeader(HEADER_VARY, HEADER_ORIGIN);
    }

    // Expose-Headers
    if (corsPolicy.getExposeHeadersString() != null) {
      response.setHeader(HEADER_AC_EXPOSE_HEADERS, corsPolicy.getExposeHeadersString());
    }
    return CorsResult.ALLOWED;
  }

  public CorsResult handlePreflight(HttpServletRequest request, HttpServletResponse response, CorsPolicy corsPolicy) {
    // CORS Headers
    var origin = Asserts.notNull(getCrossOrigin(request));

    // Origin
    if (!corsPolicy.allowOrigin(origin)) {
      return CorsResult.NOT_ALLOWED_ORIGIN;
    }
    var requestContext = ServletUtils.getRequestContext(request);

    // Request Method
    var requestMethod = Asserts.notNull(request.getHeader(HEADER_AC_REQUEST_METHOD));
    if (!requestContext.getActionDesc().getAllowMethods().contains(requestMethod)) {
      return CorsResult.NOT_ALLOWED_METHOD;
    }

    // Request Headers
    var requestHeaders = request.getHeader(HEADER_AC_REQUEST_HEADERS);
    if (requestHeaders != null) {
      if (!corsPolicy.allowHeaders(requestHeaders)) {
        return CorsResult.NOT_ALLOWED_HEADER;
      }
    }

    // Allow-Origin
    response.setHeader(HEADER_AC_ALLOW_ORIGIN, corsPolicy.getAllowOrigin(origin));
    if (corsPolicy.isVaryOrigin()) {
      response.addHeader(HEADER_VARY, HEADER_ORIGIN);
    }

    // Allow-Methods
    response.setHeader(HEADER_AC_ALLOW_METHODS, requestContext.getActionDesc().getMethodsAsString());

    // Allow-Headers
    if (corsPolicy.getAllowHeadersAsString() != null) {
      response.setHeader(HEADER_AC_ALLOW_HEADERS, corsPolicy.getAllowHeadersAsString());
    }

    // Allow-Credentials
    if (corsPolicy.isAllowCredentials()) {
      response.setHeader(HEADER_AC_ALLOW_CREDENTIALS, Boolean.toString(corsPolicy.isAllowCredentials()));
    }

    // Max-Age
    response.setHeader(HEADER_AC_MAX_AGE, Integer.toString(corsPolicy.getMaxAge()));
    return CorsResult.ALLOWED;
  }

  public static enum CorsResult {
    ALLOWED, NOT_ALLOWED_ORIGIN, NOT_ALLOWED_METHOD, NOT_ALLOWED_HEADER, NOT_ALLOWED_CORS
  }
}
