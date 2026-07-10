// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

import com.appslandia.common.base.UncheckedException;
import com.appslandia.common.utils.Asserts;
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
 * @author Loc Ha
 *
 */
public class InitializerFilter extends HttpFilter {
  private static final long serialVersionUID = 1L;

  @Inject
  protected AppConfig appConfig;

  @Inject
  protected LanguageProvider languageProvider;

  @Inject
  protected CorsPolicyHandler corsPolicyHandler;

  @Inject
  protected CorsPolicyProvider corsPolicyProvider;

  @Inject
  protected HttpAuthMechanismProvider httpAuthMechanismProvider;

  protected void redirectLang(HttpServletRequest request, HttpServletResponse response, String pathLang)
      throws Exception {
    var url = ServletUtils.getUriWithQuery(request, pathLang);
    var targetUrl = appConfig.isEnableSession() ? response.encodeRedirectURL(url) : url;
    response.sendRedirect(targetUrl);
  }

  @Override
  protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    Asserts.isTrue(request.getDispatcherType() == DispatcherType.REQUEST);
    try {
      // DEBUG
      if (appConfig.isEnableDebug()) {
        ServletUtils.testErrorStatus(request, "__test_error_status1");
      }

      // RequestContext
      var requestContext = ServletUtils.getRequestContext(request);
      var actionDesc = requestContext.getActionDesc();

      // Not Found?
      if (actionDesc == null) {
        throw new NotFoundException("Action not found", Resources.ERROR_NOT_FOUND, null);
      }

      // Language
      if (languageProvider.isMultiLanguages()) {
        if (!requestContext.isPathLanguage()) {
          if (HttpMethod.GET.equals(request.getMethod()) || HttpMethod.HEAD.equals(request.getMethod())) {
            redirectLang(request, response, requestContext.getLanguageId());
            return;
          }

          throw new NotFoundException("Path language not found", Resources.ERROR_NOT_FOUND, null);
        }
      }

      // Allow Method?
      if (!actionDesc.getHttpMethods().contains(request.getMethod())) {
        if (!HttpMethod.OPTIONS.equals(request.getMethod())) {

          throw new MethodNotAllowedException("Method not allowed: " + request.getMethod(),
              Resources.ERROR_METHOD_NOT_ALLOWED, null, (resp) -> {
                resp.setHeader("Allow", actionDesc.getMethodsAsString());
              });
        }
      }

      // Consume Type
      if (actionDesc.getConsumeType() != null) {
        if (!ServletUtils.isMimeTypeSupported(request.getContentType(), actionDesc.getConsumeType().value())) {
          throw new UnsupportedMediaTypeException("Unsupported media type: " + request.getContentType(),
              Resources.ERROR_UNSUPPORTED_MEDIA_TYPE, null);
        }
      }

      // OPTIONS
      if (HttpMethod.OPTIONS.equals(request.getMethod())) {
        doOptions(request, response, requestContext);
        return;
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

      // Request/Response Wrapper
      request = new HttpRequestFacade(request, requestContext.getPathParams());
      response = new HttpResponseWrapper(response);

      // Next filter
      chain.doFilter(request, response);

    } catch (RuntimeException | ServletException | IOException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new UncheckedException(ex);
    }
  }

  protected void doCorsPolicy(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext,
      String crossOrigin) throws Exception {

    var enableCors = requestContext.getActionDesc().getEnableCors();
    var corsPolicy = (enableCors != null) ? this.corsPolicyProvider.getCorsPolicy(enableCors.policy()) : null;

    if (corsPolicy == null) {
      throw new ForbiddenException("CORS failed. Result=" + CorsResult.NO_CORS_POLICY, Resources.ERROR_FORBIDDEN, null);
    }

    var corsResult = corsPolicyHandler.handleRequest(request, response, crossOrigin, corsPolicy);
    if (corsResult != CorsPolicyHandler.CorsResult.ALLOWED) {
      throw new ForbiddenException("CORS failed. Result=" + corsResult, Resources.ERROR_FORBIDDEN, null);
    }
  }

  protected boolean doAuthorize(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext)
      throws Exception {
    var principal = ServletUtils.getPrincipal(request);
    var httpAuthMechanism = httpAuthMechanismProvider.getMechanism(requestContext.getModule());

    // Authenticate
    if ((principal == null) || !requestContext.getModule().equals(principal.getModule())) {
      httpAuthMechanism.askAuthenticate(request, response, requestContext);
      return false;
    }

    // Authorize
    if (!authorize(request, requestContext.getActionDesc().getAuthorize())) {
      throw new ForbiddenException("Authorize failed. Principal=" + principal.getName(), Resources.ERROR_FORBIDDEN,
          null);
    }

    // Re-authenticate
    if (requestContext.getActionDesc().getAuthorize().reauth() && !isReauthSession(principal)) {
      httpAuthMechanism.askReauthenticate(request, response, requestContext);
      return false;
    }
    return true;
  }

  private static boolean authorize(HttpServletRequest request, Authorize authorize) {
    var roles = authorize.roles();
    if ((roles.length == 0)) {
      return true;
    }
    return Arrays.stream(roles).anyMatch(role -> request.isUserInRole(role));
  }

  protected boolean isReauthSession(AuthPrincipal principal) {
    if (principal.getReauthAt() == null) {
      return false;
    }

    var reauthTimeoutSec = appConfig.getLong(AppConfig.CONFIG_REAUTH_TIMEOUT_SEC);
    var diff = Duration.between(principal.getReauthAt(), Instant.now());

    return diff.toSeconds() <= reauthTimeoutSec;
  }

  protected void doOptions(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext)
      throws Exception {
    var origin = request.getHeader(CorsPolicy.HEADER_ORIGIN);
    if ((origin == null) || (request.getHeader(CorsPolicy.HEADER_AC_REQUEST_METHOD) == null)) {
      response.setHeader("Allow", requestContext.getActionDesc().getMethodsAsString());
      return;
    }

    var enableCors = requestContext.getActionDesc().getEnableCors();
    var corsPolicy = (enableCors != null) ? this.corsPolicyProvider.getCorsPolicy(enableCors.policy()) : null;

    if (corsPolicy == null) {
      throw new ForbiddenException("CORS failed. Result=" + CorsResult.NO_CORS_POLICY, Resources.ERROR_FORBIDDEN, null);
    }

    var corsResult = corsPolicyHandler.handlePreflight(request, response, origin, corsPolicy);
    if (corsResult != CorsPolicyHandler.CorsResult.ALLOWED) {
      throw new ForbiddenException("CORS failed. Result=" + corsResult, Resources.ERROR_FORBIDDEN, null);
    }
  }

  private static String getCrossOrigin(HttpServletRequest request) {
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
}
