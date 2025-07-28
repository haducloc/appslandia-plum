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

import java.io.IOException;
import java.util.Arrays;

import com.appslandia.common.base.MemoryStream.NodeIterator;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.DateUtils;
import com.appslandia.plum.base.CorsPolicyHandler.CorsResult;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.inject.Inject;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
public class InitializerHandler extends HttpFilter {
  private static final long serialVersionUID = 1L;

  @Inject
  protected AppConfig appConfig;

  @Inject
  protected ResponseEncodingStrategy responseEncodingStrategy;

  @Inject
  protected LanguageProvider languageProvider;

  @Inject
  protected ConfigHeaderHandler configHeaderHandler;

  @Inject
  protected HeaderPolicyProvider headerPolicyProvider;

  @Inject
  protected CorsPolicyHandler corsPolicyHandler;

  @Inject
  protected CorsPolicyProvider corsPolicyProvider;

  @Inject
  protected HttpAuthMechanismProvider httpAuthMechanismProvider;

  @Inject
  protected AuthorizePolicyProvider authorizePolicyProvider;

  @Inject
  protected ExceptionHandler exceptionHandler;

  @Inject
  protected AccessRateHandler accessRateHandler;

  @Inject
  protected RemoteClientVerifier remoteClientVerifier;

  @Inject
  protected Md5DigestPool md5DigestPool;

  @Inject
  protected ServletContentUtil servletContentUtil;

  @Override
  public void init(FilterConfig config) throws ServletException {
    super.init(config);
  }

  protected void initialize(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext)
      throws Exception {
    this.configHeaderHandler.writeHeaders(request, response, requestContext);

    for (String policy : this.appConfig.getStringArray(AppConfig.CONFIG_ENABLE_HEADER_POLICIES)) {
      this.headerPolicyProvider.getHeaderPolicy(policy).writePolicy(request, response, requestContext);
    }

    // Vary
    if (this.responseEncodingStrategy.enableEncoding(requestContext)) {
      response.addHeader("Vary", "Accept-Encoding");
    }
  }

  protected void redirectLang(HttpServletRequest request, HttpServletResponse response, String pathLang)
      throws Exception {
    var status = HttpServletResponse.SC_MOVED_TEMPORARILY;

    var url = ServletUtils.getRequestUrl(request, pathLang);
    ServletUtils.sendRedirect(response, this.appConfig.isEnableSession() ? response.encodeRedirectURL(url) : url,
        status);
  }

