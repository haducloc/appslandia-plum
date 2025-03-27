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

import com.appslandia.common.base.NotImplementedException;
import com.appslandia.common.utils.STR;

import jakarta.security.enterprise.credential.Credential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.CredentialValidationResult.Status;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
public abstract class HttpAuthHandler implements AuthHandler {

  protected abstract Credential doParseCredential(String credential) throws Exception;

  @Override
  public Credential parseCredential(HttpServletRequest request) {
    String authorization = request.getHeader("Authorization");
    if (authorization == null) {
      return null;
    }
    int idx = authorization.indexOf(' ');
    if (idx <= 0) {
      return null;
    }
    String authMethod = authorization.substring(0, idx);
    String credential = authorization.substring(idx + 1);

    if (!getAuthMethod().getType().equalsIgnoreCase(authMethod)) {
      return null;
    }
    if (credential.isEmpty()) {
      return null;
    }

    Credential cred = null;
    try {
      cred = doParseCredential(credential);
    } catch (Exception ex) {
      // ignore
    }
    return cred;
  }

  @Override
  public void askAuthenticate(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext)
      throws Exception {
    String challenge = null;

    if (AuthMethod.BEARER == getAuthMethod()) {
      challenge = getAuthMethod().getType() + " realm=\"" + requestContext.getModule() + "\"";

      // The request attribute: CredentialValidationResult.class.getName()
      // was set in HttpAuthenticationMechanismBase.validateRequest()

      CredentialValidationResult authResult = (CredentialValidationResult) request
          .getAttribute(CredentialValidationResult.class.getName());

      if ((authResult != null) && (authResult.getStatus() == Status.INVALID)) {
        challenge += ", error=\"" + InvalidAuthResult.getInvalidCode(authResult) + "\"";
      }

    } else if (AuthMethod.BASIC == getAuthMethod()) {
      challenge = getAuthMethod().getType() + " realm=\"" + requestContext.getModule() + "\"";

    } else {
      throw new NotImplementedException(
          STR.fmt("The askAuthenticate() was not implemented for the auth method '{}'.", getAuthMethod()));
    }

    response.setHeader("WWW-Authenticate", challenge);
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
  }

  @Override
  public void askReauthenticate(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext)
      throws Exception {
    throw new UnsupportedOperationException();
  }
}
