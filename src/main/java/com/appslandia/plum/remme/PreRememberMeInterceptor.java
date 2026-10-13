// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.remme;

import java.io.Serializable;

import com.appslandia.plum.base.AppLogger;
import com.appslandia.plum.base.HttpAuthMechanismBase;
import com.appslandia.plum.base.RequestContextParser;
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
  protected RemMePostHandler remMePostHandler;

  @Inject
  protected RequestContextParser requestContextParser;

  @Inject
  protected RemMeConfig remMeConfig;

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

    // RemMePrincipal
    var remMePrincipal = (RemMePrincipal) ServletUtils.removeAttribute(request, RemMePrincipal.class.getName());
    if (remMePrincipal != null) {

      var remMeCookieName = remMeConfig.getRemMeCookieName(remMePrincipal.getModule());
      var remMeCookieSecure = remMeConfig.isRemMeCookieSecure(remMePrincipal.getModule());
      var remMeCookieHttpOnly = remMeConfig.isRemMeCookieHttpOnly(remMePrincipal.getModule());

      ServletUtils.saveCookie(request, response, remMeCookieName, remMePrincipal.getSeriesToken(),
          remMePrincipal.getMaxAge(), null, (c) -> {
            c.setSecure(remMeCookieSecure);
            c.setHttpOnly(remMeCookieHttpOnly);
          });

      remMePostHandler.onRemMeSuccess(request, response, remMePrincipal.getModule(), remMePrincipal.getIdentity());
    }
    return AuthenticationStatus.SUCCESS;
  }
}
