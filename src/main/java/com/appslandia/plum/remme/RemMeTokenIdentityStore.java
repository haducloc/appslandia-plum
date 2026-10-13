// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.remme;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;

import com.appslandia.common.base.BaseEncoder;
import com.appslandia.common.base.Out;
import com.appslandia.common.cdi.Json;
import com.appslandia.common.cdi.Json.Profile;
import com.appslandia.common.json.JsonException;
import com.appslandia.common.json.JsonProcessor;
import com.appslandia.common.utils.Asserts;
import com.appslandia.plum.base.AuthEvent;
import com.appslandia.plum.base.AuthEventPublisher;
import com.appslandia.plum.base.AuthInvalidResult;
import com.appslandia.plum.base.AuthPrincipal;
import com.appslandia.plum.base.AuthResult;
import com.appslandia.plum.base.ModuleConfig;
import com.appslandia.plum.base.RequestPrincipal;
import com.appslandia.plum.base.SeriesToken;
import com.appslandia.plum.utils.ServletUtils;

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
public abstract class RemMeTokenIdentityStore implements RememberMeIdentityStore {

  @Inject
  protected RemMeTokenHandler remMeTokenHandler;

  @Inject
  protected AuthEventPublisher authEventPublisher;

  @Inject
  @Json(Profile.COMPACT)
  protected JsonProcessor jsonProcessor;

  @Inject
  protected HttpServletRequest currentRequest;

  @Inject
  protected RemMeConfig remMeConfig;

  protected String getIdentity(AuthPrincipal principal) {
    return principal.getCallerUniqueId();
  }

  @Override
  public String generateLoginToken(CallerPrincipal callerPrincipal, Set<String> groups) {
    Asserts.isTrue(callerPrincipal instanceof AuthPrincipal,
        "The given callerPrincipal must be an instance of AuthPrincipal.");

    // AuthPrincipal
    var principal = (AuthPrincipal) callerPrincipal;
    var identity = getIdentity(principal);
    var expiresInSec = remMeConfig.getRemMeCookieAge(principal.getModule());

    // Save Token
    var boundClientIp = remMeConfig.isRemMeBoundClientIp(principal.getModule());
    var boundUserAgent = remMeConfig.isRemMeBoundUserAgent(principal.getModule());

    var clientBoundData = getClientBoundData(currentRequest, boundClientIp, boundUserAgent);
    var seriesToken = remMeTokenHandler.saveToken(principal.getModule(), identity, clientBoundData, expiresInSec);

    return encodeToken(seriesToken);
  }

  protected int computeExpiresInSec(String module, Instant curExpiresAtUtc, Instant curTimeAtUtc) {
    var maxAge = remMeConfig.getRemMeCookieAge(module);
    var slidingExpiry = remMeConfig.isRemMeCookieSlidingExpiry(module);
    var remainingSec = ChronoUnit.SECONDS.between(curTimeAtUtc, curExpiresAtUtc);

    if (remainingSec <= 0) {
      return slidingExpiry ? maxAge : 0;
    }

    if (!slidingExpiry || (remainingSec >= maxAge / 2)) {
      return (int) remainingSec;
    }
    return maxAge;
  }

  protected abstract AuthResult doValidate(String module, String identity);

