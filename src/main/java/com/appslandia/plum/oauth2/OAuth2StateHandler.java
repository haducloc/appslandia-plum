// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.oauth2;

import java.time.Instant;
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
public abstract class OAuth2StateHandler {

  public static final String KEY_FAMILY_OAUTH2_INTEGRITY = "oauth2_integrity";

  @Inject
  protected OAuth2StateManager stateManager;

  @Inject
  protected OAuth2ConfigProvider configProvider;

  protected OAuth2Config getConfig(String provider) {
    return configProvider.getConfig(provider);
  }

  protected UUID generateSeries() {
    return UUID.randomUUID();
  }

  /**
   * @param keyFamily the key family identifier; always {@link OAuth2StateHandler#KEY_FAMILY_OAUTH2_INTEGRITY}
   * 
   */
  protected abstract EncodedVerifier getIntegrityVerifier(String module, String keyFamily);

  public SeriesToken createState(String module, String provider, String returnUrl, String clientBoundData,
      Out<String> codeVerifierOut) {

    Arguments.notNull(module);
    Arguments.notNull(provider);
    Arguments.notNull(codeVerifierOut);

    var config = getConfig(provider);

    // OAuth2State
    var state = new OAuth2State();

    var series = generateSeries();
    state.setSeries(series);

    state.setModule(module);
    state.setProvider(provider);
    state.setReturnUrl(returnUrl);

    // PKCE
    var codeVerifier = config.getCodeVerifierGenerator(module).generate();
    state.setCodeVerifier(codeVerifier);

    // Lifetime
    var issuedAtUtc = Instant.now();
    var expiresAtUtc = issuedAtUtc.plusSeconds(config.getStateExpiresInSec(module));

    state.setIssuedAtUtc(issuedAtUtc);
    state.setExpiresAtUtc(expiresAtUtc);

    // Integrity
    var clearToken = config.getStateTokenGenerator(module).generate();
    var integrityData = getIntegrityData(module, provider, series, clearToken, codeVerifier, returnUrl, clientBoundData,
        issuedAtUtc, expiresAtUtc);

    state.setIntegrityHash(getIntegrityVerifier(module, KEY_FAMILY_OAUTH2_INTEGRITY).compute(integrityData));

    // Save State
    stateManager.insert(state);

    codeVerifierOut.value = codeVerifier;
    return new SeriesToken().setSeries(series).setToken(clearToken);
  }

  public OAuth2State verifyState(String module, String provider, UUID series, String token, String clientBoundData,
      Out<String> invalidCode) {

    Arguments.notNull(module);
    Arguments.notNull(provider);
    Arguments.notNull(series);
    Arguments.notNull(token);
    Arguments.notNull(invalidCode);

    var config = getConfig(provider);

    // OAuth2State
    var state = stateManager.load(series);

    if (state == null) {
      invalidCode.value = AuthResult.TOKEN_INVALID;
      return null;
    }

    Asserts.isTrue(state.getModule().equals(module), "module is mismatched.");
    Asserts.isTrue(state.getProvider().equals(provider), "provider is mismatched.");

    // ExpiresAtUtc
    if (DateUtils.between(state.getExpiresAtUtc(), Instant.now()).toMillis() >= config.getStateExpiryLeewayMs(module)) {
      invalidCode.value = AuthResult.TOKEN_EXPIRED;
      return state;
    }

    // Integrity
    var integrityData = getIntegrityData(state.getModule(), state.getProvider(), state.getSeries(), token,
        state.getCodeVerifier(), state.getReturnUrl(), clientBoundData, state.getIssuedAtUtc(),
        state.getExpiresAtUtc());

    if (!getIntegrityVerifier(module, KEY_FAMILY_OAUTH2_INTEGRITY).verify(integrityData, state.getIntegrityHash())) {
      invalidCode.value = AuthResult.TOKEN_INVALID;
      return state;
    }
    return state;
  }

  protected byte[] getIntegrityData(String module, String provider, UUID series, String token, String codeVerifier,
      String returnUrl, String clientBoundData, Instant issuedAtUtc, Instant expiresAtUtc) {

    var capacity = StringUtils.computeLength(module, provider, token, codeVerifier, returnUrl, clientBoundData) + 128;

    return new BinaryEncoder(capacity).writeString(module).writeString(provider).writeUuid(series).writeString(token)
        .writeString(codeVerifier).writeString(returnUrl).writeString(clientBoundData).writeInstant(issuedAtUtc)
        .writeInstant(expiresAtUtc).toByteArray();
  }

  public void remove(UUID series) {
    stateManager.remove(series);
  }
}
