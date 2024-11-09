// The MIT License (MIT)
// Copyright © 2015 AppsLandia. All rights reserved.

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

import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.UUID;

import com.appslandia.common.base.BaseEncoder;
import com.appslandia.common.base.Out;
import com.appslandia.common.cdi.Json;
import com.appslandia.common.cdi.Json.Profile;
import com.appslandia.common.json.JsonProcessor;
import com.appslandia.common.utils.DateUtils;
import com.appslandia.plum.utils.SecurityUtils;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.CallerPrincipal;
import jakarta.security.enterprise.credential.RememberMeCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.RememberMeIdentityStore;
import jakarta.servlet.http.HttpServletRequest;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@ApplicationScoped
public class RemMeTokenIdentityStore implements RememberMeIdentityStore {

  public static final String CONFIG_EXPIRY_LEEWAY_MS = RemMeTokenIdentityStore.class.getName() + ".expiry_leeway_ms";
  public static final String CONFIG_TOKEN_BOUND_CLIENT_IP = RemMeTokenIdentityStore.class.getName()
      + ".token_bound_client_ip";
  public static final String CONFIG_TOKEN_BOUND_USER_AGENT = RemMeTokenIdentityStore.class.getName()
      + ".token_bound_user_agent";

  @Inject
  protected AppConfig appConfig;

  @Inject
  protected RemMeTokenHandler remMeTokenHandler;

  @Inject
  protected LoginEventManager loginEventManager;

  @Inject
  @Json(Profile.COMPACT)
  protected JsonProcessor jsonProcessor;

  @Inject
  protected IdentityHandler identityHandler;

  @Inject
  protected HttpServletRequest currentRequest;

  protected int getExpiryLeewayMs() {
    return this.appConfig.getInt(CONFIG_EXPIRY_LEEWAY_MS, 0);
  }

  protected boolean getTokenBoundClientIp() {
    return this.appConfig.getBool(CONFIG_TOKEN_BOUND_CLIENT_IP, false);
  }

  protected boolean getTokenBoundUserAgent() {
    return this.appConfig.getBool(CONFIG_TOKEN_BOUND_USER_AGENT, true);
  }

  protected String getClientData() {
    String clientIp = getTokenBoundClientIp() ? ServletUtils.getClientIp(this.currentRequest) : "false";
    String userAgent = getTokenBoundUserAgent() ? ServletUtils.getUserAgent(this.currentRequest) : "false";
    return clientIp + "," + userAgent;
  }

  @Override
  public String generateLoginToken(CallerPrincipal callerPrincipal, Set<String> groups) {
    // UserPrincipal
    UserPrincipal userPrincipal = (UserPrincipal) callerPrincipal;
    String identity = this.identityHandler.getIdentity(userPrincipal);
    int expiresInSec = this.appConfig.getInt(AppConfig.REMEMBER_ME_COOKIE_AGE);

    // Save Token
    SeriesToken seriesToken = this.remMeTokenHandler.saveToken(identity, userPrincipal.getModule(), getClientData(),
        expiresInSec);
    return encodeSeriesToken(seriesToken);
  }

  protected int newExpiresInSec(LocalDateTime curExpiresAtUtc, LocalDateTime curTimeAtUtc) {
    int maxAge = this.appConfig.getInt(AppConfig.REMEMBER_ME_COOKIE_AGE);
    int remainingSec = (int) ChronoUnit.SECONDS.between(curTimeAtUtc, curExpiresAtUtc);
    if (remainingSec < 0) {
      remainingSec = 0;
    }
    if (!this.appConfig.getBool(AppConfig.REMEMBER_ME_COOKIE_SLIDING) || (remainingSec >= maxAge / 2)) {
      return remainingSec;
    }
    return maxAge;
  }

