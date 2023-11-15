// The MIT License (MIT)
// Copyright © 2015 AppsLandia. All rights reserved.

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

import java.security.Principal;
import java.util.Arrays;

import com.appslandia.common.base.AssertException;
import com.appslandia.common.base.InitializeException;
import com.appslandia.common.base.Out;
import com.appslandia.common.utils.Asserts;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.SecurityContext;
import jakarta.security.enterprise.credential.Credential;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@ApplicationScoped
public class AuthContext {

  @Inject
  protected AppConfig appConfig;

  @Inject
  protected SecurityContext securityContext;

  @Inject
  protected IdentityValidator identityValidator;

  @Inject
  protected Instance<HttpAuthenticationMechanismBase> authenticationMechanism;

  @PostConstruct
  protected void initialize() {
    if (!this.authenticationMechanism.isResolvable()) {
      throw new InitializeException("HttpAuthenticationMechanismBase is not resolvable.");
    }
  }

  public boolean authenticate(HttpServletRequest request, HttpServletResponse response, Credential credential,
      boolean rememberMe, Out<String> invalidCode) throws ServletException {

    final boolean hasPrincipal = request.getUserPrincipal() != null;

    // If hasPrincipal
    if (hasPrincipal) {
      UserPrincipal principal = ServletUtils.getUserPrincipal(request);

      // Validate the credential in advance to make sure the given credential is valid

      // UsernamePasswordCredential
      if (credential instanceof UsernamePasswordCredential) {

        UsernamePasswordCredential usernamePasswordCredential = (UsernamePasswordCredential) credential;
        Asserts.isTrue(principal.getName().equalsIgnoreCase(usernamePasswordCredential.getCaller()));

        if (this.identityValidator.validate(principal.getModule(), usernamePasswordCredential.getCaller(),
            usernamePasswordCredential.getPasswordAsString(), invalidCode) == null) {
          return false;
        }
      }

      // LOGOUT
      request.logout();
    }

    // AUTHENTICATE

    // AuthParameters
    AuthParameters authParameters = new AuthParameters().credential(credential).rememberMe(rememberMe)
        .reauthentication(hasPrincipal);
    AuthenticationStatus authStatus = this.securityContext.authenticate(request, response, authParameters);

    // HttpAuthenticationMechanismBase.validateRequest() is supposed to be called
    CredentialValidationResult authResult = (CredentialValidationResult) request
        .getAttribute(CredentialValidationResult.class.getName());
    Asserts.notNull(authResult, "HttpAuthenticationMechanismBase.validateRequest() was not called.");

    if (authResult.getStatus() == CredentialValidationResult.Status.VALID) {
      Asserts.isTrue(authStatus == AuthenticationStatus.SUCCESS);

      // Authenticated successfully: Change sessionId
      if (this.appConfig.isEnableSession() && (request.getSession(false) != null)) {
        request.changeSessionId();
      }
      return true;

    } else {
      invalidCode.value = (authResult instanceof InvalidAuthResult) ? ((InvalidAuthResult) authResult).getCode()
          : InvalidAuthResult.CREDENTIAL_INVALID.getCode();
      return false;
    }
  }

  public boolean isCallerInRoles(String... roles) {
    Asserts.hasElements(roles);
    return Arrays.stream(roles).anyMatch(role -> this.securityContext.isCallerInRole(role));
  }

  public UserPrincipal getUserPrincipal() {
    Principal principal = this.securityContext.getCallerPrincipal();
    if (principal == null) {
      return null;
    }
    if (!(principal instanceof UserPrincipal)) {
      throw new AssertException("securityContext.getCallerPrincipal() must be UserPrincipal.");
    }
    return (UserPrincipal) principal;
  }

  public UserPrincipal getRequiredPrincipal() {
    return Asserts.notNull(getUserPrincipal(), "securityContext.getCallerPrincipal() is required.");
  }
}
