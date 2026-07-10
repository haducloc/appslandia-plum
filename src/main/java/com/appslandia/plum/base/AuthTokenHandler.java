// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

import com.appslandia.common.base.Out;
import com.appslandia.common.base.TextGenerator;
import com.appslandia.common.crypto.TextDigester;
import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.DateUtils;
import com.appslandia.common.utils.STR;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;

/**
 *
 * @author Loc Ha
 *
 */
public abstract class AuthTokenHandler {

  public static final String CONFIG_EXPIRY_LEEWAY_MS = AuthTokenHandler.class.getName() + ".expiry_leeway_ms";

  @Inject
  protected AppConfig appConfig;

  @Inject
  protected AppLogger appLogger;

  @Inject
  protected AuthTokenManager authTokenManager;

  protected int expiryLeewayMs;

  @PostConstruct
  protected void initialize() {
    expiryLeewayMs = appConfig.getInt(CONFIG_EXPIRY_LEEWAY_MS, 0);

    appLogger.info(STR.fmt("{}: {}", CONFIG_EXPIRY_LEEWAY_MS, expiryLeewayMs));
  }

  protected abstract TextGenerator getTokenGenerator();

  protected abstract TextDigester getTokenDigester();

  public SeriesToken saveToken(String module, String identity, String code, String clientBoundData, int expiresInSec) {
    Arguments.notNull(module);
    Arguments.isTrue(expiresInSec > 0);

    if (identity != null) {
      identity = identity.toLowerCase(Locale.ENGLISH);
    }

    // AuthToken
    var authToken = new AuthToken();

    var issuedAtUtc = Instant.now();
    var expiresAtUtc = issuedAtUtc.plusSeconds(expiresInSec);

    var clearToken = getTokenGenerator().generate();
    var tokenData = getTokenData(module, identity, clearToken, code, clientBoundData, issuedAtUtc, expiresAtUtc);

    authToken.setHashToken(getTokenDigester().digest(tokenData));

    authToken.setIdentity(identity);
    authToken.setModule(module);

    authToken.setIssuedAtUtc(issuedAtUtc);
    authToken.setExpiresAtUtc(expiresAtUtc);

    // Save Token
    authTokenManager.save(authToken);

    return new SeriesToken().setSeries(authToken.getSeries()).setToken(clearToken);
  }

  public AuthToken verifyToken(String module, UUID series, String token, String code, String clientBoundData,
      Out<String> invalidCode) {
    Arguments.notNull(module);
    Arguments.notNull(series);
    Arguments.notNull(token);

    // AuthToken
    var authToken = authTokenManager.load(series);

    if (authToken == null) {
      invalidCode.value = AuthResult.TOKEN_INVALID;
      return null;
    }

    // Verify Token
    var tokenData = getTokenData(authToken.getModule(), authToken.getIdentity(), token, code, clientBoundData,
        authToken.getIssuedAtUtc(), authToken.getExpiresAtUtc());

    if (!getTokenDigester().verify(tokenData, authToken.getHashToken())) {
      invalidCode.value = AuthResult.TOKEN_INVALID;
      return authToken;
    }

    // ExpiresAtUtc
    if (DateUtils.between(authToken.getExpiresAtUtc(), Instant.now()).toMillis() >= expiryLeewayMs) {
      invalidCode.value = AuthResult.TOKEN_EXPIRED;
      return authToken;
    }

    // Module
    if (!authToken.getModule().equals(module)) {
      invalidCode.value = AuthResult.TOKEN_MODULE_INVALID;
      return authToken;
    }

    return authToken;
  }

  protected String getTokenData(String module, String identity, String token, String code, String clientBoundData,
      Instant issuedAtUtc, Instant expiresAtUtc) {

    return String.join("|", module, String.valueOf(identity), token, String.valueOf(code),
        String.valueOf(clientBoundData), Long.toString(issuedAtUtc.toEpochMilli()),
        Long.toString(expiresAtUtc.toEpochMilli()));
  }

  public void remove(UUID series) {
    authTokenManager.remove(series);
  }
}
