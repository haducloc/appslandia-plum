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

package com.appslandia.plum.mocks;

import java.security.Principal;
import java.util.Set;

import com.appslandia.common.utils.Asserts;
import com.appslandia.plum.defaults.HttpAuthenticationMechanismHandler;

import jakarta.inject.Inject;
import jakarta.security.enterprise.AuthenticationException;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.SecurityContext;
import jakarta.security.enterprise.authentication.mechanism.http.AuthenticationParameters;
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
      return this.httpAuthenticationMechanismHandler.validateRequest(request, response, httpMessageContext);
    } catch (AuthenticationException ex) {
      throw new RuntimeException(ex);
    }
  }
}
