// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import com.appslandia.common.utils.URLEncoding;
import com.appslandia.plum.remme.RemMeConfig;
import com.appslandia.plum.utils.ServletUtils;

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
  protected RemMeConfig remMeConfig;

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
    response.sendRedirect(response.encodeRedirectURL(url.toString()));
  }

  @Override
  public void askReauthenticate(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext)
      throws Exception {
    var url = getRedirectUrl(request, true);
    response.sendRedirect(response.encodeRedirectURL(url.toString()));
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

  public String getRemMeCookieName() {
    return remMeConfig.getRemMeCookieName(getModule());
  }

  public int getRemMeCookieAge() {
    return remMeConfig.getRemMeCookieAge(getModule());
  }

  public boolean isRemMeCookieSecure() {
    return remMeConfig.isRemMeCookieSecure(getModule());
  }

  public boolean isRemMeCookieHttpOnly() {
    return remMeConfig.isRemMeCookieHttpOnly(getModule());
  }

  public boolean isRemMeCookieSlidingExpiry() {
    return remMeConfig.isRemMeCookieSlidingExpiry(getModule());
  }
}
