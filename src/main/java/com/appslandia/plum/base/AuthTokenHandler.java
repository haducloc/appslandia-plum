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

import java.util.Locale;
import java.util.UUID;

import com.appslandia.common.base.Out;
import com.appslandia.common.base.TextGenerator;
import com.appslandia.common.crypto.TextDigester;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.DateUtils;

import jakarta.inject.Inject;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public abstract class AuthTokenHandler {

  @Inject
  protected AuthTokenManager authTokenManager;

  protected abstract TextGenerator getTokenGenerator();

  protected abstract TextDigester getTokenDigester();

  public SeriesToken saveToken(String identity, String module, String tokenBoundData, long expiresInMs, long issuedAt) {
    Asserts.notNull(module);
    if (identity != null) {
      identity = identity.toLowerCase(Locale.ENGLISH);
    }

    // AuthToken
    AuthToken authToken = new AuthToken();
    String clearToken = getTokenGenerator().generate();
    long expiresAt = issuedAt + expiresInMs;

    String tokenData = getTokenData(clearToken, identity, module, tokenBoundData, expiresAt, issuedAt);
    authToken.setHashToken(getTokenDigester().digest(tokenData));

    authToken.setIdentity(identity);
    authToken.setModule(module);

    authToken.setExpiresAt(expiresAt);
    authToken.setIssuedAt(issuedAt);

    // Save Token
    this.authTokenManager.save(authToken);
    return new SeriesToken().setSeries(authToken.getSeries()).setToken(clearToken);
  }

  public AuthToken verifyToken(UUID series, String token, String module, String tokenBoundData, int expiryLeewayMs,
      Out<String> invalidCode) {
    Asserts.notNull(series);
    Asserts.notNull(token);
    Asserts.notNull(module);

    // AuthToken
    AuthToken authToken = this.authTokenManager.load(series);
    if (authToken == null) {
      invalidCode.value = InvalidAuthResult.TOKEN_INVALID.getCode();
      return null;
    }

    // Verify Token
    String tokenData = getTokenData(token, authToken.getIdentity(), authToken.getModule(), tokenBoundData,
        authToken.getExpiresAt(), authToken.getIssuedAt());

    if (!getTokenDigester().verify(tokenData, authToken.getHashToken())) {
      invalidCode.value = InvalidAuthResult.TOKEN_INVALID.getCode();
      return authToken;
    }

    // ExpiresAt
    if (!DateUtils.isFutureTime(authToken.getExpiresAt(), expiryLeewayMs)) {
      invalidCode.value = InvalidAuthResult.TOKEN_EXPIRED.getCode();
      return authToken;
    }

    // Module
    if (!authToken.getModule().equals(module)) {
      invalidCode.value = InvalidAuthResult.TOKEN_MODULE_MISMATCH.getCode();
      return authToken;
    }
    return authToken;
  }

  protected String getTokenData(String token, String identity, String module, String tokenBoundData, long expiresAt,
      long issuedAt) {
    return String.join("|", token, identity, module, tokenBoundData, Long.toString(expiresAt), Long.toString(issuedAt));
  }

  public void remove(UUID series) {
    this.authTokenManager.remove(series);
  }
}
