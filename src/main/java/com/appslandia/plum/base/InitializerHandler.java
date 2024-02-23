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

import com.appslandia.common.logging.AppLogger;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.DateUtils;
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
  protected AppLogger appLogger;

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
  protected RateLimitHandler rateLimitHandler;

  @Inject
  protected RemoteClientVerifier remoteClientVerifier;

  protected void initialize(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext)
      throws Exception {
    if (request.getCharacterEncoding() == null) {
      request.setCharacterEncoding(StandardCharsets.UTF_8.name());
    }
    for (String policy : this.appConfig.getStringArray(AppConfig.CONFIG_HEADER_POLICIES)) {
      this.headerPolicyProvider.getHeaderPolicy(policy).writePolicy(request, response, requestContext);
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
        if (!ServletUtils.allowContentType(request.getContentType(),
            requestContext.getActionDesc().getConsumeType().value())) {
          throw new HttpException(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
              requestContext.res(Resources.ERROR_UNSUPPORTED_MEDIA_TYPE))
              .setTitleKey(Resources.ERROR_UNSUPPORTED_MEDIA_TYPE);
        }
      }

      // Language
      if (!requestContext.isPathLanguage() && this.appConfig.getBool(AppConfig.CONFIG_REQUIRE_PATH_LANG)) {
        if (!requestContext.isGetOrHead()) {
          throw new BadRequestException(requestContext.res(Resources.ERROR_BAD_REQUEST));
        }
        redirectLang(request, response, requestContext);
        return;
      }

      // Allow Client
      if (!this.remoteClientVerifier.allowClient(request)) {
        throw new ForbiddenException(requestContext.res(Resources.ERROR_FORBIDDEN));
      }

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
          throw new ForbiddenException(requestContext.res(Resources.ERROR_FORBIDDEN_CORS, "No @EnableCors"));
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

        // Not Modules.APP
        if (!Modules.APP.equalsIgnoreCase(requestContext.getModule())) {

          // Check Module
          if (!principal.getModule().equalsIgnoreCase(requestContext.getModule())) {
            throw new ForbiddenException(requestContext.res(Resources.ERROR_FORBIDDEN))
                .setTitleKey(Resources.ERROR_FORBIDDEN);
          }

          // REAUTHENTICATE
          if (authorize.reauth() && !isReauthenticated(principal)) {
            this.authHandlerProvider.getAuthHandler(requestContext.getModule()).askReauthenticate(request, response,
                requestContext);
            return;
          }

          // Authorize
          if (!authorize(request, principal, authorize)) {
            throw new ForbiddenException(requestContext.res(Resources.ERROR_FORBIDDEN))
                .setTitleKey(Resources.ERROR_FORBIDDEN);
          }
        }
      }

      // Rate Limit
      this.rateLimitHandler.checkRequest(request, requestContext);

      // GZIP
      final boolean gzipContent = (requestContext.getActionDesc().getEnableGzip() != null)
          && ServletUtils.isGzipAccepted(request);

      // VARY: Accept-Encoding
      if (requestContext.getActionDesc().getEnableGzip() != null) {
        response.addHeader("Vary", "Accept-Encoding");
      }

      // ETAG
      if ((requestContext.getActionDesc().getEnableEtag() != null) && requestContext.isGetOrHead()) {
        ContentResponseWrapper wrapper = new ContentResponseWrapper(response, true, gzipContent);
        chain.doFilter(request, wrapper);

        if ((300 <= response.getStatus()) && (response.getStatus() < 400)) {

          // If the cacheControl contains 'no-store', skip ETag
          String cacheControl = response.getHeader("Cache-Control");
          if (cacheControl != null && cacheControl.contains("no-store")) {
            return;
          }
        }
        wrapper.finishWrapper();

        if (!ServletUtils.checkNotModified(request, response,
            ServletUtils.toEtag(wrapper.getContent().digest("MD5")))) {
          response.setContentLengthLong(wrapper.getContent().size());
          wrapper.getContent().writeTo(response.getOutputStream());
        }
        return;
      }

      // GZIP
      if (gzipContent) {
        GzipResponseWrapper wrapper = new GzipResponseWrapper(response);
        try {
          chain.doFilter(request, wrapper);

          if ((300 <= response.getStatus()) && (response.getStatus() < 400)) {
            return;
          }
          wrapper.finishWrapper();

        } catch (Exception ex) {
          if (response.isCommitted()) {
            wrapper.finishWrapper();
          }
          throw ex;
        }
        return;
      }

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
