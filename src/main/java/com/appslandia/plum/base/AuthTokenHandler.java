// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

import com.appslandia.common.base.BinaryEncoder;
import com.appslandia.common.base.Out;
import com.appslandia.common.base.TokenGenerator;
import com.appslandia.common.crypto.EncodedVerifier;
import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.DateUtils;
import com.appslandia.common.utils.StringUtils;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;

/**
 *
 * @author Loc Ha
 *
 */
public abstract class AuthTokenHandler {

  public static final String CONFIG_TOKEN_LENGTH = "auth_token.token_length";

  @Inject
  protected AppConfig appConfig;

  @Inject
  protected AppLogger appLogger;

  @Inject
  protected AuthTokenManager authTokenManager;

  protected TokenGenerator tokenGenerator;

  @PostConstruct
  protected void initialize() {
    var tokenLength = appConfig.getInt(CONFIG_TOKEN_LENGTH, 64);

    appLogger.info("{}: {}", CONFIG_TOKEN_LENGTH, tokenLength);
    Arguments.isTrue(tokenLength >= 32);

    tokenGenerator = new TokenGenerator(tokenLength);
  }

  protected UUID generateSeries() {
    return UUID.randomUUID();
  }

  protected abstract EncodedVerifier getCodeVerifier(String module, String keyFamily);

  protected abstract EncodedVerifier getIntegrityVerifier(String module, String keyFamily);

  public SeriesToken saveToken(String module, String purposeId, String identity, String data, String clientBoundData,
      int expiresInSec, String code, Integer maxCodeAttempts) {

    Arguments.notNull(module);
    Arguments.notNull(purposeId);
    Arguments.isTrue(expiresInSec > 0);
    Arguments.isTrue(code == null || (maxCodeAttempts != null && maxCodeAttempts > 0));

    if (identity != null) {
      identity = identity.toLowerCase(Locale.ROOT);
    }

    // AuthToken
    var authToken = new AuthToken();
    var series = generateSeries();
    authToken.setSeries(series);

    var issuedAtUtc = Instant.now();
    var expiresAtUtc = issuedAtUtc.plusSeconds(expiresInSec);

    var clearToken = tokenGenerator.generate();
    var integrityData = getIntegrityData(module, identity, series, clearToken, data, clientBoundData, issuedAtUtc,
        expiresAtUtc, maxCodeAttempts);

    var integrityKeyFamily = getIntegrityKeyFamily(purposeId);
    var integrityHash = getIntegrityVerifier(module, integrityKeyFamily).compute(integrityData);
    authToken.setIntegrityHash(integrityHash);

    authToken.setModule(module);
    authToken.setIdentity(identity);
    authToken.setData(data);

    // Code
    if (code != null) {
      Arguments.isTrue(maxCodeAttempts > 0);

      var codeKeyFamily = getCodeKeyFamily(purposeId);
      var codeHash = getCodeVerifier(module, codeKeyFamily).computeUtf8(code);
      authToken.setCodeHash(codeHash);

      authToken.setCodeAttempts(0);
      authToken.setMaxAttempts(maxCodeAttempts);
    }

    authToken.setIssuedAtUtc(issuedAtUtc);
    authToken.setExpiresAtUtc(expiresAtUtc);

    // Save Token
    authTokenManager.insert(authToken);

    return new SeriesToken().setSeries(authToken.getSeries()).setToken(clearToken);
  }

  public AuthToken verifyToken(String module, String purposeId, UUID series, String token, String code,
      String clientBoundData, long expiryLeewayMs, Out<String> invalidCode) {

    Arguments.notNull(module);
    Arguments.notNull(purposeId);
    Arguments.notNull(series);
    Arguments.notNull(token);
    Arguments.isTrue(expiryLeewayMs >= 0);
    Arguments.notNull(invalidCode);

    // AuthToken
    var authToken = authTokenManager.load(series);

    if (authToken == null) {
      invalidCode.value = AuthResult.TOKEN_INVALID;
      return null;
    }
    Asserts.isTrue(authToken.getModule().equals(module), "module is mismatched.");

    // Code Attempt Limit
    if (authToken.getCodeHash() != null) {
      Asserts.notNull(code, "code is required.");

      var codeAttempts = Asserts.notNull(authToken.getCodeAttempts());
      var maxAttempts = Asserts.notNull(authToken.getMaxAttempts());

      if (codeAttempts >= maxAttempts) {
        invalidCode.value = AuthResult.CODE_ATTEMPTS_EXCEEDED;
        return authToken;
      }
    }

    // ExpiresAtUtc
    if (DateUtils.between(authToken.getExpiresAtUtc(), Instant.now()).toMillis() >= expiryLeewayMs) {
      invalidCode.value = AuthResult.TOKEN_EXPIRED;
      return authToken;
    }

    // Integrity
    var integrityData = getIntegrityData(module, authToken.getIdentity(), series, token, authToken.getData(),
        clientBoundData, authToken.getIssuedAtUtc(), authToken.getExpiresAtUtc(), authToken.getMaxAttempts());

    var integrityKeyFamily = getIntegrityKeyFamily(purposeId);
    if (!getIntegrityVerifier(module, integrityKeyFamily).verify(integrityData, authToken.getIntegrityHash())) {
      invalidCode.value = AuthResult.TOKEN_INVALID;
      return authToken;
    }

    // Code
    if (authToken.getCodeHash() != null) {
      var codeKeyFamily = getCodeKeyFamily(purposeId);

      if (!getCodeVerifier(module, codeKeyFamily).verifyUtf8(code, authToken.getCodeHash())) {
        invalidCode.value = AuthResult.CODE_INVALID;

        var codeAttempts = Asserts.notNull(authToken.getCodeAttempts());
        authTokenManager.updateCodeAttempts(series, codeAttempts + 1);
        return authToken;
      }
    }
    return authToken;
  }

  public void remove(UUID series) {
    authTokenManager.remove(series);
  }

  protected byte[] getIntegrityData(String module, String identity, UUID series, String token, String data,
      String clientBoundData, Instant issuedAtUtc, Instant expiresAtUtc, Integer maxCodeAttempts) {

    var capacity = StringUtils.computeLength(module, identity, token, data, clientBoundData) + 128;

    return new BinaryEncoder(capacity).writeString(module).writeString(identity).writeUuid(series).writeString(token)
        .writeString(data).writeString(clientBoundData).writeInstant(issuedAtUtc).writeInstant(expiresAtUtc)
        .writeInt(maxCodeAttempts).toByteArray();
  }

  protected String getCodeKeyFamily(String purposeId) {
    return purposeId + "_code";
  }

  protected String getIntegrityKeyFamily(String purposeId) {
    return purposeId + "_integrity";
  }
}