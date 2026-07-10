// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.CollectionUtils;
import com.appslandia.plum.utils.SecurityUtils;

import jakarta.security.enterprise.identitystore.CredentialValidationResult;

/**
 *
 * @author Loc Ha
 *
 */
public class AuthResult {

  public static final AuthResult NOT_VALIDATED_RESULT = new AuthResult();

  final AuthPrincipal principal;
  final Set<String> groups;
  final String invalidCode;

  private AuthResult() {
    this(null, null, null);
  }

  private AuthResult(AuthPrincipal principal, Set<String> groups, String invalidCode) {
    this.principal = principal;
    this.groups = groups;
    this.invalidCode = invalidCode;
  }

  private AuthResult(String invalidCode) {
    this(null, null, Arguments.notNull(invalidCode, "invalidCode is required."));
  }

  public AuthResult(AuthPrincipal principal, String groups) {
    this(Arguments.notNull(principal, "principal is required."), SecurityUtils.toUserRoles(groups), null);
  }

  public AuthResult(AuthPrincipal principal, Set<String> groups) {
    this(Arguments.notNull(principal, "principal is required."), CollectionUtils.toUnmodifiableSet(groups), null);
  }

  public AuthPrincipal getPrincipal() {
    return principal;
  }

  public Set<String> getGroups() {
    return groups;
  }

  public String getInvalidCode() {
    return invalidCode;
  }

  // Credential
  public static final String CREDENTIAL_INVALID = "credential_invalid";
  public static final String CREDENTIAL_EXPIRED = "credential_expired";
  public static final String CREDENTIAL_LOCKED = "credential_locked";
  public static final String CREDENTIAL_SUSPENDED = "credential_suspended";
  public static final String CREDENTIAL_NOT_ACTIVATED = "credential_not_activated";
  public static final String CREDENTIAL_COMPROMISED = "credential_compromised";
  public static final String CREDENTIAL_ATTEMPTS_EXCEEDED = "credential_attempts_exceeded";
  public static final String CREDENTIAL_MODULE_INVALID = "credential_module_invalid";

  // Token
  public static final String TOKEN_INVALID = "token_invalid";
  public static final String TOKEN_EXPIRED = "token_expired";
  public static final String TOKEN_NOT_VALID_YET = "token_not_valid_yet";
  public static final String TOKEN_REVOKED = "token_revoked";
  public static final String TOKEN_COMPROMISED = "token_compromised";
  public static final String TOKEN_MODULE_INVALID = "token_module_invalid";

  // Other
  public static final String AUTH_INTERNAL_ERROR = "auth_internal_error";

  private static final Map<String, AuthResult> AUTH_INVALID_RESULTS = new ConcurrentHashMap<>();

  public static AuthResult toInvalidResult(String invalidCode) {
    return AUTH_INVALID_RESULTS.computeIfAbsent(invalidCode, AuthResult::new);
  }

  public static String getResKey(String invalidCode) {
    return "auth_result." + invalidCode;
  }

  public static String getResKey(CredentialValidationResult invalidResult) {
    var invalidCode = AuthInvalidResult.getInvalidCode(invalidResult);
    return getResKey(invalidCode);
  }
}
