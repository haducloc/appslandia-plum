// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import com.appslandia.common.jose.JwtToken;

import jakarta.security.enterprise.credential.Credential;

/**
 *
 * @author Loc Ha
 *
 */
public abstract class JwtIdentityStore extends IdentityStoreBase {

  protected abstract JwtToken parseJwtToken(String module, String credential);

  @Override
  protected AuthResult doValidate(String module, Credential credential) {
    if (!(credential instanceof JwtCredential jwtCredential)) {
      return AuthResult.NOT_VALIDATED_RESULT;
    }

    // JwtToken
    JwtToken token = null;
    try {
      token = parseJwtToken(module, jwtCredential.getToken());

    } catch (Exception ex) {
      return AuthResult.toInvalidResult(AuthResult.TOKEN_INVALID);
    }

    var payload = token.getPayload();

    var callerName = payload.getString(AuthPrincipal.ATTRIBUTE_USERNAME);
    var sub = payload.getStringReq(AuthPrincipal.ATTRIBUTE_SUB);

    if (callerName == null) {
      callerName = sub;
    }
    var userRoles = payload.getString(AuthPrincipal.ATTRIBUTE_ROLES);
    return new AuthResult(new AuthPrincipal(storeId(), module, callerName, payload), userRoles);
  }
}
