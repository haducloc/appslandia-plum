// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import jakarta.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.CredentialValidationResult.Status;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * The implementation must be exposed as a CDI bean with @ApplicationScoped and annotated with @MappedID using the using
 * the target module name.
 *
 * @author Loc Ha
 *
 */
public abstract class BearerHttpAuthMechanism extends AuthorizationMechanismBase {

  @Override
  public AuthMethod getAuthMethod() {
    return AuthMethod.BEARER;
  }

  @Override
  public void askAuthenticate(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext)
      throws Exception {
    var challenge = getAuthMethod().getType() + " realm=\"" + requestContext.getModule() + "\"";

    // CredentialValidationResult
    var authResult = (CredentialValidationResult) request.getAttribute(CredentialValidationResult.class.getName());

    if ((authResult != null) && (authResult.getStatus() == Status.INVALID)) {
      challenge += ", error=\"" + AuthInvalidResult.getInvalidCode(authResult) + "\"";
    }

    response.setHeader("WWW-Authenticate", challenge);
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
  }

  @Override
  protected boolean isReauthentication(HttpServletRequest request, HttpMessageContext httpMessageContext) {
    return false;
  }
}
