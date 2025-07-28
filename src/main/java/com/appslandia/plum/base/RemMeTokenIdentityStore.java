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
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class RemMeTokenIdentityStore implements RememberMeIdentityStore {

  public static final String CONFIG_EXPIRY_LEEWAY_MS = RemMeTokenIdentityStore.class.getName() + ".expiry_leeway_ms";
  public static final String CONFIG_TOKEN_CLIENT_IP = RemMeTokenIdentityStore.class.getName() + ".token_client_ip";
  public static final String CONFIG_TOKEN_USER_AGENT = RemMeTokenIdentityStore.class.getName() + ".token_user_agent";

  @Inject
  protected AppConfig appConfig;

  @Inject
  protected RemMeTokenHandler remMeTokenHandler;

  @Inject
  protected AuthEventManager authEventManager;

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

  protected boolean getTokenClientIp() {
    return this.appConfig.getBool(CONFIG_TOKEN_CLIENT_IP, false);
  }

  protected boolean getTokenUserAgent() {
    return this.appConfig.getBool(CONFIG_TOKEN_USER_AGENT, true);
  }

  protected String getClientData() {
    var clientIp = getTokenClientIp() ? ServletUtils.getClientIp(this.currentRequest) : "IP";
    var userAgent = getTokenUserAgent() ? ServletUtils.getUserAgent(this.currentRequest) : "UA";
    return clientIp + "," + userAgent;
  }

  @Override
  public String generateLoginToken(CallerPrincipal callerPrincipal, Set<String> groups) {
    // UserPrincipal
    var userPrincipal = (UserPrincipal) callerPrincipal;
    var identity = this.identityHandler.getIdentity(userPrincipal);
    var expiresInSec = this.appConfig.getInt(AppConfig.REMEMBER_ME_COOKIE_AGE);

    // Save Token
    var seriesToken = this.remMeTokenHandler.saveToken(identity, userPrincipal.getModule(), getClientData(),
        expiresInSec);
    return encodeSeriesToken(seriesToken);
  }

  protected int newExpiresInSec(LocalDateTime curExpiresAtUtc, LocalDateTime curTimeAtUtc) {
    var maxAge = this.appConfig.getInt(AppConfig.REMEMBER_ME_COOKIE_AGE);
    var remainingSec = (int) ChronoUnit.SECONDS.between(curTimeAtUtc, curExpiresAtUtc);
    if (remainingSec < 0) {
      remainingSec = 0;
    }
    if (!this.appConfig.getBool(AppConfig.REMEMBER_ME_COOKIE_SLIDING_EXP) || (remainingSec >= maxAge / 2)) {
      return remainingSec;
    }
    return maxAge;
  }

  @Override
  public CredentialValidationResult validate(RememberMeCredential credential) {
    // SeriesToken
    var seriesToken = decodeSeriesToken(credential.getToken());
    if ((seriesToken == null) || (seriesToken.getSeries() == null) || (seriesToken.getToken() == null)) {
      return InvalidAuth.TOKEN_INVALID;
    }

    // RequestContext
    var requestContext = ServletUtils.getRequestContext(this.currentRequest);
    var clientData = getClientData();

    // RemMeToken
    var invalidCode = new Out<String>();
    var remMeToken = this.remMeTokenHandler.verifyToken(seriesToken.getSeries(), seriesToken.getToken(),
        requestContext.getModule(), clientData, getExpiryLeewayMs(), invalidCode);

    if (invalidCode.value != null) {
      if (InvalidAuth.TOKEN_COMPROMISED.getCode().equals(invalidCode.value)) {
        this.remMeTokenHandler.removeAll(remMeToken.getIdentity());

        saveAuthEvent(remMeToken, DateUtils.timeAtUtcF3(), AuthEvent.AUTH_RESULT_TOKEN_COMP);
      }
      return InvalidAuth.valueOf(invalidCode.value);
    }

    // Validate Identity
    var rolesPrincipal = this.identityHandler.validateIdentity(remMeToken.getModule(), remMeToken.getIdentity(),
        invalidCode);
    if (rolesPrincipal == null) {
      return InvalidAuth.valueOf(invalidCode.get());
    }

    // Issue New Token
    var curTimeAtUtc = DateUtils.nowAtUtcF3().toLocalDateTime();
    var expiresInSec = newExpiresInSec(curTimeAtUtc, remMeToken.getExpiresAtUtc());

    var clearToken = this.remMeTokenHandler.reissue(remMeToken.getSeries(), remMeToken.getIdentity(),
        remMeToken.getModule(), clientData, curTimeAtUtc, expiresInSec);

    // LoginToken
    var newLoginToken = encodeSeriesToken(new SeriesToken().setSeries(seriesToken.getSeries()).setToken(clearToken));
    this.currentRequest.setAttribute(LoginToken.class.getName(),
        new LoginToken(newLoginToken, expiresInSec, remMeToken.getIdentity(), remMeToken.getModule()));

    saveAuthEvent(remMeToken, curTimeAtUtc, AuthEvent.AUTH_RESULT_SUCCESS);

    // AuthUserPrincipal(rememberMe=true, re-authentication=false)
    var principal = new AuthUserPrincipal(rolesPrincipal.getPrincipal(), remMeToken.getModule(), true, false);
    return SecurityUtils.createIdentityStoreResult(principal, rolesPrincipal.getRoles());
  }

  @Override
  public void removeLoginToken(String token) {
    var seriesToken = decodeSeriesToken(token);
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
        var idx = token.indexOf(':');
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

  protected void saveAuthEvent(RemMeToken remMeToken, LocalDateTime authAtUtc, String authResult) {
    var event = new AuthEvent();
    event.setIdentity(remMeToken.getIdentity());
    event.setModule(remMeToken.getModule());

    event.setAuthType(AuthEvent.AUTH_TYPE_REMME);
    event.setAuthResult(authResult);

    event.setRemmeSeries(remMeToken.getSeries());
    event.setClientIp(ServletUtils.getClientIp(this.currentRequest));
    event.setUserAgent(ServletUtils.getUserAgent(this.currentRequest));
    event.setAuthAtUtc(authAtUtc);

    this.authEventManager.save(event);
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
