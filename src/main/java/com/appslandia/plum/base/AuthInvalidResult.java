// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.appslandia.common.utils.Arguments;

import jakarta.security.enterprise.CallerPrincipal;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;

/**
 *
 * @author Loc Ha
 *
 */
public class AuthInvalidResult extends CredentialValidationResult {

  static final CallerPrincipal NULL_PRINCIPAL = new CallerPrincipal("null");
  static final CredentialValidationResult INVALID_RESULT = CredentialValidationResult.INVALID_RESULT;

  final String invalidCode;

  private AuthInvalidResult(String invalidCode) {
    super((String) null, NULL_PRINCIPAL, null, null, null);
    this.invalidCode = Arguments.notNull(invalidCode, "invalidCode is required.");
  }

  @Override
  public Status getStatus() {
    return INVALID_RESULT.getStatus();
  }

  @Override
  public String getIdentityStoreId() {
    return INVALID_RESULT.getIdentityStoreId();
  }

  @Override
  public CallerPrincipal getCallerPrincipal() {
    return INVALID_RESULT.getCallerPrincipal();
  }

  @Override
  public String getCallerUniqueId() {
    return INVALID_RESULT.getCallerUniqueId();
  }

  @Override
  public String getCallerDn() {
    return INVALID_RESULT.getCallerDn();
  }

  @Override
  public Set<String> getCallerGroups() {
    return INVALID_RESULT.getCallerGroups();
  }

  public String getInvalidCode() {
    return invalidCode;
  }

  public static String getInvalidCode(CredentialValidationResult invalidResult) {
    Arguments.notNull(invalidResult);
    Arguments.isTrue(invalidResult.getStatus() == Status.INVALID, "The given result must be an invalid result.");

    if (invalidResult instanceof AuthInvalidResult result) {
      return result.getInvalidCode();
    }
    return AuthResult.CREDENTIAL_INVALID;
  }

  private static final Map<String, AuthInvalidResult> AUTH_INVALID_RESULTS = new ConcurrentHashMap<>();

  public static AuthInvalidResult toResult(String invalidCode) {
    return AUTH_INVALID_RESULTS.computeIfAbsent(invalidCode, AuthInvalidResult::new);
  }
}
