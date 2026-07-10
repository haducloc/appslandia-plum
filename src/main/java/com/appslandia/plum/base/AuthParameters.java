// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import com.appslandia.common.utils.Asserts;

import jakarta.security.enterprise.authentication.mechanism.http.AuthenticationParameters;
import jakarta.security.enterprise.credential.Credential;

/**
 *
 * @author Loc Ha
 *
 */
public class AuthParameters extends AuthenticationParameters {

  private String module;
  private boolean reauthentication;

  public String getModule() {
    return Asserts.notNull(module, "module is required.");
  }

  public void setModule(String module) {
    this.module = module;
  }

  public boolean isReauthentication() {
    return reauthentication;
  }

  public void setReauthentication(boolean reauthentication) {
    this.reauthentication = reauthentication;
  }

  @Override
  public Credential getCredential() {
    return Asserts.notNull(super.getCredential(), "credential is required.");
  }

  public AuthParameters module(String module) {
    setModule(module);
    return this;
  }

  public AuthParameters reauthentication(boolean reauthentication) {
    setReauthentication(reauthentication);
    return this;
  }

  @Override
  public AuthParameters credential(Credential credential) {
    super.credential(credential);
    return this;
  }

  @Override
  public AuthParameters newAuthentication(boolean newAuthentication) {
    super.newAuthentication(newAuthentication);
    return this;
  }

  @Override
  public AuthParameters rememberMe(boolean rememberMe) {
    super.rememberMe(rememberMe);
    return this;
  }
}
