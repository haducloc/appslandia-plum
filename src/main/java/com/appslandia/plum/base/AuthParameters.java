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
    return Asserts.notNull(this.module, "module is required.");
  }

  public void setModule(String module) {
    this.module = module;
  }

  public boolean isReauthentication() {
    return this.reauthentication;
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
