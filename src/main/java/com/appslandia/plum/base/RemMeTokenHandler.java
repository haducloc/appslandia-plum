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
public abstract class RemMeTokenHandler {

  @Inject
  protected RemMeTokenManager remMeTokenManager;

  protected abstract TextGenerator getSeriesGenerator();

  protected abstract TextGenerator getTokenGenerator();

  protected abstract TextDigester getTokenDigester();

  public SeriesToken saveToken(String identity, String module, String tokenBoundData, long expiresInMs, long issuedAt) {
    Asserts.notNull(identity);
    Asserts.notNull(module);
    identity = identity.toLowerCase(Locale.ENGLISH);

    // RemMeToken
    RemMeToken remMeToken = new RemMeToken();
    remMeToken.setSeries(getSeriesGenerator().generate());

    String clearToken = getTokenGenerator().generate();
    long expiresAt = issuedAt + expiresInMs;

    String tokenData = getTokenData(remMeToken.getSeries(), clearToken, identity, module, tokenBoundData, expiresAt,
        issuedAt);
    remMeToken.setHashToken(getTokenDigester().digest(tokenData));

    remMeToken.setIdentity(identity);
    remMeToken.setModule(module);

    remMeToken.setExpiresAt(expiresAt);
    remMeToken.setIssuedAt(issuedAt);

    // Save Token
    this.remMeTokenManager.save(remMeToken);
    return new SeriesToken().setSeries(remMeToken.getSeries()).setToken(clearToken);
  }

  public RemMeToken verifyToken(String series, String token, String module, String tokenBoundData, int expiryLeewayMs,
      Out<String> invalidCode) {
    Asserts.notNull(series);
    Asserts.notNull(token);
    Asserts.notNull(module);

    // RemMeToken
    RemMeToken remMeToken = this.remMeTokenManager.load(series);
    if (remMeToken == null) {
      invalidCode.value = InvalidAuthResult.TOKEN_INVALID.getCode();
      return null;
    }

    // Verify Token
    String tokenData = getTokenData(remMeToken.getSeries(), token, remMeToken.getIdentity(), remMeToken.getModule(),
        tokenBoundData, remMeToken.getExpiresAt(), remMeToken.getIssuedAt());

    if (!getTokenDigester().verify(tokenData, remMeToken.getHashToken())) {
      invalidCode.value = InvalidAuthResult.TOKEN_COMPROMISED.getCode();
      return remMeToken;
    }

    // ExpiresAt
    if (!DateUtils.isFutureTime(remMeToken.getExpiresAt(), expiryLeewayMs)) {
      invalidCode.value = InvalidAuthResult.TOKEN_EXPIRED.getCode();
      return remMeToken;
    }

    // Module
    if (!remMeToken.getModule().equals(module)) {
      invalidCode.value = InvalidAuthResult.TOKEN_MODULE_MISMATCH.getCode();
      return remMeToken;
    }
    return remMeToken;
  }

  protected String getTokenData(String series, String token, String identity, String module, String tokenBoundData,
      long expiresAt, long issuedAt) {
    return String.join("|", series, token, identity, module, tokenBoundData, Long.toString(expiresAt),
        Long.toString(issuedAt));
  }

  public String reissue(String series, String identity, String module, String tokenBoundData, long expiresInMs,
      long issuedAt) {
    String newToken = this.getTokenGenerator().generate();
    long expiresAt = issuedAt + expiresInMs;
    String newTokenData = this.getTokenData(series, newToken, identity, module, tokenBoundData, expiresAt, issuedAt);

    String newHashToken = this.getTokenDigester().digest(newTokenData);
    this.remMeTokenManager.reissue(series, newHashToken, expiresInMs, issuedAt);
    return newToken;
  }

  public void remove(String series) {
    this.remMeTokenManager.remove(series);
  }

  public void onTokenCompromised(RemMeToken remMeToken) {
    this.remMeTokenManager.removeAll(remMeToken.getIdentity());
  }
}
