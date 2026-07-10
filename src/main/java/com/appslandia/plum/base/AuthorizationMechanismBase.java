// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import com.appslandia.common.utils.Asserts;

import jakarta.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import jakarta.security.enterprise.credential.Credential;
import jakarta.servlet.http.HttpServletRequest;

/**
 * The implementation must be exposed as a CDI bean with @ApplicationScoped and annotated with @MappedID using the using
 * the target module name.
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
