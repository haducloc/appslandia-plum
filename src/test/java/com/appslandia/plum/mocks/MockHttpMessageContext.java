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

  private Principal callerPrincipal;
  private Set<String> groups;

  @Override
  public boolean isProtected() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isAuthenticationRequest() {
    return this.isAuthenticationRequest;
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
    return this.authParameters;
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
    return this.request;
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
    return this.response;
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
      this.callerPrincipal = Asserts.notNull(result.getCallerPrincipal());
      this.groups = result.getCallerGroups();

      var req = unwrapToMockImpl(this.request);
      req.setUserPrincipal(this.callerPrincipal);
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
    this.callerPrincipal = null;
    this.groups = null;
    return AuthenticationStatus.NOT_DONE;
  }

  @Override
  public Principal getCallerPrincipal() {
    return this.callerPrincipal;
  }

  @Override
  public Set<String> getGroups() {
    return this.groups;
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
