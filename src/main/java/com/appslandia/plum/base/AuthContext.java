// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import com.appslandia.common.base.Out;
import com.appslandia.common.utils.Asserts;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.SecurityContext;
import jakarta.security.enterprise.credential.Credential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class AuthContext {

  @Inject
  protected SecurityContext securityContext;

  @Inject
  protected IdentityStoreHandlerBase identityStoreHandler;

  @Inject
  protected SessionAttributeClearer sessionAttributeClearer;

  public CredentialValidationResult validate(Credential credential, String module) throws ServletException {
    var authCredential = new AuthCredential(credential, module, true, false, false);
    var authResult = identityStoreHandler.validate(authCredential, true);
    return authResult;
  }

  public boolean authenticate(HttpServletRequest request, HttpServletResponse response, AuthParameters authParameters,
      Out<String> invalidCode) throws ServletException {
    Asserts.isNull(request.getUserPrincipal(), "request.getUserPrincipal() must be null.");

    // Authenticate
    var authStatus = securityContext.authenticate(request, response, authParameters);

    // CredentialValidationResult
    var authResult = (CredentialValidationResult) ServletUtils.removeAttribute(request,
        CredentialValidationResult.class.getName());
    Asserts.notNull(authResult, "HttpAuthMechanismBase.validateRequest() was not called.");

    if (authResult.getStatus() == CredentialValidationResult.Status.VALID) {
      Asserts.isTrue(authStatus == AuthenticationStatus.SUCCESS);

      // Clear session and change sessionId
      var session = request.getSession(false);
      if (session != null) {
        sessionAttributeClearer.clearSession(session);
        request.changeSessionId();
      }
      return true;

    } else {
      invalidCode.value = AuthInvalidResult.getInvalidCode(authResult);
      return false;
    }
  }
}
