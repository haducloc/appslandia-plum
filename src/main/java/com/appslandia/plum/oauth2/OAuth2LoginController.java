// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.oauth2;

import java.time.Instant;

import com.appslandia.common.base.Out;
import com.appslandia.common.utils.Asserts;
import com.appslandia.plum.base.ActionResult;
import com.appslandia.plum.base.AuthPrincipal;
import com.appslandia.plum.base.Authorize;
import com.appslandia.plum.base.HttpActionRequest;
import com.appslandia.plum.base.HttpGet;
import com.appslandia.plum.base.RequestContext;
import com.appslandia.plum.base.UnauthorizedException;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Each OAuth2 provider must provide an implementation of this abstract class, exposed as an {@link ApplicationScoped}
 * CDI bean.
 *
 * @author Loc Ha
 */
public abstract class OAuth2LoginController extends OAuth2Controller {

  @Inject
  protected OAuth2FlowDelegate flowDelegate;

  @Inject
  protected OAuth2ConfigProvider configProvider;

  @Inject
  protected OAuth2AccountServiceProvider accountServiceProvider;

  protected abstract String getProvider();

  protected OAuth2Config getConfig(String provider) {
    return configProvider.getConfig(provider);
  }

  protected OAuth2AccountService getAccountService(String module) {
    return accountServiceProvider.getAccountService(module);
  }

  protected ActionResult handleLoggedIn(String contextModule, AuthPrincipal principal) {
    if (contextModule.equals(principal.getModule())) {
      return createHomeResult(contextModule);
    }
    throw new UnauthorizedException("You must log out before signing in to a different module.");
  }

  @HttpGet
  public ActionResult login(HttpActionRequest request, HttpServletResponse response, RequestContext context)
      throws Exception {
    var module = context.getModule();

    if (request.getUserPrincipal() != null) {
      return handleLoggedIn(module, request.getUserPrincipal());
    }

    var provider = getProvider();
    var config = getConfig(provider);

    // Start authentication.
    var returnUrl = request.getReturnUrl();
    var callbackUri = ServletUtils.toAbsoluteUri(request, config.getLoginCallbackUri(module));
    var authorizationUrl = flowDelegate.buildAuthorizationUrl(request, module, provider, callbackUri, returnUrl);

    response.sendRedirect(authorizationUrl);
    return ActionResult.EMPTY;
  }

  @HttpGet
  public ActionResult callback(HttpActionRequest request, HttpServletResponse response, RequestContext context,
      String state, String code, String error) throws Exception {

    request.assertTrue(request.getUserPrincipal() == null, "request.getUserPrincipal() must be null");
    request.assertNotNull(state, "state is required.");

    var module = context.getModule();
    var provider = getProvider();

    // Handle provider error.
    if (error != null) {
      if ("access_denied".equals(error)) {
        request.addErrorMessage(context.res("oauth2.authentication_cancelled", provider));
      } else {
        request.addErrorMessage(context.res("oauth2.authentication_failed", provider));
      }
      return createLoginResult(module);
    }
    request.assertNotNull(code, "code is required.");

    var config = getConfig(provider);
    var accountService = getAccountService(module);

    // Exchange code for token.
    var callbackUri = ServletUtils.toAbsoluteUri(request, config.getLoginCallbackUri(module));
    var returnUrl = new Out<String>();
    var token = flowDelegate.handleCallback(request, module, provider, state, code, callbackUri, returnUrl);

    // Load user info.
    var userInfoJson = flowDelegate.getUserInfo(module, provider, token.getAccess_token());
    var userInfo = config.parseUserInfo(userInfoJson);

    // Login existing account.
    var account = accountService.getBySubject(userInfo.getSubject(), provider);
    if (account != null) {
      Asserts.isTrue(userInfo.getEmail().equalsIgnoreCase(account.getEmail()));

      return authenticateUser(request, response, provider, userInfo.getSubject(), token.getAccess_token(),
          returnUrl.value);
    }

    // Existing account must link manually.
    account = accountService.getByEmail(userInfo.getEmail());
    if (account != null) {
      request.addErrorMessage(context.res("oauth2.account_manual_link_required", provider));
      return createLoginResult(module);
    }

    // Prepare registration.
    var expiresAtUtc = Instant.now().plusSeconds(config.getSignUpExpiresInSec());
    var registration = new OAuth2Registration(module, provider, token.getAccess_token(), expiresAtUtc, returnUrl.value,
        userInfo);
    return prepareRegisterResult(request, registration);
  }

  @HttpGet
  @Authorize
  public ActionResult link(HttpActionRequest request, HttpServletResponse response, RequestContext context,
      String state, String code, String error) throws Exception {

    var principal = request.getUserPrincipal();

    request.assertNotNull(principal, "request.getUserPrincipal() must not be null");
    request.assertNotNull(state, "state is required.");

    var module = context.getModule();
    var provider = getProvider();

    request.assertTrue(module.equals(principal.getModule()), "principal module is mismatched.");

    // Handle provider error.
    if (error != null) {
      if ("access_denied".equals(error)) {
        request.addErrorMessage(context.res("oauth2.authentication_cancelled", provider));
      } else {
        request.addErrorMessage(context.res("oauth2.authentication_failed", provider));
      }
      return createProfileResult(module);
    }
    request.assertNotNull(code, "code is required.");

    var config = getConfig(provider);
    var accountService = getAccountService(module);

    // Exchange code for token.
    var callbackUri = ServletUtils.toAbsoluteUri(request, config.getLinkCallbackUri(module));
    var returnUrl = new Out<String>();
    var token = flowDelegate.handleCallback(request, module, provider, state, code, callbackUri, returnUrl);

    // Load user info.
    var userInfoJson = flowDelegate.getUserInfo(module, provider, token.getAccess_token());
    var userInfo = config.parseUserInfo(userInfoJson);

    // Assert email.
    var principalEmail = (String) principal.getReq(AuthPrincipal.ATTRIBUTE_EMAIL);
    Asserts.isTrue(principalEmail.equalsIgnoreCase(userInfo.getEmail()));

    // Load signed-in account.
    var account = accountService.getByEmail(principalEmail);
    Asserts.notNull(account);

    // Already linked.
    if (userInfo.getSubject().equals(account.getSubject(provider))) {
      request.addInfoMessage(context.res("oauth2.account_already_linked", provider));
      return createProfileResult(module);
    }

    // Linked to another identity.
    if (account.getSubject(provider) != null) {
      request.addErrorMessage(context.res("oauth2.account_linked_to_another", provider));
      return createProfileResult(module);
    }

    // Identity already linked.
    var linkedAccount = accountService.getBySubject(userInfo.getSubject(), provider);
    if (linkedAccount != null) {
      request.addErrorMessage(context.res("oauth2.provider_account_linked_to_another", provider));
      return createProfileResult(module);
    }

    // Link account.
    accountService.link(account.getEmail(), userInfo.getSubject(), provider);

    request.addInfoMessage(context.res("oauth2.account_linked_successfully", provider));
    return createProfileResult(module);
  }
}
