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

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

import com.appslandia.common.base.Out;
import com.appslandia.common.base.TextGenerator;
import com.appslandia.common.crypto.TextDigester;
import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.DateUtils;

import jakarta.inject.Inject;

/**
 *
 * @author Loc Ha
 *
 */
public abstract class AuthTokenHandler {

  @Inject
  protected AuthTokenManager authTokenManager;

  protected abstract TextGenerator getTokenGenerator();

  protected abstract TextDigester getTokenDigester();

  public SeriesToken saveToken(String identity, String module, String tokenBoundData, int expiresInSec) {
    Arguments.notNull(module);
    if (identity != null) {
      identity = identity.toLowerCase(Locale.ENGLISH);
    }

    // AuthToken
    var authToken = new AuthToken();
    var issuedAtUtc = DateUtils.timeAtUtcF3();
    var expiresAtUtc = issuedAtUtc.plusSeconds(expiresInSec);

    var clearToken = getTokenGenerator().generate();
    var tokenData = getTokenData(clearToken, identity, module, issuedAtUtc, expiresAtUtc, tokenBoundData);
    authToken.setHashToken(getTokenDigester().digest(tokenData));

    authToken.setIdentity(identity);
    authToken.setModule(module);

    authToken.setIssuedAtUtc(issuedAtUtc);
    authToken.setExpiresAtUtc(expiresAtUtc);

    // Save Token
    this.authTokenManager.save(authToken);
    return new SeriesToken().setSeries(authToken.getSeries()).setToken(clearToken);
  }

  public AuthToken verifyToken(UUID series, String token, String module, String tokenBoundData, int expiryLeewayMs,
      Out<String> invalidCode) {
    Arguments.notNull(series);
    Arguments.notNull(token);
    Arguments.notNull(module);

    // AuthToken
    var authToken = this.authTokenManager.load(series);
    if (authToken == null) {
      invalidCode.value = InvalidAuth.TOKEN_INVALID.getCode();
      return null;
    }

    // Verify Token
    var tokenData = getTokenData(token, authToken.getIdentity(), authToken.getModule(), authToken.getIssuedAtUtc(),
        authToken.getExpiresAtUtc(), tokenBoundData);

    if (!getTokenDigester().verify(tokenData, authToken.getHashToken())) {
      invalidCode.value = InvalidAuth.TOKEN_INVALID.getCode();
      return authToken;
    }

    // ExpiresAtUtc
    if (DateUtils.betweenMs(authToken.getExpiresAtUtc(), DateUtils.timeAtUtcF3()) > expiryLeewayMs) {
      invalidCode.value = InvalidAuth.TOKEN_EXPIRED.getCode();
      return authToken;
    }

    // Module
    if (!authToken.getModule().equals(module)) {
      invalidCode.value = InvalidAuth.TOKEN_MODULE_MISMATCH.getCode();
      return authToken;
    }
    return authToken;
  }

  protected String getTokenData(String token, String identity, String module, LocalDateTime issuedAtUtc,
      LocalDateTime expiresAtUtc, String tokenBoundData) {
    return String.join("|", token, identity, module, issuedAtUtc.toString(), expiresAtUtc.toString(), tokenBoundData);
  }

  public void remove(UUID series) {
    this.authTokenManager.remove(series);
  }
}
