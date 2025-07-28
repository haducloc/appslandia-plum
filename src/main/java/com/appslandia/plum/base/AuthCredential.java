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
    return this.credential;
  }

  public String getModule() {
    return this.module;
  }

  public boolean isAuthenticationRequest() {
    return this.authenticationRequest;
  }

  public boolean isReauthentication() {
    return this.reauthentication;
  }

  public boolean isRememberMe() {
    return this.rememberMe;
  }

  @Override
  public boolean isCleared() {
    return this.credential.isCleared();
  }

  @Override
  public void clear() {
    this.credential.clear();
  }

  @Override
  public boolean isValid() {
    return this.credential.isValid();
  }
}
