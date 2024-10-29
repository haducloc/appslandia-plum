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
import java.util.Set;

import com.appslandia.common.base.BaseEncoder;
import com.appslandia.common.base.Out;
import com.appslandia.common.cdi.Json;
import com.appslandia.common.cdi.Json.Profile;
import com.appslandia.common.json.JsonProcessor;
import com.appslandia.common.utils.STR;
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
    String userAgent = getTokenBoundUserAgent() ? this.currentRequest.getHeader("User-Agent") : "false";
    return STR.fmt("IP={}|UA={}", clientIp, userAgent);
  }

  @Override
  public String generateLoginToken(CallerPrincipal callerPrincipal, Set<String> groups) {
    // UserPrincipal
    UserPrincipal userPrincipal = (UserPrincipal) callerPrincipal;
    String identity = this.identityHandler.getIdentity(userPrincipal);

    long expiresInMs = this.appConfig.getInt(AppConfig.REMEMBER_ME_COOKIE_AGE) * 1000L;
    long issuedAt = System.currentTimeMillis();

    // Save Token
    SeriesToken seriesToken = this.remMeTokenHandler.saveToken(identity, userPrincipal.getModule(), getClientData(),
        expiresInMs, issuedAt);
    return encodeSeriesToken(seriesToken);
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
        this.remMeTokenHandler.handleTokenCompromise(remMeToken);
      }
      return InvalidAuthResult.valueOf(invalidCode.value);
    }

    // Validate Identity
    PrincipalRoles principalRoles = this.identityHandler.validateIdentity(remMeToken.getModule(),
        remMeToken.getIdentity(), invalidCode);
    if (principalRoles == null) {
      return InvalidAuthResult.valueOf(invalidCode.get());
    }

    // New Token
    boolean remMeCookieSliding = this.appConfig.getBool(AppConfig.REMEMBER_ME_COOKIE_SLIDING);
    int remMeCookieAge = this.appConfig.getInt(AppConfig.REMEMBER_ME_COOKIE_AGE);

    long curTimeMs = System.currentTimeMillis();
    long expiresInMs = remMeCookieSliding ? getExpiresInMs(remMeToken.getExpiresAt(), curTimeMs, remMeCookieAge)
        : remMeToken.getExpiresAt() - curTimeMs;

    String clearToken = this.remMeTokenHandler.reissue(remMeToken.getSeries(), remMeToken.getIdentity(),
        remMeToken.getModule(), clientData, expiresInMs, curTimeMs);

    // LoginToken
    String newLoginToken = encodeSeriesToken(new SeriesToken().setSeries(seriesToken.getSeries()).setToken(clearToken));
    this.currentRequest.setAttribute(LoginToken.class.getName(),
        new LoginToken(newLoginToken, (int) (expiresInMs / 1000L), remMeToken.getIdentity(), remMeToken.getModule()));

    // AuthUserPrincipal(rememberMe=true, re-authentication=false)
    AuthUserPrincipal principal = new AuthUserPrincipal(principalRoles.getPrincipal(), remMeToken.getModule(), true,
        false);

    this.remMeTokenHandler.handleLoginSuccess(remMeToken.getIdentity(), remMeToken.getModule(), curTimeMs, clientData);
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
    return BaseEncoder.BASE64_URL_NP.encode(this.jsonProcessor.toString(token).getBytes(StandardCharsets.UTF_8));
  }

  protected SeriesToken decodeSeriesToken(String token) {
    try {
      return this.jsonProcessor.read(
          new StringReader(new String(BaseEncoder.BASE64_URL_NP.decode(token), StandardCharsets.UTF_8)),
          SeriesToken.class);
    } catch (Exception ex) {
      return null;
    }
  }

  static long getExpiresInMs(long curExpiresAt, long curTimeMs, int maxAge) {
    long remainingMs = curExpiresAt - curTimeMs;
    if (remainingMs >= maxAge * 500L) {
      return remainingMs;
    }
    return maxAge * 1000L;
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
