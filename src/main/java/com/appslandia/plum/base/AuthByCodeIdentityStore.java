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

import java.util.Locale;

import com.appslandia.common.base.Out;
import com.appslandia.common.utils.Asserts;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.inject.Inject;
import jakarta.security.enterprise.credential.Credential;
import jakarta.servlet.http.HttpServletRequest;

/**
 *
 * @author Loc Ha
 *
 */
public abstract class AuthByCodeIdentityStore extends IdentityStoreBase implements AuthByCodeTokenHandler {

  public static final String CONFIG_EXPIRY_LEEWAY_MS = AuthByCodeIdentityStore.class.getName() + ".expiry_leeway_ms";
  public static final String CONFIG_TOKEN_CLIENT_IP = AuthByCodeIdentityStore.class.getName() + ".token_client_ip";
  public static final String CONFIG_TOKEN_USER_AGENT = AuthByCodeIdentityStore.class.getName() + ".token_user_agent";

  @Inject
  protected AppConfig appConfig;

  @Inject
  protected AuthTokenHandler authTokenHandler;

  @Inject
  protected IdentityHandler identityHandler;

  @Inject
  protected HttpServletRequest currentRequest;

  protected int getExpiryLeewayMs() {
    return this.appConfig.getInt(CONFIG_EXPIRY_LEEWAY_MS, 0);
  }

  protected boolean getTokenClientIp() {
    return this.appConfig.getBool(CONFIG_TOKEN_CLIENT_IP, true);
  }

  protected boolean getTokenUserAgent() {
    return this.appConfig.getBool(CONFIG_TOKEN_USER_AGENT, true);
  }

  protected String getClientData() {
    var clientIp = getTokenClientIp() ? ServletUtils.getClientIp(this.currentRequest) : "IP";
    var userAgent = getTokenUserAgent() ? ServletUtils.getUserAgent(this.currentRequest) : "UA";
    return clientIp + "," + userAgent;
  }

  @Override
  protected RolesPrincipal doValidate(String module, Credential credential, Out<String> invalidCode) {
    Asserts.isTrue(credential instanceof AuthByCodeCredential,
        "The given credential must be an instance of AuthByCodeCredential.");

    var authByCodeCredential = (AuthByCodeCredential) credential;
    var tokenBoundData = getTokenData(authByCodeCredential.getCode(), getClientData());

    // Verify Token
    var authToken = this.authTokenHandler.verifyToken(authByCodeCredential.getSeries(), authByCodeCredential.getToken(),
        module, tokenBoundData, getExpiryLeewayMs(), invalidCode);

    if (invalidCode.value != null) {
      return null;
    }
    return this.identityHandler.validateIdentity(module, authToken.getIdentity(), invalidCode);
  }

  @Override
  public SeriesToken saveToken(String identity, String module, String code, int expiresInSec) {
    var tokenBoundData = getTokenData(code, getClientData());
    return this.authTokenHandler.saveToken(identity, module, tokenBoundData, expiresInSec);
  }

  protected String getTokenData(String code, String clientData) {
    return code.toLowerCase(Locale.ROOT) + "," + clientData;
  }
}
