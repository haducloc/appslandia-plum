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
public abstract class RemMeTokenHandler {

  public static final String CONFIG_EXPIRY_LEEWAY_MS = RemMeTokenHandler.class.getName() + ".expiry_leeway_ms";

  @Inject
  protected AppConfig appConfig;

  @Inject
  protected AppLogger appLogger;

  @Inject
  protected RemMeTokenManager remMeTokenManager;

  protected int expiryLeewayMs;

  @PostConstruct
  protected void initialize() {
    expiryLeewayMs = appConfig.getInt(CONFIG_EXPIRY_LEEWAY_MS, 0);
    appLogger.info(STR.fmt("{}: {}", CONFIG_EXPIRY_LEEWAY_MS, expiryLeewayMs));
  }

  protected abstract TextGenerator getTokenGenerator();

  protected abstract TextDigester getTokenDigester();

  public SeriesToken saveToken(String module, String identity, String clientBoundData, int expiresInSec) {
    Arguments.notNull(module);
    Arguments.notNull(identity);
    Arguments.isTrue(expiresInSec > 0);

    identity = identity.toLowerCase(Locale.ENGLISH);

    // RemMeToken
    var remMeToken = new RemMeToken();
    var issuedAtUtc = Instant.now();
    var expiresAtUtc = issuedAtUtc.plusSeconds(expiresInSec);

    var clearToken = getTokenGenerator().generate();
    var tokenData = getTokenData(module, identity, clearToken, clientBoundData, issuedAtUtc, expiresAtUtc);
    remMeToken.setHashToken(getTokenDigester().digest(tokenData));

    remMeToken.setIdentity(identity);
    remMeToken.setModule(module);

    remMeToken.setIssuedAtUtc(issuedAtUtc);
    remMeToken.setExpiresAtUtc(expiresAtUtc);

    // Save Token
    remMeTokenManager.save(remMeToken);
    return new SeriesToken().setSeries(remMeToken.getSeries()).setToken(clearToken);
  }

  public RemMeToken verifyToken(String module, UUID series, String token, String clientBoundData,
      Out<String> invalidCode) {
    Arguments.notNull(module);
    Arguments.notNull(series);
    Arguments.notNull(token);

    // RemMeToken
    var remMeToken = remMeTokenManager.load(series);
    if (remMeToken == null) {
      invalidCode.value = AuthResult.TOKEN_INVALID;
      return null;
    }

    // Verify Token
    var tokenData = getTokenData(remMeToken.getModule(), remMeToken.getIdentity(), token, clientBoundData,
        remMeToken.getIssuedAtUtc(), remMeToken.getExpiresAtUtc());

    if (!getTokenDigester().verify(tokenData, remMeToken.getHashToken())) {
      invalidCode.value = AuthResult.TOKEN_COMPROMISED;
      return remMeToken;
    }

    // ExpiresAtUtc
    if (DateUtils.between(remMeToken.getExpiresAtUtc(), Instant.now()).toMillis() >= expiryLeewayMs) {
      invalidCode.value = AuthResult.TOKEN_EXPIRED;
      return remMeToken;
    }

    // Module
    if (!remMeToken.getModule().equals(module)) {
      invalidCode.value = AuthResult.TOKEN_MODULE_INVALID;
      return remMeToken;
    }
    return remMeToken;
  }

  public String reissue(String module, UUID series, String identity, String clientBoundData, Instant issuedAtUtc,
      int expiresInSec) {
    Arguments.notNull(module);
    Arguments.notNull(series);
    Arguments.notNull(identity);
    Arguments.notNull(issuedAtUtc);
    Arguments.isTrue(expiresInSec > 0);

    var expiresAtUtc = issuedAtUtc.plusSeconds(expiresInSec);
    var newToken = getTokenGenerator().generate();

    var newTokenData = getTokenData(module, identity, newToken, clientBoundData, issuedAtUtc, expiresAtUtc);
    var newHashToken = getTokenDigester().digest(newTokenData);

    remMeTokenManager.update(series, newHashToken, issuedAtUtc, expiresAtUtc);
    return newToken;
  }

  protected String getTokenData(String module, String identity, String token, String clientBoundData,
      Instant issuedAtUtc, Instant expiresAtUtc) {

    return String.join("|", module, identity, token, String.valueOf(clientBoundData),
        Long.toString(issuedAtUtc.toEpochMilli()), Long.toString(expiresAtUtc.toEpochMilli()));
  }

  public void remove(UUID series) {
    remMeTokenManager.remove(series);
  }

  public void removeAll(String identity) {
    remMeTokenManager.removeAll(identity);
  }
}
