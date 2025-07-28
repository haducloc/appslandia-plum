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

import java.lang.reflect.Method;
import java.util.Arrays;

import com.appslandia.common.base.AppLogger;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.inject.Inject;
import jakarta.security.enterprise.AuthenticationException;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import jakarta.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import jakarta.security.enterprise.credential.Credential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStoreHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
public abstract class HttpAuthMechanismBase implements HttpAuthenticationMechanism {

  @Inject
  protected AppLogger appLogger;

  @Inject
  protected IdentityStoreHandler identityStoreHandler;

  @Inject
  protected RequestContextParser requestContextParser;

  protected abstract AuthMethod getAuthMethod();

  protected abstract Credential getCredential(HttpServletRequest request, HttpMessageContext httpMessageContext);

  protected boolean isReauthentication(HttpServletRequest request, HttpMessageContext httpMessageContext) {
    return false;
  }

  protected String getModule(HttpServletRequest request, HttpMessageContext httpMessageContext) {
    if (httpMessageContext.isAuthenticationRequest()) {
      if (httpMessageContext.getAuthParameters() instanceof AuthParameters authParams) {
        return authParams.getModule();
      }
    }
    var requestContext = ServletUtils.getRequestContext(request);
    return requestContext.getModule();
  }

  @Override
  public AuthenticationStatus validateRequest(HttpServletRequest request, HttpServletResponse response,
      HttpMessageContext httpMessageContext) throws AuthenticationException {

    // RequestContext (Remove this if HttpAuthenticationMechanismHandler used in Jakarta EE 11+)
    this.requestContextParser.initRequestContext(request, response);

    // credential
    var credential = getCredential(request, httpMessageContext);
    if (credential == null) {
      return httpMessageContext.doNothing();
    }

    var module = getModule(request, httpMessageContext);
    var reauthentication = isReauthentication(request, httpMessageContext);

    var authCredential = new AuthCredential(credential, module, httpMessageContext.isAuthenticationRequest(),
        reauthentication, httpMessageContext.getAuthParameters().isRememberMe());

    // Validate authCredential
    CredentialValidationResult result = null;
    try {
      result = this.identityStoreHandler.validate(authCredential);

    } catch (RuntimeException ex) {
      this.appLogger.error(ex);
      result = InvalidAuth.ID_STORE_EXCEPTION;
    }

    // Store CredentialValidationResult
    request.setAttribute(CredentialValidationResult.class.getName(), result);

    // VALID
    if (result.getStatus() == CredentialValidationResult.Status.VALID) {
      return httpMessageContext.notifyContainerAboutLogin(result);
    }
    return httpMessageContext.doNothing();
  }

  public abstract void askAuthenticate(HttpServletRequest request, HttpServletResponse response,
      RequestContext requestContext) throws Exception;

  public void askReauthenticate(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext)
      throws Exception {
    throw new UnsupportedOperationException("askReauthenticate(request, response, requestContext)");
  }

  // isValidateRequest

  private static final Class<?>[] VALIDATE_REQUEST_PARAMETER_TYPES = new Class<?>[] { HttpServletRequest.class,
      HttpServletResponse.class, HttpMessageContext.class };

  public static boolean isValidateRequest(Method ctxMth) {
    return HttpAuthenticationMechanism.class.isAssignableFrom(ctxMth.getDeclaringClass())
        && "validateRequest".equals(ctxMth.getName())
        && Arrays.equals(VALIDATE_REQUEST_PARAMETER_TYPES, ctxMth.getParameterTypes());
  }
}
