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

import java.io.Serializable;

import com.appslandia.common.base.AppLogger;
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
@Priority(Interceptor.Priority.PLATFORM_BEFORE + 208)
//RememberMeInterceptor @Priority(PLATFORM_BEFORE + 210)
public class PreRememberMeInterceptor implements Serializable {
  private static final long serialVersionUID = 1L;

  @Inject
  protected AppLogger appLogger;

  @Inject
  protected AppConfig appConfig;

  @Inject
  protected CookieHandler cookieHandler;

  @Inject
  protected PostRememberMe postRememberMe;

  @Inject
  protected RequestContextParser requestContextParser;

  @AroundInvoke
  public Object intercept(InvocationContext context) throws Exception {

    // If not intercepting HttpAuthenticationMechanism#validateRequest
    if (!HttpAuthenticationMechanismBase.isValidateRequest(context.getMethod())) {
      return context.proceed();
    }

    Object[] parameters = context.getParameters();
    HttpServletRequest request = (HttpServletRequest) parameters[0];
    HttpServletResponse response = (HttpServletResponse) parameters[1];

    if (request.getUserPrincipal() != null) {
      this.appLogger.warn(
          "PreRememberMeInterceptor must be executed between ConditionalAutoApplySessionInterceptor and RememberMeInterceptor.");
      return context.proceed();
    }

    // Early parsing of request context
    this.requestContextParser.parse(request, response);

    // Try to authenticate with the next interceptor or actual authentication
    // mechanism
    AuthenticationStatus status = (AuthenticationStatus) context.proceed();
    if (status != AuthenticationStatus.SUCCESS) {
      return status;
    }

    // > AuthenticationStatus.SUCCESS

    // RemMeTokenIdentityStore.LoginToken?
    RemMeTokenIdentityStore.LoginToken loginToken = (RemMeTokenIdentityStore.LoginToken) ServletUtils
        .removeAttribute(request, RemMeTokenIdentityStore.LoginToken.class.getName());

    if (loginToken != null) {
      String remMeCookieName = this.appConfig.getStringReq(AppConfig.REMEMBER_ME_COOKIE_NAME);
      boolean remMeCookieSecure = this.appConfig.getBool(AppConfig.REMEMBER_ME_COOKIE_SECURE);
      boolean remMeCookieHttpOnly = this.appConfig.getBool(AppConfig.REMEMBER_ME_COOKIE_HTTPONLY);

      this.cookieHandler.saveCookie(response, remMeCookieName, loginToken.getLoginToken(), loginToken.getMaxAge(),
          (c) -> {
            c.setSecure(remMeCookieSecure);
            c.setHttpOnly(remMeCookieHttpOnly);
          });

      this.postRememberMe.apply(request, response, loginToken.getIdentity(), loginToken.getModule());
    }
    return AuthenticationStatus.SUCCESS;
  }
}