  @Override
  public CredentialValidationResult validate(RememberMeCredential credential) {
    // SeriesToken
    SeriesToken seriesToken = decodeSeriesToken(credential.getToken());
    if ((seriesToken == null) || (seriesToken.getSeries() == null) || (seriesToken.getToken() == null)) {
      return InvalidAuthResult.TOKEN_INVALID;
    }

    // RequestContext
    RequestContext requestContext = ServletUtils.getRequestContext(this.currentRequest);
    String clientData = getClientData();

    // RemMeToken
    Out<String> invalidCode = new Out<>();
    RemMeToken remMeToken = this.remMeTokenHandler.verifyToken(seriesToken.getSeries(), seriesToken.getToken(),
        requestContext.getModule(), clientData, getExpiryLeewayMs(), invalidCode);

    if (invalidCode.value != null) {
      if (InvalidAuthResult.TOKEN_COMPROMISED.getCode().equals(invalidCode.value)) {
        this.remMeTokenHandler.removeAll(remMeToken.getIdentity());

        saveLoginEvent(remMeToken, DateUtils.nowAtUtcF3().toLocalDateTime(),
            LoginEvent.LOGIN_FAILURE_REMEMBER_ME_TOKEN_COMPROMISED);
      }
      return InvalidAuthResult.valueOf(invalidCode.value);
    }

    // Validate Identity
    PrincipalRoles principalRoles = this.identityHandler.validateIdentity(remMeToken.getModule(),
        remMeToken.getIdentity(), invalidCode);
    if (principalRoles == null) {
      return InvalidAuthResult.valueOf(invalidCode.get());
    }

    // Issue New Token
    LocalDateTime curTimeAtUtc = DateUtils.nowAtUtcF3().toLocalDateTime();
    int expiresInSec = newExpiresInSec(curTimeAtUtc, remMeToken.getExpiresAtUtc());

    String clearToken = this.remMeTokenHandler.reissue(remMeToken.getSeries(), remMeToken.getIdentity(),
        remMeToken.getModule(), clientData, curTimeAtUtc, expiresInSec);

    // LoginToken
    String newLoginToken = encodeSeriesToken(new SeriesToken().setSeries(seriesToken.getSeries()).setToken(clearToken));
    this.currentRequest.setAttribute(LoginToken.class.getName(),
        new LoginToken(newLoginToken, (int) expiresInSec, remMeToken.getIdentity(), remMeToken.getModule()));

    saveLoginEvent(remMeToken, curTimeAtUtc, LoginEvent.LOGIN_RESULT_SUCCESS);

    // AuthUserPrincipal(rememberMe=true, re-authentication=false)
    AuthUserPrincipal principal = new AuthUserPrincipal(principalRoles.getPrincipal(), remMeToken.getModule(), true,
        false);
    return SecurityUtils.createIdentityStoreResult(principal, principalRoles.getRoles());
  }

  @Override
  public void removeLoginToken(String token) {
    SeriesToken seriesToken = decodeSeriesToken(token);
    if ((seriesToken != null) && (seriesToken.getSeries() != null)) {
      this.remMeTokenHandler.remove(seriesToken.getSeries());
    }
  }

  protected String encodeSeriesToken(SeriesToken token) {
    if (this.appConfig.isEnableDebug()) {
      return token.getSeries() + ":" + token.getToken();
    } else {
      return BaseEncoder.BASE64_URL_NP.encode(this.jsonProcessor.toString(token).getBytes(StandardCharsets.UTF_8));
    }
  }

  protected SeriesToken decodeSeriesToken(String token) {
    if (this.appConfig.isEnableDebug()) {
      try {
        int idx = token.indexOf(':');
        return new SeriesToken().setSeries(UUID.fromString(token.substring(0, idx))).setToken(token.substring(idx + 1));
      } catch (Exception ex) {
        return null;
      }
    } else {
      try {
        return this.jsonProcessor.read(
            new StringReader(new String(BaseEncoder.BASE64_URL_NP.decode(token), StandardCharsets.UTF_8)),
            SeriesToken.class);
      } catch (Exception ex) {
        return null;
      }
    }
  }

  protected void saveLoginEvent(RemMeToken remMeToken, LocalDateTime loginAtUtc, int loginResult) {
    LoginEvent loginEvent = new LoginEvent();
    loginEvent.setIdentity(remMeToken.getIdentity());
    loginEvent.setModule(remMeToken.getModule());

    loginEvent.setEventType(LoginEvent.LOGIN_TYPE_REMEMBER_ME);
    loginEvent.setLoginResult(loginResult);

    loginEvent.setSeries(remMeToken.getSeries());
    loginEvent.setClientIp(ServletUtils.getClientIp(this.currentRequest));
    loginEvent.setUserAgent(ServletUtils.getUserAgent(this.currentRequest));
    loginEvent.setLoginAtUtc(loginAtUtc);

    this.loginEventManager.save(loginEvent);
  }

  static class LoginToken {
    final String loginToken;
    final int maxAge;
    final String identity;
    final String module;

    public LoginToken(String loginToken, int maxAge, String identity, String module) {
      this.loginToken = loginToken;
      this.maxAge = maxAge;
      this.identity = identity;
      this.module = module;
    }

    public String getLoginToken() {
      return this.loginToken;
    }

    public int getMaxAge() {
      return this.maxAge;
    }

    public String getIdentity() {
      return this.identity;
    }

    public String getModule() {
      return this.module;
    }
  }
}
