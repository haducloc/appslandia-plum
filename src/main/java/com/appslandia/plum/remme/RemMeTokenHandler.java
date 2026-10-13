// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.remme;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

import com.appslandia.common.base.BinaryEncoder;
import com.appslandia.common.base.Out;
import com.appslandia.common.crypto.EncodedVerifier;
import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.DateUtils;
import com.appslandia.common.utils.StringUtils;
import com.appslandia.plum.base.AuthResult;
import com.appslandia.plum.base.SeriesToken;

import jakarta.inject.Inject;

/**
 *
 * @author Loc Ha
 *
 */
public abstract class RemMeTokenHandler {

  public static final String KEY_FAMILY_REMME_TOKEN = "remember_me_token";
  public static final String KEY_FAMILY_REMME_INTEGRITY = "remember_me_integrity";

  @Inject
  protected RemMeConfig remMeConfig;

  @Inject
  protected RemMeTokenManager remMeTokenManager;

  protected UUID generateSeries() {
    return UUID.randomUUID();
  }

  /**
   * @param keyFamily the key family identifier; always {@link RemMeTokenHandler#KEY_FAMILY_REMME_TOKEN}
   * 
   */
  protected abstract EncodedVerifier getTokenVerifier(String module, String keyFamily);

  /**
   * @param keyFamily the key family identifier; always {@link RemMeTokenHandler#KEY_FAMILY_REMME_INTEGRITY}
   * 
   */
  protected abstract EncodedVerifier getIntegrityVerifier(String module, String keyFamily);

  public SeriesToken saveToken(String module, String identity, String clientBoundData, int expiresInSec) {
    Arguments.notNull(module);
    Arguments.notNull(identity);
    Arguments.isTrue(expiresInSec > 0);

    identity = identity.toLowerCase(Locale.ROOT);

    // RemMeToken
    var remMeToken = new RemMeToken();
    var series = generateSeries();
    remMeToken.setSeries(series);

    var clearToken = remMeConfig.getTokenGenerator(module).generate();
    remMeToken.setTokenHash(getTokenVerifier(module, KEY_FAMILY_REMME_TOKEN).computeUtf8(clearToken));

    remMeToken.setModule(module);
    remMeToken.setIdentity(identity);

    var issuedAtUtc = Instant.now();
    var expiresAtUtc = issuedAtUtc.plusSeconds(expiresInSec);

    var integrityData = getIntegrityData(module, identity, series, clientBoundData, issuedAtUtc, expiresAtUtc);
    remMeToken.setIntegrityHash(getIntegrityVerifier(module, KEY_FAMILY_REMME_INTEGRITY).compute(integrityData));

    remMeToken.setIssuedAtUtc(issuedAtUtc);
    remMeToken.setExpiresAtUtc(expiresAtUtc);

    // Save Token
    remMeTokenManager.insert(remMeToken);

    return new SeriesToken().setSeries(remMeToken.getSeries()).setToken(clearToken);
  }

  public RemMeToken verifyToken(String module, UUID series, String token, String clientBoundData,
      Out<String> invalidCode) {

    Arguments.notNull(module);
    Arguments.notNull(series);
    Arguments.notNull(token);
    Arguments.notNull(invalidCode);

    // RemMeToken
    var remMeToken = remMeTokenManager.load(series);

    if (remMeToken == null) {
      invalidCode.value = AuthResult.TOKEN_INVALID;
      return null;
    }
    Asserts.isTrue(remMeToken.getModule().equals(module), "module is mismatched.");

    // Token
    if (!getTokenVerifier(module, KEY_FAMILY_REMME_TOKEN).verifyUtf8(token, remMeToken.getTokenHash())) {
      invalidCode.value = AuthResult.TOKEN_COMPROMISED;
      return remMeToken;
    }

    // ExpiresAtUtc
    if (DateUtils.between(remMeToken.getExpiresAtUtc(), Instant.now()).toMillis() >= remMeConfig
        .getRemMeExpiryLeewayMs(module)) {
      invalidCode.value = AuthResult.TOKEN_EXPIRED;
      return remMeToken;
    }

    // Integrity
    var integrityData = getIntegrityData(module, remMeToken.getIdentity(), remMeToken.getSeries(), clientBoundData,
        remMeToken.getIssuedAtUtc(), remMeToken.getExpiresAtUtc());

    if (!getIntegrityVerifier(module, KEY_FAMILY_REMME_INTEGRITY).verify(integrityData,
        remMeToken.getIntegrityHash())) {
      invalidCode.value = AuthResult.TOKEN_INVALID;
      return remMeToken;
    }

    return remMeToken;
  }

  public String reissueToken(String module, String identity, UUID series, String clientBoundData, Instant issuedAtUtc,
      int expiresInSec) {

    Arguments.notNull(module);
    Arguments.notNull(identity);
    Arguments.notNull(series);
    Arguments.notNull(issuedAtUtc);
    Arguments.isTrue(expiresInSec >= 0);

    identity = identity.toLowerCase(Locale.ROOT);

    // Token
    var clearToken = remMeConfig.getTokenGenerator(module).generate();
    var tokenHash = getTokenVerifier(module, KEY_FAMILY_REMME_TOKEN).computeUtf8(clearToken);

    // Integrity
    var expiresAtUtc = issuedAtUtc.plusSeconds(expiresInSec);

    var integrityData = getIntegrityData(module, identity, series, clientBoundData, issuedAtUtc, expiresAtUtc);
    var integrityHash = getIntegrityVerifier(module, KEY_FAMILY_REMME_INTEGRITY).compute(integrityData);

    remMeTokenManager.update(series, tokenHash, integrityHash, issuedAtUtc, expiresAtUtc);
    return clearToken;
  }

  protected byte[] getIntegrityData(String module, String identity, UUID series, String clientBoundData,
      Instant issuedAtUtc, Instant expiresAtUtc) {

    var capacity = StringUtils.computeLength(module, identity, clientBoundData) + 128;

    return new BinaryEncoder(capacity).writeString(module).writeString(identity).writeUuid(series)
        .writeString(clientBoundData).writeInstant(issuedAtUtc).writeInstant(expiresAtUtc).toByteArray();
  }

  public void remove(UUID series) {
    remMeTokenManager.remove(series);
  }

  public void removeAll(String identity) {
    remMeTokenManager.removeAll(identity);
  }
}
