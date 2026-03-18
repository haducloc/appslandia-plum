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

import jakarta.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.CredentialValidationResult.Status;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
public abstract class BearerHttpAuthMechanism extends AuthorizationMechanismBase {

  @Override
  protected AuthMethod getAuthMethod() {
    return AuthMethod.BEARER;
  }

  @Override
  public void askAuthenticate(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext)
      throws Exception {
    var challenge = getAuthMethod().getType() + " realm=\"" + requestContext.getModule() + "\"";

    // CredentialValidationResult
    var authResult = (CredentialValidationResult) request.getAttribute(CredentialValidationResult.class.getName());

    if ((authResult != null) && (authResult.getStatus() == Status.INVALID)) {
      challenge += ", error=\"" + InvalidAuth.getInvalidCode(authResult) + "\"";
    }

    response.setHeader("WWW-Authenticate", challenge);
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
  }

  @Override
  protected boolean isReauthentication(HttpServletRequest request, HttpMessageContext httpMessageContext) {
    return false;
  }
}
