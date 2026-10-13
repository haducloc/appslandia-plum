// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.util.Collections;
import java.util.Set;

import com.appslandia.common.utils.Asserts;

import jakarta.security.enterprise.credential.Credential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.CredentialValidationResult.Status;
import jakarta.security.enterprise.identitystore.IdentityStore;

/**
 *
 * @author Loc Ha
 *
 */
public abstract class IdentityStoreBase implements IdentityStore {

  public String storeId() {
    return null;
  }

  public abstract Set<String> validationModules();

  protected abstract AuthResult doValidate(String module, Credential credential);

  @Override
  public CredentialValidationResult validate(Credential credential) {
    // AuthCredential
    if (!(credential instanceof AuthCredential authCredential)) {
      throw new IllegalArgumentException(
          "The given credential must be an instance of com.appslandia.plum.base.AuthCredential.");
    }

    if (!isProvideValidate(authCredential.getModule())) {
      return CredentialValidationResult.NOT_VALIDATED_RESULT;
    }

    // Validate credential
    var authResult = doValidate(authCredential.getModule(), authCredential.getCredential());
    Asserts.notNull(authResult, "doValidate(module, credential) must not return null.");

    if (authResult == AuthResult.NOT_VALIDATED_RESULT) {
      return CredentialValidationResult.NOT_VALIDATED_RESULT;
    }
    if (authResult.getInvalidCode() != null) {
      return AuthInvalidResult.toResult(authResult.getInvalidCode());
    }

    var callerPrincipal = authResult.getPrincipal();

    return new CredentialValidationResult(storeId(), callerPrincipal,
        (String) callerPrincipal.get(AuthPrincipal.ATTRIBUTE_LDAP_DN), callerPrincipal.getCallerUniqueId(),
        authResult.getGroups());
  }

  @Override
  public Set<String> getCallerGroups(CredentialValidationResult validResult) {
    Asserts.notNull(validResult);
    Asserts.isTrue(validResult.getStatus() == Status.VALID);

    // AuthPrincipal
    if (!(validResult.getCallerPrincipal() instanceof AuthPrincipal authPrincipal)) {
      throw new IllegalArgumentException(
          "The given validationResult.getCallerPrincipal() must be an instance of com.appslandia.plum.base.AuthPrincipal.");
    }

    if (!isProvideGroupsOnly(authPrincipal.getModule())) {
      return Collections.emptySet();
    }

    return loadCallerGroups(authPrincipal);
  }

  protected Set<String> loadCallerGroups(AuthPrincipal principal) {
    return Collections.emptySet();
  }

  public boolean isProvideGroupsOnly(String module) {
    if (!validationModules().contains(module) || (validationTypes().size() > 1)
        || !validationTypes().contains(ValidationType.PROVIDE_GROUPS)) {
      return false;
    }
    return true;
  }

  public boolean isProvideValidate(String module) {
    if (!validationModules().contains(module) || !validationTypes().contains(ValidationType.VALIDATE)) {
      return false;
    }
    return true;
  }
}