  @Override
  protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    Asserts.isTrue(request.getDispatcherType().equals(DispatcherType.REQUEST));
    try {
      // DEBUG
      if (this.appConfig.isEnableDebug()) {
        ServletUtils.testErrorStatus(request, "__test_error_status1");
        ServletUtils.testOutStream(request, response, "__test_out_stream1");
      }

      // RequestContext
      final var requestContext = ServletUtils.getRequestContext(request);

      // Initialize
      this.initialize(request, response, requestContext);

      final var actionDesc = requestContext.getActionDesc();

      // Not Found?
      if (actionDesc == null) {
        throw new NotFoundException(requestContext.res(Resources.ERROR_NOT_FOUND))
            .setTitleKey(Resources.ERROR_NOT_FOUND);
      }

      // Allow Method?
      if (!actionDesc.getAllowMethods().contains(request.getMethod())) {
        throw new HttpException(HttpServletResponse.SC_METHOD_NOT_ALLOWED,
            requestContext.res(Resources.ERROR_METHOD_NOT_ALLOWED)).setTitleKey(Resources.ERROR_METHOD_NOT_ALLOWED);
      }

      // Consume Type
      if (actionDesc.getConsumeType() != null) {
        if (!ServletUtils.isContentSupported(request.getContentType(), actionDesc.getConsumeType().value())) {

          throw new HttpException(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
              requestContext.res(Resources.ERROR_UNSUPPORTED_MEDIA_TYPE))
              .setTitleKey(Resources.ERROR_UNSUPPORTED_MEDIA_TYPE);
        }
      }

      // Language
      if (this.languageProvider.isMultiLanguages()) {
        if (!requestContext.isPathLanguage()) {
          if (HttpMethod.GET.equals(request.getMethod()) || HttpMethod.HEAD.equals(request.getMethod())) {
            redirectLang(request, response, requestContext.getLanguageId());
            return;
          }
          throw new BadRequestException(requestContext.res(Resources.ERROR_BAD_REQUEST));
        }
      } else {
        if (requestContext.isPathLanguage()) {
          if (HttpMethod.GET.equals(request.getMethod()) || HttpMethod.HEAD.equals(request.getMethod())) {
            redirectLang(request, response, null);
            return;
          }
          throw new BadRequestException(requestContext.res(Resources.ERROR_BAD_REQUEST));
        }
      }

      // Allow Client
      if (!this.remoteClientVerifier.allowClient(request)) {
        throw new ForbiddenException(requestContext.res(Resources.ERROR_FORBIDDEN));
      }

      // Access Rate
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

      // Authorize Origin
      var origin = this.corsPolicyHandler.getCrossOrigin(request);
      if (origin != null) {

        // Not @EnableCors
        if (actionDesc.getEnableCors() == null) {
          throw new ForbiddenException(
              requestContext.res(Resources.ERROR_FORBIDDEN_CORS, CorsResult.NOT_ALLOWED_CORS.name()));
        }
        var corsPolicy = this.corsPolicyProvider.getCorsPolicy(actionDesc.getEnableCors().value());
        var corsResult = this.corsPolicyHandler.handleCors(request, response, origin, corsPolicy);

        if (corsResult != CorsPolicyHandler.CorsResult.ALLOWED) {
          throw new ForbiddenException(requestContext.res(Resources.ERROR_FORBIDDEN_CORS, corsResult.name()));
        }
      }

      // Authorize
      var authorize = actionDesc.getAuthorize();
      if (authorize != null) {
        var principal = ServletUtils.getPrincipal(request);
        var httpAuthMechanism = this.httpAuthMechanismProvider.getMechanism(requestContext.getModule());

        // Authenticate
        if (principal == null) {
          httpAuthMechanism.askAuthenticate(request, response, requestContext);
          return;
        }

        // No @BypassAuthorization
        if (actionDesc.getBypassAuthorization() == null) {

          // Module
          if (!requestContext.getModule().equals(principal.getModule())) {
            httpAuthMechanism.askAuthenticate(request, response, requestContext);
            return;
          }

          // Authorize
          if (!authorize(request, principal, authorize)) {
            throw new ForbiddenException(requestContext.res(Resources.ERROR_FORBIDDEN))
                .setTitleKey(Resources.ERROR_FORBIDDEN);
          }
        }

        // REAUTHENTICATE
        if (authorize.reauth() && !isReauthSession(principal)) {
          httpAuthMechanism.askReauthenticate(request, response, requestContext);
          return;
        }
      }

      // NOT EnableAsync?
      if (actionDesc.getEnableAsync() == null) {

        // ResponseEncoder
        var responseEncoder = this.responseEncodingStrategy.getResponseEncoder(request, requestContext);

        // If ETAG
        if (requestContext.isGetOrHead() && requestContext.getActionDesc().getEnableEtag() != null) {

          // ServletContentResponse
          final var content = this.servletContentUtil.newServletContent();
          var wrapper = new ServletContentResponse(response, true, content);
          chain.doFilter(request, wrapper);

          if ((300 <= response.getStatus()) && (response.getStatus() < 400)) {
            return;
          }
          wrapper.finishWrapper();

          try {
            // ETAG value
            var computedEtag = this.md5DigestPool.execute((md) -> {
              content.iterate(new NodeIterator() {

                @Override
                public void iterate(byte[] buf, int len) throws IOException {
                  md.update(buf, 0, len);
                }
              });
              return ServletUtils.toEtag(md.digest());
            });

            // If Modified?
            if (!ServletUtils.checkNotModified(request, response, computedEtag)) {

              // Encoding
              if (responseEncoder != null) {
                responseEncoder.encode(response, content);

              } else {
                // Not encoding
                response.setContentLengthLong(content.size());
                content.writeTo(response.getOutputStream());
              }
            }
          } finally {
            content.reset();
          }
          return;
        }

        // > No ETAG
        if (responseEncoder != null) {
          responseEncoder.encode(request, response, chain);
          return;
        }
      }

      // Next filter?
      chain.doFilter(request, response);

    } catch (Exception ex) {
      this.exceptionHandler.handleException(request, response, ex);
    }
  }

  protected boolean authorize(HttpServletRequest request, UserPrincipal principal, Authorize authorize) {
    var roles = authorize.roles();
    var policies = authorize.policies();
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

  protected boolean isReauthSession(UserPrincipal principal) {
    if (principal.getReauthAt() == 0) {
      return false;
    }
    var reauthTimeoutMs = this.appConfig.getLong(AppConfig.CONFIG_REAUTH_TIMEOUT_MS);
    return !DateUtils.isExpired(principal.getReauthAt() + reauthTimeoutMs, 0L);
  }

  protected void doOptions(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext)
      throws Exception {
    var origin = request.getHeader(CorsPolicyHandler.HEADER_ORIGIN);
    if ((origin == null) || (request.getHeader(CorsPolicyHandler.HEADER_AC_REQUEST_METHOD) == null)) {
      throw new ForbiddenException(requestContext.res(Resources.ERROR_FORBIDDEN))
          .setTitleKey(Resources.ERROR_FORBIDDEN);
    }
    var corsPolicy = this.corsPolicyProvider.getCorsPolicy(requestContext.getActionDesc().getEnableCors().value());
    var corsResult = this.corsPolicyHandler.handlePreflight(request, response, corsPolicy);

    if (corsResult != CorsPolicyHandler.CorsResult.ALLOWED) {
      throw new ForbiddenException(requestContext.res(Resources.ERROR_FORBIDDEN_CORS, corsResult.name()));
    }
  }

  public boolean isMockContext() {
    return false;
  }
}