  @Override
  public CredentialValidationResult validate(RememberMeCredential credential) {
    var requestContext = ServletUtils.getRequestContext(currentRequest);
    var module = requestContext.getModule();

    if (ModuleConfig.MODULE_NONE.equals(module)) {
      return CredentialValidationResult.NOT_VALIDATED_RESULT;
    }

    // SeriesToken
    var seriesToken = decodeToken(credential.getToken());
    if ((seriesToken == null) || (seriesToken.getSeries() == null) || (seriesToken.getToken() == null)) {
      return AuthInvalidResult.toResult(AuthResult.TOKEN_INVALID);
    }

    // RemMeToken
    var boundClientIp = remMeConfig.isRemMeBoundClientIp(module);
    var boundUserAgent = remMeConfig.isRemMeBoundUserAgent(module);
    var clientBoundData = getClientBoundData(currentRequest, boundClientIp, boundUserAgent);

    var invalidCode = new Out<String>();
    var remMeToken = remMeTokenHandler.verifyToken(module, seriesToken.getSeries(), seriesToken.getToken(),
        clientBoundData, invalidCode);

    if (invalidCode.value != null) {
      if (AuthResult.TOKEN_COMPROMISED.equals(invalidCode.value)) {

        remMeTokenHandler.removeAll(remMeToken.getIdentity());
        logAuthEvent(remMeToken, AuthResult.TOKEN_COMPROMISED, Instant.now(), null);
      }
      return AuthInvalidResult.toResult(invalidCode.value);
    }

    // Validate
    var authResult = doValidate(module, remMeToken.getIdentity());
    Asserts.notNull(authResult, "doValidate(module, identity) must not return null.");

    if (authResult == AuthResult.NOT_VALIDATED_RESULT) {
      return CredentialValidationResult.NOT_VALIDATED_RESULT;
    }
    if (authResult.getInvalidCode() != null) {
      return AuthInvalidResult.toResult(authResult.getInvalidCode());
    }

    // Issue New Token
    var curTimeAtUtc = Instant.now();
    var expiresInSec = computeExpiresInSec(module, remMeToken.getExpiresAtUtc(), curTimeAtUtc);

    var clearToken = remMeTokenHandler.reissueToken(module, remMeToken.getIdentity(), remMeToken.getSeries(),
        clientBoundData, curTimeAtUtc, expiresInSec);

    // Log AuthEvent
    logAuthEvent(remMeToken, AuthEvent.AUTH_RESULT_SUCCESS, curTimeAtUtc,
        authResult.getPrincipal().getCallerUniqueId());

    // RemMePrincipal
    var encToken = encodeToken(new SeriesToken().setSeries(seriesToken.getSeries()).setToken(clearToken));
    var remMePrincipal = new RemMePrincipal(module, encToken, remMeToken.getIdentity(), expiresInSec);

    currentRequest.setAttribute(RemMePrincipal.class.getName(), remMePrincipal);

    // RequestPrincipal(principal, rememberMe=true, re-authentication=false)
    var principal = authResult.getPrincipal();
    var callerPrincipal = new RequestPrincipal(principal, true, false);

    return new CredentialValidationResult(principal.getStoreId(), callerPrincipal,
        (String) principal.get(AuthPrincipal.ATTRIBUTE_LDAP_DN), principal.getCallerUniqueId(), authResult.getGroups());
  }

  @Override
  public void removeLoginToken(String token) {
    var seriesToken = decodeToken(token);

    if ((seriesToken != null) && (seriesToken.getSeries() != null)) {
      remMeTokenHandler.remove(seriesToken.getSeries());
    }
  }

  protected String encodeToken(SeriesToken token) {
    return BaseEncoder.BASE64_URL_NP.encode(jsonProcessor.toString(token).getBytes(StandardCharsets.UTF_8));
  }

  protected SeriesToken decodeToken(String token) {
    try {
      return jsonProcessor.read(
          new StringReader(new String(BaseEncoder.BASE64_URL_NP.decode(token), StandardCharsets.UTF_8)),
          SeriesToken.class);
    } catch (JsonException ex) {
      return null;
    }
  }

  protected void logAuthEvent(RemMeToken remMeToken, String authResult, Instant authAtUtc, String callerUniqueId) {
    var event = AuthEventPublisher.newBaseAuthEvent(currentRequest);

    event.setIdentity(remMeToken.getIdentity());
    event.setModule(remMeToken.getModule());

    event.setAuthType(AuthEvent.AUTH_TYPE_REMEMBER_ME);
    event.setAuthResult(authResult);
    event.setSeries(remMeToken.getSeries());
    event.setCallerUniqueId(callerUniqueId);

    event.setAuthAtUtc(authAtUtc);

    authEventPublisher.fire(event);
  }

  protected String getClientBoundData(HttpServletRequest request, boolean boundClientIp, boolean boundUserAgent) {
    return ServletUtils.getClientBoundData(request, boundClientIp, boundUserAgent);
  }
}
