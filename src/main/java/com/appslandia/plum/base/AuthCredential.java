// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import jakarta.security.enterprise.credential.Credential;

/**
 *
 * @author Loc Ha
 *
 */
public class AuthCredential implements Credential {

  final Credential credential;

  final String module;
  final boolean authenticationRequest;
  final boolean reauthentication;
  final boolean rememberMe;

  public AuthCredential(Credential credential, String module, boolean authenticationRequest, boolean reauthentication,
      boolean rememberMe) {
    this.credential = credential;

    this.module = module;
    this.authenticationRequest = authenticationRequest;
    this.reauthentication = reauthentication;
    this.rememberMe = rememberMe;
  }

  public Credential getCredential() {
    return credential;
  }

  public String getModule() {
    return module;
  }

  public boolean isAuthenticationRequest() {
    return authenticationRequest;
  }

  public boolean isReauthentication() {
    return reauthentication;
  }

  public boolean isRememberMe() {
    return rememberMe;
  }

  @Override
  public boolean isCleared() {
    return credential.isCleared();
  }

  @Override
  public void clear() {
    credential.clear();
  }

  @Override
  public boolean isValid() {
    return credential.isValid();
  }
}
