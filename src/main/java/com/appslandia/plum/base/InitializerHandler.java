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

import com.appslandia.common.base.MemoryStream.BlockIterator;
import com.appslandia.common.base.UncheckedException;
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
  protected LanguageProvider languageProvider;

  @Inject
  protected CorsPolicyHandler corsPolicyHandler;

  @Inject
  protected AppPolicyProvider appPolicyProvider;

  @Inject
  protected CompressHandlerProvider compressHandlerProvider;

  @Inject
  protected HttpAuthMechanismProvider httpAuthMechanismProvider;

  @Inject
  protected Md5DigestPool md5DigestPool;

  @Inject
  protected ServletContentUtil servletContentUtil;

  @Override
  public void init(FilterConfig config) throws ServletException {
    super.init(config);
  }

  protected void redirectLang(HttpServletRequest request, HttpServletResponse response, String pathLang)
      throws Exception {
    var url = ServletUtils.getUriWithQuery(request, pathLang);
    var targetUrl = appConfig.isEnableSession() ? response.encodeRedirectURL(url) : url;

    ServletUtils.sendRedirect(response, targetUrl, HttpServletResponse.SC_MOVED_TEMPORARILY);
  }

  @Override
  protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    Asserts.isTrue(request.getDispatcherType().equals(DispatcherType.REQUEST));
    try {
      // DEBUG
      if (appConfig.isEnableDebug()) {
        ServletUtils.testErrorStatus(request, "__test_error_status1");
        ServletUtils.testOutStream(request, response, "__test_out_stream1");
      }

      // RequestContext
      final var requestContext = ServletUtils.getRequestContext(request);
      final var actionDesc = requestContext.getActionDesc();

      // Not Found?
      if (actionDesc == null) {
        throw new NotFoundException(requestContext.res(Resources.ERROR_NOT_FOUND));
      }

      // Language
      if (languageProvider.isMultiLanguages()) {
        if (!requestContext.isPathLanguage()) {
          if (HttpMethod.GET.equals(request.getMethod()) || HttpMethod.HEAD.equals(request.getMethod())) {
            redirectLang(request, response, requestContext.getLanguageId());
            return;
          }
          throw new BadRequestException(requestContext.res(Resources.ERROR_BAD_REQUEST));
        }
      }

      // Allow Method?
      if (!actionDesc.getHttpMethods().contains(request.getMethod())) {
        if (!HttpMethod.OPTIONS.equals(request.getMethod())) {
          throw new MethodNotAllowedException(requestContext.res(Resources.ERROR_METHOD_NOT_ALLOWED))
              .setAllow(actionDesc.getMethodsAsString());
        }
      }

      // Consume Type
      if (actionDesc.getConsumeType() != null) {
        if (!ServletUtils.isContentSupported(request.getContentType(), actionDesc.getConsumeType().value())) {

          throw new HttpException(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
              requestContext.res(Resources.ERROR_UNSUPPORTED_MEDIA_TYPE));
        }
      }

      // OPTIONS
      if (HttpMethod.OPTIONS.equals(request.getMethod())) {
        doOptions(request, response, requestContext);
        return;
      }

      // RequestWrapper
      request = new RequestWrapper(request, requestContext.getPathParams());
      if (isMockContext()) {
        request.setAttribute(RequestWrapper.class.getName(), request);
      }

      // Authorize CORS
      var crossOrigin = getCrossOrigin(request);
      if (crossOrigin != null) {
        doCorsPolicy(request, response, requestContext, crossOrigin);
      }

      // Authorize
      if (actionDesc.getAuthorize() != null) {
        if (!doAuthorize(request, response, requestContext)) {
          return;
        }
      }

      // EnableAsync?
      if (actionDesc.getEnableAsync() != null) {
        chain.doFilter(request, response);
        return;
      }

      // ETAG
      if (requestContext.isGetOrHead()
          && appPolicyProvider.getEtagPolicies().stream().anyMatch(p -> p.matches(requestContext.getRelativePath()))) {
        doEtagRequest(request, response, chain);
        return;
      }

      // Compression
      var compressHandler = appPolicyProvider.getCompressPolicies().stream().anyMatch(
          p -> p.matches(requestContext.getRelativePath())) ? compressHandlerProvider.getHandler(request) : null;

      if (compressHandler != null) {
        compressHandler.apply(request, response, chain);
        return;
      }

      // Next filter
      chain.doFilter(request, response);

    } catch (RuntimeException | ServletException | IOException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new UncheckedException(ex);
    }
  }

  protected void doEtagRequest(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws Exception {
    final var content = servletContentUtil.newServletContent();
    try {
      // ServletContentResponse
      var wrapper = new ServletContentResponse(response, true, content);
      chain.doFilter(request, wrapper);

      // 3XX
      if ((300 <= response.getStatus()) && (response.getStatus() < 400)) {
        return;
      }
      wrapper.finishWrapper();

      // ETAG value
      var computedEtag = md5DigestPool.execute((md) -> {
        content.iterate(new BlockIterator() {

          @Override
          public void iterate(byte[] buf, int len) throws IOException {
            md.update(buf, 0, len);
          }
        });
        return ServletUtils.toEtag(md.digest());
      });

      // If Modified?
      if (!ServletUtils.checkNotModified(request, response, computedEtag)) {
        var requestContext = ServletUtils.getRequestContext(request);

        var compressHandler = appPolicyProvider.getCompressPolicies().stream().anyMatch(
            p -> p.matches(requestContext.getRelativePath())) ? compressHandlerProvider.getHandler(request) : null;

        if (compressHandler != null && content.length() > appConfig.getGzipThreshold()) {
          compressHandler.apply(response, content);

        } else {
          response.setContentLengthLong(content.length());
          content.writeTo(response.getOutputStream());
        }
      }
    } finally {
      content.reset();
    }
  }

  protected void doCorsPolicy(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext,
      String crossOrigin) throws Exception {
    var corsPolicy = appPolicyProvider.getCorsPolicies().stream()
        .filter(p -> p.matches(requestContext.getRelativePath())).findFirst().orElse(null);
    if (corsPolicy == null) {
      throw new ForbiddenException(
          requestContext.res(Resources.ERROR_FORBIDDEN_CORS, CorsResult.NOT_ALLOWED_CORS.name()));
    }

    var corsResult = corsPolicyHandler.handleRequest(request, response, crossOrigin, corsPolicy);
    if (corsResult != CorsPolicyHandler.CorsResult.ALLOWED) {
      throw new ForbiddenException(requestContext.res(Resources.ERROR_FORBIDDEN_CORS, corsResult.name()));
    }
  }

  protected boolean doAuthorize(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext)
      throws Exception {
    var principal = ServletUtils.getPrincipal(request);
    var httpAuthMechanism = httpAuthMechanismProvider.getMechanism(requestContext.getModule());
    var actionDesc = requestContext.getActionDesc();

    // Authenticate
    if ((principal == null) || !requestContext.getModule().equals(principal.getModule())) {
      httpAuthMechanism.askAuthenticate(request, response, requestContext);
      return false;
    }

    // Authorize Policies
    for (var authorizePolicy : appPolicyProvider.getAuthorizePolicies()) {
      if (!authorizePolicy.authorize(request, requestContext, principal)) {
        throw new ForbiddenException(requestContext.res(Resources.ERROR_FORBIDDEN));
      }
    }

    // Authorize
    if (!authorize(request, actionDesc.getAuthorize())) {
      throw new ForbiddenException(requestContext.res(Resources.ERROR_FORBIDDEN));
    }

    // Re-authenticate
    if (actionDesc.getAuthorize().reauth() && !isReauthSession(principal)) {
      httpAuthMechanism.askReauthenticate(request, response, requestContext);
      return false;
    }
    return true;
  }

  protected boolean authorize(HttpServletRequest request, Authorize authorize) {
    var roles = authorize.roles();
    if ((roles.length == 0)) {
      return true;
    }
    return Arrays.stream(roles).anyMatch(role -> request.isUserInRole(role));
  }

  protected boolean isReauthSession(UserPrincipal principal) {
    if (principal.getReauthAt() == 0) {
      return false;
    }
    var reauthTimeoutMs = appConfig.getLong(AppConfig.CONFIG_REAUTH_TIMEOUT_MS);
    return !DateUtils.isExpired(principal.getReauthAt() + reauthTimeoutMs, 0L);
  }

  protected void doOptions(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext)
      throws Exception {

    var origin = request.getHeader(CorsPolicy.HEADER_ORIGIN);
    if ((origin == null) || (request.getHeader(CorsPolicy.HEADER_AC_REQUEST_METHOD) == null)) {
      response.setHeader("Allow", requestContext.getActionDesc().getMethodsAsString());
      return;
    }

    var corsPolicy = appPolicyProvider.getCorsPolicies().stream()
        .filter(p -> p.matches(requestContext.getRelativePath())).findFirst().orElse(null);
    if (corsPolicy == null) {
      throw new ForbiddenException(
          requestContext.res(Resources.ERROR_FORBIDDEN_CORS, CorsResult.NOT_ALLOWED_CORS.name()));
    }

    var corsResult = corsPolicyHandler.handlePreflight(request, response, origin, corsPolicy);
    if (corsResult != CorsPolicyHandler.CorsResult.ALLOWED) {
      throw new ForbiddenException(requestContext.res(Resources.ERROR_FORBIDDEN_CORS, corsResult.name()));
    }
  }

  static String getCrossOrigin(HttpServletRequest request) {
    var origin = request.getHeader(CorsPolicy.HEADER_ORIGIN);
    if (origin == null) {
      return null;
    }
    var url = ServletUtils.getHostUrl(request);

    if (origin.equals(url.toString())) {
      return null;
    }
    return origin;
  }

  public boolean isMockContext() {
    return false;
  }
}
