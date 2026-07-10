// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.mem;

import java.util.Set;

import com.appslandia.plum.base.AuthByCodeIdentityStore;
import com.appslandia.plum.base.AuthEvent;
import com.appslandia.plum.base.AuthEventPublisher;
import com.appslandia.plum.base.AuthPrincipal;
import com.appslandia.plum.base.AuthResult;

import jakarta.inject.Inject;
import jakarta.security.enterprise.credential.Credential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.CredentialValidationResult.Status;

/**
 *
 * @author Loc Ha
 *
 */
public class MemUserAuthByCodeIdentityStore extends AuthByCodeIdentityStore {

  private static final Set<String> VALIDATION_MODULES = Set.of(MemModules.MEM_BASIC, MemModules.MEM_FORM);

  @Inject
  protected MemUserService userService;

  @Inject
  protected AuthEventPublisher authEventPublisher;

  @Override
  public String storeId() {
    return "MemUserAuthByCodeStore";
  }

  @Override
  protected AuthResult doValidate(String module, String identity) {
    var user = userService.getByUsername(identity);
    if (user == null) {
      return AuthResult.toInvalidResult(AuthResult.CREDENTIAL_INVALID);
    }

    var authPrincipal = new AuthPrincipal(storeId(), module, user.getUsername(), user.toAttributes());
    return new AuthResult(authPrincipal, user.getUserRoles());
  }

  @Override
  public CredentialValidationResult validate(Credential credential) {
    var result = super.validate(credential);
    if (result.getStatus() != Status.NOT_VALIDATED) {

      var authEvent = new AuthEvent();
      authEventPublisher.fire(authEvent);
    }
    return result;
  }

  @Override
  public Set<String> validationModules() {
    return VALIDATION_MODULES;
  }
}
