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

import com.appslandia.common.base.Out;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.STR;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.credential.Credential;
import jakarta.servlet.http.HttpServletRequest;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@ApplicationScoped
public class AuthByCodeIdentityStore extends IdentityStoreBase {

  public static final String CONFIG_EXPIRY_LEEWAY_MS = AuthByCodeIdentityStore.class.getName() + ".expiry_leeway_ms";

  public static final String CONFIG_TOKEN_BOUND_USER_AGENT = AuthByCodeIdentityStore.class.getName()
      + ".token_bound_user_agent";

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

  protected boolean getTokenBoundUserAgent() {
    return this.appConfig.getBool(CONFIG_TOKEN_BOUND_USER_AGENT, true);
  }

  @Override
  public Class<? extends AuthByCodeCredential> getAcceptedCredentialType() {
    return AuthByCodeCredential.class;
  }

  protected String getTokenBoundData(String code) {
    String userAgent = getTokenBoundUserAgent() ? this.currentRequest.getHeader("User-Agent") : "false";
    return STR.fmt("{}|{}", code, userAgent);
  }

  @Override
  protected PrincipalRoles doValidate(String module, Credential credential, Out<String> invalidCode) {
    AuthByCodeCredential authByCodeCredential = (AuthByCodeCredential) credential;
    String tokenBoundData = getTokenBoundData(authByCodeCredential.getCode());

    // Verify Token
    AuthToken authToken = this.authTokenHandler.verifyToken(authByCodeCredential.getSeries(),
        authByCodeCredential.getToken(), module, tokenBoundData, getExpiryLeewayMs(), invalidCode);

    if (authToken == null) {
      Asserts.notNull(invalidCode.value);
      return null;
    }
    Asserts.isNull(invalidCode.value);

    return this.identityHandler.validateIdentity(module, authToken.getIdentity(), invalidCode);
  }
}
