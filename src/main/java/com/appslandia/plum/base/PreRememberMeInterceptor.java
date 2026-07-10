// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.io.Serializable;

import com.appslandia.plum.utils.ServletUtils;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.authentication.mechanism.http.RememberMe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
@Interceptor
@RememberMe
@Priority(Interceptor.Priority.PLATFORM_BEFORE + 205)
public class PreRememberMeInterceptor implements Serializable {
  private static final long serialVersionUID = 1L;

  @Inject
  protected AppLogger appLogger;

  @Inject
  protected AppConfig appConfig;

  @Inject
  protected RemMePostHandler remMePostHandler;

  @Inject
  protected RequestContextParser requestContextParser;

  @AroundInvoke
  public Object intercept(InvocationContext context) throws Exception {
    // Not HttpAuthMechanismBase.isValidateRequest(context.getMethod()))
    if (!HttpAuthMechanismBase.isValidateRequest(context.getMethod())) {
      return context.proceed();
    }

    var parameters = context.getParameters();
    var request = (HttpServletRequest) parameters[0];
    var response = (HttpServletResponse) parameters[1];

    if (request.getUserPrincipal() != null) {
      appLogger.warn(
          "PreRememberMeInterceptor(205) must be executed between AutoApplySessionInterceptor(200) and RememberMeInterceptor(210).");
      return context.proceed();
    }

    // RequestContext
    requestContextParser.initRequestContext(request, response);

    // Invoke the next interceptor or the actual authentication mechanism
    var status = (AuthenticationStatus) context.proceed();
    if (status != AuthenticationStatus.SUCCESS) {
      return status;
    }

    // > AuthenticationStatus.SUCCESS

    // RemMePrincipal?: New rememberMe cookie
    var remMePrincipal = (RemMePrincipal) ServletUtils.removeAttribute(request, RemMePrincipal.class.getName());
    if (remMePrincipal != null) {

      var remMeCookieName = appConfig.getStringReq(AppConfig.REMEMBER_ME_COOKIE_NAME);
      var remMeCookieSecure = appConfig.getBool(AppConfig.REMEMBER_ME_COOKIE_SECURE);
      var remMeCookieHttpOnly = appConfig.getBool(AppConfig.REMEMBER_ME_COOKIE_HTTPONLY);

      ServletUtils.saveCookie(request, response, remMeCookieName, remMePrincipal.getSeriesToken(),
          remMePrincipal.getMaxAge(), null, (c) -> {
            c.setSecure(remMeCookieSecure);
            c.setHttpOnly(remMeCookieHttpOnly);
          });

      remMePostHandler.onRemMeSuccess(request, response, remMePrincipal.getIdentity(), remMePrincipal.getModule());
    }
    return AuthenticationStatus.SUCCESS;
  }
}
