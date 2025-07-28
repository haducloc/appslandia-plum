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

import java.util.Arrays;

import com.appslandia.common.base.Out;
import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.Asserts;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.SecurityContext;
import jakarta.security.enterprise.credential.Credential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStoreHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class AuthContext {

  @Inject
  protected AppConfig appConfig;

  @Inject
  protected SecurityContext securityContext;

  @Inject
  protected IdentityStoreHandler identityStoreHandler;

  public CredentialValidationResult validate(Credential credential, String module) throws ServletException {
    var authCredential = new AuthCredential(credential, module, true, false, false);
    var authResult = this.identityStoreHandler.validate(authCredential);
    return authResult;
  }

  public boolean authenticate(RequestWrapper request, HttpServletResponse response, AuthParameters authParameters,
      Out<String> invalidCode) throws ServletException {
    Asserts.isNull(request.getUserPrincipal(), "Already authenticated.");

    // Authenticate
    var authStatus = this.securityContext.authenticate(request, response, authParameters);

    // CredentialValidationResult
    var authResult = (CredentialValidationResult) request.getAttribute(CredentialValidationResult.class.getName());
    Asserts.notNull(authResult, "HttpAuthMechanismBase.validateRequest() was not called.");

    if (authResult.getStatus() == CredentialValidationResult.Status.VALID) {
      Asserts.isTrue(authStatus == AuthenticationStatus.SUCCESS);

      // Authenticated successfully: Change sessionId
      if (this.appConfig.isEnableSession() && (request.getSession(false) != null)) {
        request.changeSessionId();
      }
      return true;

    } else {
      invalidCode.value = InvalidAuth.getInvalidCode(authResult);
      return false;
    }
  }

  public boolean isUserInRoles(String... roles) {
    Arguments.hasElements(roles);
    return Arrays.stream(roles).anyMatch(role -> this.securityContext.isCallerInRole(role));
  }

  public UserPrincipal getPrincipal() {
    var principal = this.securityContext.getCallerPrincipal();
    if (principal == null) {
      return null;
    }
    if (!(principal instanceof UserPrincipal)) {
      throw new IllegalStateException("securityContext.getCallerPrincipal() must be UserPrincipal.");
    }
    return (UserPrincipal) principal;
  }

  public UserPrincipal getPrincipalReq() {
    return Asserts.notNull(getPrincipal(), "securityContext.getCallerPrincipal() is required.");
  }
}
