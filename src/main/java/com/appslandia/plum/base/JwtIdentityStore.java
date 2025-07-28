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

import com.appslandia.common.base.Out;
import com.appslandia.common.jose.JwtToken;
import com.appslandia.common.utils.Asserts;

import jakarta.security.enterprise.credential.Credential;

/**
 *
 * @author Loc Ha
 *
 */
public abstract class JwtIdentityStore extends IdentityStoreBase {

  protected abstract JwtToken parseJwtToken(String credential);

  @Override
  protected RolesPrincipal doValidate(String module, Credential credential, Out<String> invalidCode) {
    Asserts.isTrue(credential instanceof JwtCredential, "The given credential must be an instance of JwtCredential.");
    var jwtCredential = (JwtCredential) credential;

    // JwtToken
    JwtToken token = null;
    try {
      token = parseJwtToken(jwtCredential.getToken());

    } catch (Exception ex) {
      invalidCode.value = InvalidAuth.TOKEN_INVALID.getCode();
      return null;
    }

    // PrincipalRoles
    var userRoles = (String) token.getPayload().get(UserPrincipal.ATTRIBUTE_ROLES);
    return new RolesPrincipal(new JwtPrincipal(token), userRoles);
  }
}
