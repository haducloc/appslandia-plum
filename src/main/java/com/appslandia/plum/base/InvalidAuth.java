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

import java.util.Set;

import com.appslandia.common.utils.Arguments;

import jakarta.security.enterprise.CallerPrincipal;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;

/**
 *
 * @author Loc Ha
 *
 */
public class InvalidAuth extends CredentialValidationResult {

  static final CallerPrincipal INVALID_PRINCIPAL = new CallerPrincipal("null");
  static final CredentialValidationResult INVALID_RESULT = CredentialValidationResult.INVALID_RESULT;

  final String code;

  // NOTES: CredentialValidationResult doesn't provide a public constructor for
  // creating an invalid result
  // Use INVALID_RESULT to implement custom invalid result

  private InvalidAuth(String code) {
    super((String) null, INVALID_PRINCIPAL, null, null, null);
    this.code = Arguments.notNull(code);
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

  public String getCode() {
    return this.code;
  }

  public static final InvalidAuth CREDENTIAL_INVALID = new InvalidAuth("credential_invalid");

  public static final InvalidAuth CREDENTIAL_SUSPENDED = new InvalidAuth("credential_suspended");
  public static final InvalidAuth CREDENTIAL_NOT_ACTIVATED = new InvalidAuth("credential_not_activated");
  public static final InvalidAuth CREDENTIAL_NOT_APPROVED = new InvalidAuth("credential_not_approved");
  public static final InvalidAuth CREDENTIAL_COMPROMISED = new InvalidAuth("credential_compromised");

  public static final InvalidAuth TOKEN_INVALID = new InvalidAuth("token_invalid");
  public static final InvalidAuth TOKEN_COMPROMISED = new InvalidAuth("token_compromised");
  public static final InvalidAuth TOKEN_EXPIRED = new InvalidAuth("token_expired");
  public static final InvalidAuth TOKEN_MODULE_MISMATCH = new InvalidAuth("token_module_mismatch");

  public static final InvalidAuth ID_STORE_EXCEPTION = new InvalidAuth("id_store_exception");

  public static InvalidAuth valueOf(String code) {
    Arguments.notNull(code);

    if (CREDENTIAL_INVALID.getCode().equalsIgnoreCase(code)) {
      return CREDENTIAL_INVALID;
    }
    if (CREDENTIAL_SUSPENDED.getCode().equalsIgnoreCase(code)) {
      return CREDENTIAL_SUSPENDED;
    }
    if (CREDENTIAL_NOT_ACTIVATED.getCode().equalsIgnoreCase(code)) {
      return CREDENTIAL_NOT_ACTIVATED;
    }
    if (CREDENTIAL_NOT_APPROVED.getCode().equalsIgnoreCase(code)) {
      return CREDENTIAL_NOT_APPROVED;
    }
    if (CREDENTIAL_COMPROMISED.getCode().equalsIgnoreCase(code)) {
      return CREDENTIAL_COMPROMISED;
    }
    if (TOKEN_INVALID.getCode().equalsIgnoreCase(code)) {
      return TOKEN_INVALID;
    }
    if (TOKEN_COMPROMISED.getCode().equalsIgnoreCase(code)) {
      return TOKEN_COMPROMISED;
    }
    if (TOKEN_EXPIRED.getCode().equalsIgnoreCase(code)) {
      return TOKEN_EXPIRED;
    }
    if (TOKEN_MODULE_MISMATCH.getCode().equalsIgnoreCase(code)) {
      return TOKEN_MODULE_MISMATCH;
    }
    if (ID_STORE_EXCEPTION.getCode().equalsIgnoreCase(code)) {
      return ID_STORE_EXCEPTION;
    }
    return new InvalidAuth(code);
  }

  public static String getInvalidCode(CredentialValidationResult invalidResult) {
    if (invalidResult instanceof InvalidAuth) {
      return ((InvalidAuth) invalidResult).getCode();
    }
    return InvalidAuth.CREDENTIAL_INVALID.getCode();
  }

  /**
   * Returns a resource key in the format "invalid_auth.{code}".
   *
   * @param invalidCode the auth error code
   * @return the formatted resource key
   */
  public static String getResKey(String invalidCode) {
    return "invalid_auth." + invalidCode;
  }
}
