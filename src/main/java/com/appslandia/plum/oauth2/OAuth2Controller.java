// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.oauth2;

import com.appslandia.common.base.Out;
import com.appslandia.common.base.Params;
import com.appslandia.common.utils.Asserts;
import com.appslandia.plum.base.ActionResult;
import com.appslandia.plum.base.AppConfig;
import com.appslandia.plum.base.AuthContext;
import com.appslandia.plum.base.AuthParameters;
import com.appslandia.plum.base.ControllerBase;
import com.appslandia.plum.base.HttpActionRequest;
import com.appslandia.plum.results.RedirectResult;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
public abstract class OAuth2Controller extends ControllerBase {

  public static final String PARAM_OAUTH_FLOW = "__oauth_flow";

  @Inject
  protected AppConfig appConfig;

  @Inject
  protected AuthContext authContext;

  protected RedirectResult createHomeResult(String module) {
    var uri = appConfig.getHomeUri(module);
    Asserts.notNull(uri, "Home URI is not configured for module: {}", module);
    return RedirectResult.fromRelativeUri(uri, null);
  }

  protected RedirectResult createLoginResult(String module) {
    var uri = appConfig.getLoginUri(module);
    Asserts.notNull(uri, "Login URI is not configured for module: {}", module);
    return RedirectResult.fromRelativeUri(uri, null);
  }

  protected RedirectResult prepareRegisterResult(HttpActionRequest request, OAuth2Registration registration) {
    request.getSession().setAttribute(OAuth2Registration.SESSION_ATTRIBUTE_ID, registration);

    var uri = appConfig.getRegisterUri(registration.getModule());
    Asserts.notNull(uri, "Register URI is not configured for module: {}", registration.getModule());
    return RedirectResult.fromRelativeUri(uri, new Params().set(PARAM_OAUTH_FLOW, true));
  }

  protected RedirectResult createProfileResult(String module) {
    var uri = appConfig.getProfileUri(module);
    Asserts.notNull(uri, "Profile URI is not configured for module: {}", module);
    return RedirectResult.fromRelativeUri(uri, null);
  }

  protected ActionResult authenticateUser(HttpActionRequest request, HttpServletResponse response,
      OAuth2Registration registration) throws Exception {

    var context = ServletUtils.getRequestContext(request);
    Asserts.isTrue(context.getModule().equals(registration.getModule()));

    return authenticateUser(request, response, registration.getProvider(), registration.getUserInfo().getSubject(),
        registration.getAccessToken(), registration.getReturnUrl());
  }

  protected ActionResult authenticateUser(HttpActionRequest request, HttpServletResponse response, String provider,
      String subject, String accessToken, String returnUrl) throws Exception {
    var context = ServletUtils.getRequestContext(request);

    // Authenticate account.
    var credential = new OAuth2Credential(provider, subject, accessToken);
    var authParams = new AuthParameters().module(context.getModule()).credential(credential).rememberMe(false)
        .reauthentication(false);

    var invalidCode = new Out<String>();
    if (!this.authContext.authenticate(request, response, authParams, invalidCode)) {
      request.addErrorMessage(context.res("oauth2.authentication_unexpected_error"));
      return createLoginResult(context.getModule());
    }

    // Return to requested page.
    if (returnUrl != null && request.isInternalUrl(returnUrl)) {
      response.sendRedirect(response.encodeRedirectURL(returnUrl));
      return ActionResult.EMPTY;
    }
    return createHomeResult(context.getModule());
  }
}
