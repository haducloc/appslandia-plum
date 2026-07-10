// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;

import com.appslandia.common.base.BaseEncoder;
import com.appslandia.common.base.Out;
import com.appslandia.common.cdi.Json;
import com.appslandia.common.cdi.Json.Profile;
import com.appslandia.common.json.JsonProcessor;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.STR;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.annotation.PostConstruct;
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

  public static final String CONFIG_BOUND_CLIENT_IP = RemMeTokenIdentityStore.class.getName() + ".bound_client_ip";
  public static final String CONFIG_BOUND_USER_AGENT = RemMeTokenIdentityStore.class.getName() + ".bound_user_agent";

  @Inject
  protected AppConfig appConfig;

  @Inject
  protected AppLogger appLogger;

  @Inject
  protected RemMeTokenHandler remMeTokenHandler;

  @Inject
  protected AuthEventPublisher authEventPublisher;

  @Inject
  @Json(Profile.COMPACT)
  protected JsonProcessor jsonProcessor;

  @Inject
  protected HttpServletRequest currentRequest;

  protected boolean boundClientIp;
  protected boolean boundUserAgent;

  @PostConstruct
  protected void initialize() {
    boundClientIp = appConfig.getBool(CONFIG_BOUND_CLIENT_IP, false);
    boundUserAgent = appConfig.getBool(CONFIG_BOUND_USER_AGENT, true);

    appLogger.info(STR.fmt("{}: {}", CONFIG_BOUND_CLIENT_IP, boundClientIp));
    appLogger.info(STR.fmt("{}: {}", CONFIG_BOUND_USER_AGENT, boundUserAgent));
  }

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
    var expiresInSec = appConfig.getInt(AppConfig.REMEMBER_ME_COOKIE_AGE);

    // Save Token
    var clientBoundData = getClientBoundData(currentRequest, boundClientIp, boundUserAgent);
    var seriesToken = remMeTokenHandler.saveToken(principal.getModule(), identity, clientBoundData, expiresInSec);
    return encodeSeriesToken(seriesToken);
  }

  protected int computeExpiresInSec(Instant curExpiresAtUtc, Instant curTimeAtUtc) {
    var maxAge = appConfig.getInt(AppConfig.REMEMBER_ME_COOKIE_AGE);
    var remainingSec = (int) ChronoUnit.SECONDS.between(curTimeAtUtc, curExpiresAtUtc);

    if (remainingSec < 0) {
      remainingSec = 0;
    }

    if (!appConfig.getBool(AppConfig.REMEMBER_ME_COOKIE_SLIDING_EXP) || (remainingSec >= maxAge / 2)) {
      return remainingSec;
    }
    return maxAge;
  }

  protected abstract AuthResult doValidate(String module, String identity);

  @Override
  public CredentialValidationResult validate(RememberMeCredential credential) {

    // SeriesToken
    var seriesToken = decodeSeriesToken(credential.getToken());

    if ((seriesToken == null) || (seriesToken.getSeries() == null) || (seriesToken.getToken() == null)) {
      return AuthInvalidResult.toResult(AuthResult.TOKEN_INVALID);
    }

    var requestContext = ServletUtils.getRequestContext(currentRequest);
    var clientBoundData = getClientBoundData(currentRequest, boundClientIp, boundUserAgent);

    // RemMeToken
    var invalidCode = new Out<String>();

    var remMeToken = remMeTokenHandler.verifyToken(requestContext.getModule(), seriesToken.getSeries(),
        seriesToken.getToken(), clientBoundData, invalidCode);

    if (invalidCode.value != null) {
      if (AuthResult.TOKEN_COMPROMISED.equals(invalidCode.value)) {

        remMeTokenHandler.removeAll(remMeToken.getIdentity());
        logAuthEvent(remMeToken, AuthResult.TOKEN_COMPROMISED, Instant.now(), null);
      }
      return AuthInvalidResult.toResult(invalidCode.value);
    }

    // Validate
    var authResult = doValidate(remMeToken.getModule(), remMeToken.getIdentity());

    Asserts.notNull(authResult, "doValidate(module, identity) must not return null.");

    if (authResult == AuthResult.NOT_VALIDATED_RESULT) {
      return CredentialValidationResult.NOT_VALIDATED_RESULT;
    }

    if (authResult.getInvalidCode() != null) {
      return AuthInvalidResult.toResult(authResult.getInvalidCode());
    }

    // Issue New Token
    var curTimeAtUtc = Instant.now();
    var expiresInSec = computeExpiresInSec(remMeToken.getExpiresAtUtc(), curTimeAtUtc);

    var clearToken = remMeTokenHandler.reissue(remMeToken.getModule(), remMeToken.getSeries(), remMeToken.getIdentity(),
        clientBoundData, curTimeAtUtc, expiresInSec);

    // Log AuthEvent
    logAuthEvent(remMeToken, AuthEvent.AUTH_RESULT_SUCCESS, curTimeAtUtc,
        authResult.getPrincipal().getCallerUniqueId());

    // RemMePrincipal
    var encToken = encodeSeriesToken(new SeriesToken().setSeries(seriesToken.getSeries()).setToken(clearToken));
    var remMePrincipal = new RemMePrincipal(remMeToken.getModule(), encToken, remMeToken.getIdentity(), expiresInSec);

    currentRequest.setAttribute(RemMePrincipal.class.getName(), remMePrincipal);

    // RequestPrincipal(principal, rememberMe=true, re-authentication=false)
    var principal = authResult.getPrincipal();
    var callerPrincipal = new RequestPrincipal(principal, true, false);

    return new CredentialValidationResult(principal.getStoreId(), callerPrincipal,
        (String) principal.get(AuthPrincipal.ATTRIBUTE_LDAP_DN), principal.getCallerUniqueId(), authResult.getGroups());
  }

  @Override
  public void removeLoginToken(String token) {
    var seriesToken = decodeSeriesToken(token);

    if ((seriesToken != null) && (seriesToken.getSeries() != null)) {
      remMeTokenHandler.remove(seriesToken.getSeries());
    }
  }

  protected String encodeSeriesToken(SeriesToken token) {
    return BaseEncoder.BASE64_URL_NP.encode(jsonProcessor.toString(token).getBytes(StandardCharsets.UTF_8));
  }

  protected SeriesToken decodeSeriesToken(String token) {
    try {
      return jsonProcessor.read(
          new StringReader(new String(BaseEncoder.BASE64_URL_NP.decode(token), StandardCharsets.UTF_8)),
          SeriesToken.class);

    } catch (Exception ex) {
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
