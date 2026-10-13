// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.mocks;

import java.security.Principal;
import java.util.Set;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;

import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.ObjectUtils;

import jakarta.security.auth.message.MessageInfo;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.authentication.mechanism.http.AuthenticationParameters;
import jakarta.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
public class MockHttpMessageContext implements HttpMessageContext {

  private HttpServletRequest request;
  private HttpServletResponse response;
  private AuthenticationParameters authParameters;
  private boolean isAuthenticationRequest;

  private Principal principal;
  private Set<String> callerGroups;

  @Override
  public boolean isProtected() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isAuthenticationRequest() {
    return isAuthenticationRequest;
  }

  public void setAuthenticationRequest(boolean isAuthenticationRequest) {
    this.isAuthenticationRequest = isAuthenticationRequest;
  }

  public MockHttpMessageContext withAuthenticationRequest() {
    setAuthenticationRequest(true);
    return this;
  }

  @Override
  public boolean isRegisterSession() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setRegisterSession(String username, Set<String> groups) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void cleanClientSubject() {
    throw new UnsupportedOperationException();
  }

  @Override
  public AuthenticationParameters getAuthParameters() {
    return authParameters;
  }

  public void setAuthParameters(AuthenticationParameters authParameters) {
    this.authParameters = authParameters;
  }

  public MockHttpMessageContext withAuthParameters(AuthenticationParameters authParameters) {
    setAuthParameters(authParameters);
    return this;
  }

  @Override
  public CallbackHandler getHandler() {
    throw new UnsupportedOperationException();
  }

  @Override
  public MessageInfo getMessageInfo() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Subject getClientSubject() {
    throw new UnsupportedOperationException();
  }

  @Override
  public HttpServletRequest getRequest() {
    return request;
  }

  @Override
  public void setRequest(HttpServletRequest request) {
    this.request = request;
  }

  @Override
  public MockHttpMessageContext withRequest(HttpServletRequest request) {
    setRequest(request);
    return this;
  }

  @Override
  public HttpServletResponse getResponse() {
    return response;
  }

  @Override
  public void setResponse(HttpServletResponse response) {
    this.response = response;
  }

  public MockHttpMessageContext withResponse(HttpServletResponse response) {
    setResponse(response);
    return this;
  }

  @Override
  public AuthenticationStatus redirect(String location) {
    throw new UnsupportedOperationException();
  }

  @Override
  public AuthenticationStatus forward(String path) {
    throw new UnsupportedOperationException();
  }

  @Override
  public AuthenticationStatus responseUnauthorized() {
    throw new UnsupportedOperationException();
  }

  @Override
  public AuthenticationStatus responseNotFound() {
    throw new UnsupportedOperationException();
  }

  @Override
  public AuthenticationStatus notifyContainerAboutLogin(String callerName, Set<String> groups) {
    throw new UnsupportedOperationException();
  }

  @Override
  public AuthenticationStatus notifyContainerAboutLogin(CredentialValidationResult result) {
    if (result.getStatus() == CredentialValidationResult.Status.VALID) {
      principal = Asserts.notNull(result.getCallerPrincipal());
      callerGroups = result.getCallerGroups();

      var req = unwrapToMockImpl(request);
      req.setUserPrincipal(principal, callerGroups);
      return AuthenticationStatus.SUCCESS;
    }
    return AuthenticationStatus.SEND_FAILURE;
  }

  @Override
  public AuthenticationStatus notifyContainerAboutLogin(Principal callerPrincipal, Set<String> groups) {
    throw new UnsupportedOperationException();
  }

  @Override
  public AuthenticationStatus doNothing() {
    principal = null;
    callerGroups = null;
    return AuthenticationStatus.NOT_DONE;
  }

  @Override
  public Principal getCallerPrincipal() {
    return principal;
  }

  @Override
  public Set<String> getGroups() {
    return callerGroups;
  }

  static MockHttpServletRequest unwrapToMockImpl(HttpServletRequest request) {
    var req = request;
    while (true) {
      if (req.getClass() == MockHttpServletRequest.class) {
        return ObjectUtils.cast(req);
      }
      if (!(req instanceof HttpServletRequestWrapper)) {
        return null;
      }
      req = (HttpServletRequest) ((HttpServletRequestWrapper) req).getRequest();
    }
  }
}
