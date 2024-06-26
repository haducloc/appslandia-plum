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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.DateUtils;
import com.appslandia.plum.base.CorsPolicyHandler.CorsResult;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.inject.Inject;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class InitializerHandler extends HttpFilter {
  private static final long serialVersionUID = 1L;

  @Inject
  protected AppConfig appConfig;

  @Inject
  protected ResponseEncoderProvider responseEncoderProvider;

  @Inject
  protected LanguageProvider languageProvider;

  @Inject
  protected DefaultHeaderPolicy defaultHeaderPolicy;

  @Inject
  protected HeaderPolicyProvider headerPolicyProvider;

  @Inject
  protected CorsPolicyHandler corsPolicyHandler;

  @Inject
  protected CorsPolicyProvider corsPolicyProvider;

  @Inject
  protected AuthHandlerProvider authHandlerProvider;

  @Inject
  protected AuthorizePolicyProvider authorizePolicyProvider;

  @Inject
  protected ExceptionHandler exceptionHandler;

  @Inject
  protected RequestContextParser requestContextParser;

  @Inject
  protected AccessRateHandler accessRateHandler;

  @Inject
  protected RemoteClientVerifier remoteClientVerifier;

  protected boolean enableEtag(HttpServletRequest request, RequestContext requestContext) {
    return requestContext.getActionDesc().getEnableEtag() != null;
  }

  protected boolean enableCompression(HttpServletRequest request, RequestContext requestContext) {
    return requestContext.getActionDesc() != null && requestContext.getActionDesc().getEnableCompression() != null;
  }

  protected void initialize(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext)
      throws Exception {

    // Default Character Encoding
    if (request.getCharacterEncoding() == null) {
      request.setCharacterEncoding(StandardCharsets.UTF_8.name());
    }

    // Default Header Policies
    this.defaultHeaderPolicy.writePolicy(request, response, requestContext);

    // Enabled Header Policies
    for (String policy : this.appConfig.getStringArray(AppConfig.CONFIG_ENABLE_HEADER_POLICIES)) {
      this.headerPolicyProvider.getHeaderPolicy(policy).writePolicy(request, response, requestContext);
    }

    // Vary
    String[] varyHeaders = this.appConfig.getStringArray(AppConfig.HEADER_VARY);
    Arrays.stream(varyHeaders).forEach(header -> response.addHeader("Vary", header));

    if (enableCompression(request, requestContext)) {
      response.addHeader("Vary", "Accept-Encoding");
    }

    if (this.languageProvider.getLanguages().size() > 1) {
      response.addHeader("Vary", "Accept-Language");
    }
  }

  protected void redirectLang(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext)
      throws Exception {
    int status = HttpServletResponse.SC_MOVED_TEMPORARILY;

    String url = ServletUtils.getRequestUrl(request, requestContext.getLanguageId());
    ServletUtils.sendRedirect(response, this.appConfig.isEnableSession() ? response.encodeRedirectURL(url) : url,
        status);
  }

  @Override
  protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    Asserts.isTrue(request.getDispatcherType().equals(DispatcherType.REQUEST));
    try {
      // RequestContext
      RequestContext requestContext = this.requestContextParser.parse(request, response);

      // Initialize
      initialize(request, response, requestContext);

      // Not Found?
      if (requestContext.getActionDesc() == null) {
        throw new NotFoundException(requestContext.res(Resources.ERROR_NOT_FOUND))
            .setTitleKey(Resources.ERROR_NOT_FOUND);
      }

      // Allow Method?
      if (!requestContext.getActionDesc().getAllowMethods().contains(request.getMethod())) {
        throw new HttpException(HttpServletResponse.SC_METHOD_NOT_ALLOWED,
            requestContext.res(Resources.ERROR_METHOD_NOT_ALLOWED)).setTitleKey(Resources.ERROR_METHOD_NOT_ALLOWED);
      }

      // Consume Type
      if (requestContext.getActionDesc().getConsumeType() != null) {
        if (!ServletUtils.isContentSupported(request.getContentType(),
            requestContext.getActionDesc().getConsumeType().value())) {

          throw new HttpException(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
              requestContext.res(Resources.ERROR_UNSUPPORTED_MEDIA_TYPE))
              .setTitleKey(Resources.ERROR_UNSUPPORTED_MEDIA_TYPE);
        }
      }

      // Language
      if ((requestContext.isCookieLanguage() && (this.languageProvider.getLanguages().size() > 1))
          || (!requestContext.isPathLanguage() && this.appConfig.getBool(AppConfig.CONFIG_ENABLE_PATH_LANG))) {

        if (requestContext.isGetOrHead()) {
          redirectLang(request, response, requestContext);
          return;
        }
      }

      // Allow Client
      if (!this.remoteClientVerifier.allowClient(request)) {
        throw new ForbiddenException(requestContext.res(Resources.ERROR_FORBIDDEN));
      }

      // Check access rate
      this.accessRateHandler.checkRequest(request, requestContext);

      // HTTP OPTIONS
      if (HttpMethod.OPTIONS.equals(request.getMethod())) {
        doOptions(request, response, requestContext);
        return;
      }

      // RequestWrapper
      request = new RequestWrapper(request, requestContext.getPathParamMap());
      if (isMockContext()) {
        request.setAttribute(RequestWrapper.class.getName(), request);
      }

      // ResponseWrapperImpl
      response = new ResponseWrapperImpl(response);

      // Authorize Origin
      String origin = this.corsPolicyHandler.getCrossOrigin(request);
      if (origin != null) {

        // Not @EnableCors
        if (requestContext.getActionDesc().getEnableCors() == null) {
          throw new ForbiddenException(
              requestContext.res(Resources.ERROR_FORBIDDEN_CORS, CorsResult.NOT_ALLOWED_CORS.name()));
        }
        CorsPolicy corsPolicy = this.corsPolicyProvider
            .getCorsPolicy(requestContext.getActionDesc().getEnableCors().value());
        CorsPolicyHandler.CorsResult corsResult = this.corsPolicyHandler.handleCors(request, response, origin,
            corsPolicy);

        if (corsResult != CorsPolicyHandler.CorsResult.ALLOWED) {
          throw new ForbiddenException(requestContext.res(Resources.ERROR_FORBIDDEN_CORS, corsResult.name()));
        }
      }

      // Authorize
      Authorize authorize = requestContext.getActionDesc().getAuthorize();
      if (authorize != null) {
        UserPrincipal principal = ServletUtils.getUserPrincipal(request);

        // Authenticate
        if (principal == null) {
          this.authHandlerProvider.getAuthHandler(requestContext.getModule()).askAuthenticate(request, response,
              requestContext);
          return;
        }

        // No @BypassAuthorization
        if (requestContext.getActionDesc().getBypassAuthorization() == null) {

          // Check Module
          if (!principal.isForModule(requestContext.getModule())) {

            this.authHandlerProvider.getAuthHandler(requestContext.getModule()).askAuthenticate(request, response,
                requestContext);
            return;
          }

          // Authorize
          if (!authorize(request, principal, authorize)) {
            throw new ForbiddenException(requestContext.res(Resources.ERROR_FORBIDDEN))
                .setTitleKey(Resources.ERROR_FORBIDDEN);
          }
        }

        // Re-authenticate
        if (authorize.reauth() && !isReauthenticated(principal)) {
          this.authHandlerProvider.getAuthHandler(requestContext.getModule()).askReauthenticate(request, response,
              requestContext);
          return;
        }
      }

      // Compression (Encoding)
      String compressionType = this.responseEncoderProvider.getBestEncoding(request);
      boolean handleCompression = (compressionType != null) && enableCompression(request, requestContext);

      // ETAG
      if (requestContext.isGetOrHead() && enableEtag(request, requestContext)) {
        ContentResponseWrapper wrapper = new ContentResponseWrapper(response, true);
        chain.doFilter(request, wrapper);

        if ((300 <= response.getStatus()) && (response.getStatus() < 400)) {
          return;
        }
        wrapper.finishWrapper();

        if (ServletUtils.isETagEligible(response)) {

          if (!ServletUtils.checkNotModified(request, response,
              ServletUtils.toEtag(wrapper.getContent().digest("MD5")))) {

            if (handleCompression) {
              ResponseEncoder responseEncoder = this.responseEncoderProvider.getResponseEncoder(compressionType);
              responseEncoder.encode(response, wrapper.getContent());

            } else {
              response.setContentLengthLong(wrapper.getContent().size());
              wrapper.getContent().writeTo(response.getOutputStream());
            }
          }

        } else {
          response.setContentLengthLong(wrapper.getContent().size());
          wrapper.getContent().writeTo(response.getOutputStream());
        }
        return;
      }

      // Compression (Encoding) (No ETAG)
      if (handleCompression) {
        ResponseEncoder responseEncoder = this.responseEncoderProvider.getResponseEncoder(compressionType);
        responseEncoder.encode(request, response, chain);
        return;
      }

      // Next filter?
      chain.doFilter(request, response);

    } catch (Exception ex) {
      this.exceptionHandler.handleException(request, response, ex);
    }
  }

  protected boolean authorize(HttpServletRequest request, UserPrincipal principal, Authorize authorize) {
    String[] roles = authorize.roles();
    String[] policies = authorize.policies();
    if ((roles.length == 0) && (policies.length == 0)) {
      return true;
    }
    if (roles.length > 0) {
      if (Arrays.stream(roles).anyMatch(role -> request.isUserInRole(role))) {
        return true;
      }
    }
    if (policies.length > 0) {
      if (this.authorizePolicyProvider.authorize(principal, policies)) {
        return true;
      }
    }
    return false;
  }

  protected boolean isReauthenticated(UserPrincipal principal) {
    if (principal.getReauthAt() == 0) {
      return false;
    }
    return DateUtils.isFutureTime(principal.getReauthAt() + this.appConfig.getLong(AppConfig.CONFIG_REAUTH_TIMEOUT_MS),
        0);
  }

  protected void doOptions(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext)
      throws Exception {
    String origin = request.getHeader(CorsPolicyHandler.HEADER_ORIGIN);
    if ((origin == null) || (request.getHeader(CorsPolicyHandler.HEADER_AC_REQUEST_METHOD) == null)) {
      throw new ForbiddenException(requestContext.res(Resources.ERROR_FORBIDDEN))
          .setTitleKey(Resources.ERROR_FORBIDDEN);
    }
    CorsPolicy corsPolicy = this.corsPolicyProvider
        .getCorsPolicy(requestContext.getActionDesc().getEnableCors().value());
    CorsPolicyHandler.CorsResult corsResult = this.corsPolicyHandler.handlePreflight(request, response, corsPolicy);

    if (corsResult != CorsPolicyHandler.CorsResult.ALLOWED) {
      throw new ForbiddenException(requestContext.res(Resources.ERROR_FORBIDDEN_CORS, corsResult.name()));
    }
  }

  public boolean isMockContext() {
    return false;
  }
}
