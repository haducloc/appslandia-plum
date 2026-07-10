// Licensed under the MIT License.
// See LICENSE file in the project root for details.

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
 * The implementation must be exposed as a CDI bean with @ApplicationScoped and annotated with @MappedID using the using
 * the target module name.
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
  public AuthMethod getAuthMethod() {
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
    var targetUrl = appConfig.isEnableSession() ? response.encodeRedirectURL(url.toString()) : url.toString();
    response.sendRedirect(targetUrl);
  }

  @Override
  public void askReauthenticate(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext)
      throws Exception {
    var url = getRedirectUrl(request, true);
    var targetUrl = appConfig.isEnableSession() ? response.encodeRedirectURL(url.toString()) : url.toString();
    response.sendRedirect(targetUrl);
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
