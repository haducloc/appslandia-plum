// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.mem;

import java.util.Set;

import com.appslandia.plum.base.AuthPrincipal;
import com.appslandia.plum.base.AuthResult;
import com.appslandia.plum.base.IdentityStoreBase;

import jakarta.inject.Inject;
import jakarta.security.enterprise.credential.Credential;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;

/**
 *
 * @author Loc Ha
 *
 */
public class MemUserIdentityStore extends IdentityStoreBase {

  private static final Set<String> VALIDATION_MODULES = Set.of(MemModules.MEM_BASIC, MemModules.MEM_FORM);

  @Inject
  protected MemUserService userService;

  @Override
  public String storeId() {
    return "MemUserStore";
  }

  @Override
  public Set<String> validationModules() {
    return VALIDATION_MODULES;
  }

  @Override
  protected AuthResult doValidate(String module, Credential credential) {
    if (!(credential instanceof UsernamePasswordCredential userPassCred)) {
      return AuthResult.NOT_VALIDATED_RESULT;
    }
    var user = userService.getByUsername(userPassCred.getCaller());
    if ((user == null) || !userService.verifyPassword(userPassCred.getPasswordAsString(), user.getPassword())) {
      return AuthResult.toInvalidResult(AuthResult.CREDENTIAL_INVALID);
    }

    var authPrincipal = new AuthPrincipal(storeId(), module, user.getUsername(), user.toAttributes());
    return new AuthResult(authPrincipal, user.getUserRoles());
  }
}
