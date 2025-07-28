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

import jakarta.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import jakarta.security.enterprise.credential.Credential;
import jakarta.servlet.http.HttpServletRequest;

/**
 *
 * @author Loc Ha
 *
 */
public abstract class AuthorizationMechanismBase extends HttpAuthMechanismBase {

  protected abstract Credential doParseCredential(String credential) throws Exception;

  @Override
  protected Credential getCredential(HttpServletRequest request, HttpMessageContext httpMessageContext) {
    Asserts.isTrue(!httpMessageContext.isAuthenticationRequest());

    // Authorization
    var authorization = request.getHeader("Authorization");
    if (authorization == null) {
      return null;
    }
    var idx = authorization.indexOf(' ');
    if (idx <= 0) {
      return null;
    }
    var authMethod = authorization.substring(0, idx);
    var credential = authorization.substring(idx + 1);

    if (!getAuthMethod().getType().equalsIgnoreCase(authMethod) || credential.isEmpty()) {
      return null;
    }

    // Credential
    Credential cred = null;
    try {
      cred = doParseCredential(credential);
    } catch (Exception ex) {
      // ignore
    }
    return cred;
  }
}
