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

import com.appslandia.common.base.Params;
import com.appslandia.common.threading.LazyValue;
import com.appslandia.common.utils.STR;
import com.appslandia.common.utils.StringFormat;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * F
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class ConfigHeaderHandler {

  @Inject
  protected AppConfig appConfig;

  final LazyValue<StringFormat> cspFormat = new LazyValue<>();

  public void writeHeaders(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext) {

    // HSTS
    var scheme = ServletUtils.getScheme(request);
    if ("https".equals(scheme)) {

      var headerValue = this.appConfig.getString(AppConfig.HEADER_STRICT_TRANSPORT_SECURITY);
      if (headerValue != null) {
        response.setHeader("Strict-Transport-Security", headerValue);
      }
    }

    // X-Content-Type-Options
    var headerValue = this.appConfig.getString(AppConfig.HEADER_X_CONTENT_TYPE_OPTIONS);
    if (headerValue != null) {
      response.setHeader("X-Content-Type-Options", headerValue);
    }

    // X-Frame-Options
    headerValue = this.appConfig.getString(AppConfig.HEADER_X_FRAME_OPTIONS);
    if (headerValue != null) {
      response.setHeader("X-Frame-Options", headerValue);
    }

    // X-XSS-Protection
    headerValue = this.appConfig.getString(AppConfig.HEADER_X_XSS_PROTECTION);
    if (headerValue != null) {
      response.setHeader("X-XSS-Protection", headerValue);
    }

    // Content-Security-Policy
    var cspValue = this.appConfig.getString(AppConfig.HEADER_CONTENT_SECURITY_POLICY);
    if (cspValue != null) {

      // {nonce}
      var cspFmt = this.cspFormat.get(() -> STR.compile(cspValue));
      var csp = cspFmt.format(new Params().set("nonce", requestContext.getCspNonce()));

      if (this.appConfig.isCspRptOnly()) {
        response.setHeader("Content-Security-Policy-Report-Only", csp);
      } else {
        response.setHeader("Content-Security-Policy", csp);
      }
    }

    // Referrer-Policy
    headerValue = this.appConfig.getString(AppConfig.HEADER_REFERRER_POLICY);
    if (headerValue != null) {
      response.setHeader("Referrer-Policy", headerValue);
    }

    // Report-To
    headerValue = this.appConfig.getString(AppConfig.HEADER_REPORT_TO);
    if (headerValue != null) {
      response.setHeader("Report-To", headerValue);
    }

    // Reporting-Endpoints
    headerValue = this.appConfig.getString(AppConfig.HEADER_REPORTING_ENDPOINTS);
    if (headerValue != null) {
      response.setHeader("Reporting-Endpoints", headerValue);
    }

    // Cross-Origin-Embedder-Policy
    headerValue = this.appConfig.getString(AppConfig.HEADER_CROSS_ORIGIN_EMBEDDER_POLICY);
    if (headerValue != null) {
      response.setHeader("Cross-Origin-Embedder-Policy", headerValue);
    }

    // Cross-Origin-Opener-Policy
    headerValue = this.appConfig.getString(AppConfig.HEADER_CROSS_ORIGIN_OPENER_POLICY);
    if (headerValue != null) {
      response.setHeader("Cross-Origin-Opener-Policy", headerValue);
    }

    // Cross-Origin-Resource-Policy
    headerValue = this.appConfig.getString(AppConfig.HEADER_CROSS_ORIGIN_RESOURCE_POLICY);
    if (headerValue != null) {
      response.setHeader("Cross-Origin-Resource-Policy", headerValue);
    }

    // Permissions-Policy
    headerValue = this.appConfig.getString(AppConfig.HEADER_PERMISSIONS_POLICY);
    if (headerValue != null) {
      response.setHeader("Permissions-Policy", headerValue);
    }
  }
}
