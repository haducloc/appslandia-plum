// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.mocks;

import java.security.Principal;
import java.util.Collections;
import java.util.Set;

import com.appslandia.common.utils.Asserts;

import jakarta.inject.Inject;
import jakarta.security.enterprise.AuthenticationException;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.SecurityContext;
import jakarta.security.enterprise.authentication.mechanism.http.AuthenticationParameters;
import jakarta.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanismHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
public class MockSecurityContext implements SecurityContext {

  @Inject
  protected HttpAuthenticationMechanismHandler httpAuthenticationMechanismHandler;

  @Override
  public Principal getCallerPrincipal() {
    var currentRequest = MockContainer.currentRequestHolder.get();
    Asserts.isTrue(currentRequest != null, "currentRequest is null.");
    return currentRequest.getUserPrincipal();
  }

  @Override
  public <T extends Principal> Set<T> getPrincipalsByType(Class<T> pType) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isCallerInRole(String role) {
    var currentRequest = MockContainer.currentRequestHolder.get();
    Asserts.isTrue(currentRequest != null, "currentRequest is null.");
    return currentRequest.isUserInRole(role);
  }

  @Override
  public boolean hasAccessToWebResource(String resource, String... methods) {
    throw new UnsupportedOperationException();
  }

  @Override
  public AuthenticationStatus authenticate(HttpServletRequest request, HttpServletResponse response,
      AuthenticationParameters parameters) {
    var httpMessageContext = new MockHttpMessageContext().withRequest(request).withResponse(response)
        .withAuthParameters(parameters).withAuthenticationRequest();
    try {
      return httpAuthenticationMechanismHandler.validateRequest(request, response, httpMessageContext);
    } catch (AuthenticationException ex) {
      throw new RuntimeException(ex);
    }
  }

  @Override
  public Set<String> getAllDeclaredCallerRoles() {
    return Collections.emptySet();
  }
}
