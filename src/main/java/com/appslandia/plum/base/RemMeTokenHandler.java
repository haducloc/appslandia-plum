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

import java.time.LocalDateTime;
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
public abstract class RemMeTokenHandler {

  @Inject
  protected RemMeTokenManager remMeTokenManager;

  protected abstract TextGenerator getTokenGenerator();

  protected abstract TextDigester getTokenDigester();

  public SeriesToken saveToken(String identity, String module, String clientData, int expiresInSec) {
    Asserts.notNull(identity);
    Asserts.notNull(module);
    identity = identity.toLowerCase(Locale.ENGLISH);

    // RemMeToken
    RemMeToken remMeToken = new RemMeToken();
    LocalDateTime issuedAtUtc = DateUtils.timeAtUtcF3();
    LocalDateTime expiresAtUtc = issuedAtUtc.plusSeconds(expiresInSec);

    String clearToken = getTokenGenerator().generate();
    String tokenData = getTokenData(clearToken, identity, module, issuedAtUtc, expiresAtUtc, clientData);
    remMeToken.setHashToken(getTokenDigester().digest(tokenData));

    remMeToken.setIdentity(identity);
    remMeToken.setModule(module);

    remMeToken.setIssuedAtUtc(issuedAtUtc);
    remMeToken.setExpiresAtUtc(expiresAtUtc);

    // Save Token
    this.remMeTokenManager.save(remMeToken);
    return new SeriesToken().setSeries(remMeToken.getSeries()).setToken(clearToken);
  }

  public RemMeToken verifyToken(UUID series, String token, String module, String clientData, int expiryLeewayMs,
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
    String tokenData = getTokenData(token, remMeToken.getIdentity(), remMeToken.getModule(),
        remMeToken.getIssuedAtUtc(), remMeToken.getExpiresAtUtc(), clientData);

    if (!getTokenDigester().verify(tokenData, remMeToken.getHashToken())) {
      invalidCode.value = InvalidAuthResult.TOKEN_COMPROMISED.getCode();
      return remMeToken;
    }

    // ExpiresAtUtc
    if (DateUtils.betweenMs(remMeToken.getExpiresAtUtc(), DateUtils.timeAtUtcF3()) > expiryLeewayMs) {
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

  public String reissue(UUID series, String identity, String module, String clientData, LocalDateTime issuedAtUtc,
      int expiresInSec) {
    LocalDateTime expiresAtUtc = issuedAtUtc.plusSeconds(expiresInSec);
    String newToken = this.getTokenGenerator().generate();

    String newTokenData = this.getTokenData(newToken, identity, module, issuedAtUtc, expiresAtUtc, clientData);
    String newHashToken = this.getTokenDigester().digest(newTokenData);

    this.remMeTokenManager.update(series, newHashToken, issuedAtUtc, expiresAtUtc);
    return newToken;
  }

  protected String getTokenData(String token, String identity, String module, LocalDateTime issuedAtUtc,
      LocalDateTime expiresAtUtc, String clientData) {
    return String.join("|", token, identity, module, issuedAtUtc.toString(), expiresAtUtc.toString(), clientData);
  }

  public void remove(UUID series) {
    this.remMeTokenManager.remove(series);
  }

  public void removeAll(String identity) {
    this.remMeTokenManager.removeAll(identity);
  }
}
