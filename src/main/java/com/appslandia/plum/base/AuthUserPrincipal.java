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

/**
 *
 * @author Loc Ha
 *
 */
public class AuthUserPrincipal extends UserPrincipal {
  private static final long serialVersionUID = 1L;

  final UserPrincipal principal;
  final String module;
  final boolean rememberMe;
  final long reauthAt;

  public AuthUserPrincipal(UserPrincipal principal, AuthCredential credential) {
    this(principal, credential.getModule(), credential.isRememberMe(), credential.isReauthentication());
  }

  public AuthUserPrincipal(UserPrincipal principal, String module, boolean rememberMe, boolean reauthentication) {
    super(null, null);
    this.principal = principal;

    this.module = module;
    this.rememberMe = rememberMe;
    this.reauthAt = reauthentication ? System.currentTimeMillis() : 0L;
  }

  @Override
  public String getName() {
    return this.principal.getName();
  }

  @Override
  public Object get(String attributeName) {
    return this.principal.get(attributeName);
  }

  @Override
  public Object getReq(String attributeName) {
    return this.principal.getReq(attributeName);
  }

  @Override
  public String getModule() {
    return this.module;
  }

  @Override
  public boolean isRememberMe() {
    return this.rememberMe;
  }

  @Override
  public long getReauthAt() {
    return this.reauthAt;
  }
}
