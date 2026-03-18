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

import com.appslandia.common.utils.STR;
import com.appslandia.common.utils.URLEncoding;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import jakarta.security.enterprise.credential.Credential;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
public abstract class FormHttpAuthMechanism extends HttpAuthMechanismBase {

  @Inject
  protected AppConfig appConfig;

  private String remMeCookieName;
  private int remMeCookieAge;
  private boolean remMeCookieSecure;
  private boolean remMeCookieHttpOnly;

  @PostConstruct
  protected void initialize() {
    remMeCookieName = appConfig.getString(AppConfig.REMEMBER_ME_COOKIE_NAME);
    remMeCookieAge = appConfig.getInt(AppConfig.REMEMBER_ME_COOKIE_AGE);
    remMeCookieSecure = appConfig.getBool(AppConfig.REMEMBER_ME_COOKIE_SECURE);
    remMeCookieHttpOnly = appConfig.getBool(AppConfig.REMEMBER_ME_COOKIE_HTTPONLY);

    appLogger.info(STR.fmt("{}: {}", AppConfig.REMEMBER_ME_COOKIE_NAME, remMeCookieName));
    appLogger.info(STR.fmt("{}: {}", AppConfig.REMEMBER_ME_COOKIE_AGE, remMeCookieAge));
    appLogger.info(STR.fmt("{}: {}", AppConfig.REMEMBER_ME_COOKIE_SECURE, remMeCookieSecure));
    appLogger.info(STR.fmt("{}: {}", AppConfig.REMEMBER_ME_COOKIE_HTTPONLY, remMeCookieHttpOnly));
  }

  @Override
  protected AuthMethod getAuthMethod() {
    return AuthMethod.FORM;
  }

  @Override
  protected Credential getCredential(HttpServletRequest request, HttpMessageContext httpMessageContext) {
    if (httpMessageContext.isAuthenticationRequest()) {
      return httpMessageContext.getAuthParameters().getCredential();
    }
    return null;
  }

  @Override
  protected boolean isReauthentication(HttpServletRequest request, HttpMessageContext httpMessageContext) {
    if (httpMessageContext.isAuthenticationRequest()) {

      if (httpMessageContext.getAuthParameters() instanceof AuthParameters authParams) {
        return authParams.isReauthentication();
      }
    }
    return false;
  }

  @Override
  public void askAuthenticate(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext)
      throws Exception {
    var url = getRedirectUrl(request, false);
    var redirectUrl = url.toString();

    response.sendRedirect(appConfig.isEnableSession() ? response.encodeRedirectURL(redirectUrl) : redirectUrl);
  }

  @Override
  public void askReauthenticate(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext)
      throws Exception {
    var url = getRedirectUrl(request, true);
    var redirectUrl = url.toString();

    response.sendRedirect(appConfig.isEnableSession() ? response.encodeRedirectURL(redirectUrl) : redirectUrl);
  }

  protected StringBuilder getRedirectUrl(HttpServletRequest request, boolean reauthentication) {
    var url = ServletUtils.getFormLoginUri(request);

    // returnUrl
    var returnUrl = ServletUtils.getUriWithQuery(request);
    url.append('?').append(ServletUtils.PARAM_RETURN_URL).append('=').append(URLEncoding.encodeParam(returnUrl));

    // Re-authentication
    if (reauthentication) {
      url.append('&').append(ServletUtils.PARAM_REAUTHENTICATION).append('=').append(reauthentication);
    }
    return url;
  }

  // @RememberMe()

  public boolean isRememberMe(HttpMessageContext httpMessageContext) {
    if (httpMessageContext.isAuthenticationRequest()) {
      return httpMessageContext.getAuthParameters().isRememberMe();
    }
    return false;
  }

  public String remMeCookieName() {
    return remMeCookieName;
  }

  public int remMeCookieAge() {
    return remMeCookieAge;
  }

  public boolean remMeCookieSecure() {
    return remMeCookieSecure;
  }

  public boolean remMeCookieHttpOnly() {
    return remMeCookieHttpOnly;
  }
}
