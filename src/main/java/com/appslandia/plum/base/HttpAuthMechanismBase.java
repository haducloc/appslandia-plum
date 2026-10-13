// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.lang.reflect.Method;
import java.util.Arrays;

import com.appslandia.common.utils.Asserts;
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

  public abstract String getModule();

  public abstract AuthMethod getAuthMethod();

  protected abstract Credential getCredential(HttpServletRequest request, HttpMessageContext httpMessageContext);

  protected boolean isReauthentication(HttpServletRequest request, HttpMessageContext httpMessageContext) {
    return false;
  }

  @Override
  public AuthenticationStatus validateRequest(HttpServletRequest request, HttpServletResponse response,
      HttpMessageContext httpMessageContext) throws AuthenticationException {

    // credential
    var credential = getCredential(request, httpMessageContext);
    if (credential == null) {
      return httpMessageContext.doNothing();
    }

    var authModule = getAuthModule(request, httpMessageContext);
    Asserts.isTrue(!ModuleConfig.MODULE_NONE.equals(authModule));

    if (!getModule().equals(authModule)) {
      return httpMessageContext.doNothing();
    }

    var reauthentication = isReauthentication(request, httpMessageContext);
    var authCredential = new AuthCredential(credential, authModule, httpMessageContext.isAuthenticationRequest(),
        reauthentication, httpMessageContext.getAuthParameters().isRememberMe());

    // Validate authCredential
    CredentialValidationResult result = null;
    try {
      result = identityStoreHandler.validate(authCredential);

    } catch (RuntimeException ex) {
      appLogger.error(ex);
      result = AuthInvalidResult.toResult(AuthResult.AUTH_INTERNAL_ERROR);
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

  public static String getAuthModule(HttpServletRequest request, HttpMessageContext httpMessageContext) {
    if (httpMessageContext.isAuthenticationRequest()) {
      if (httpMessageContext.getAuthParameters() instanceof AuthParameters authParams) {
        return authParams.getModule();
      }
    }
    var requestContext = ServletUtils.getRequestContext(request);
    return requestContext.getModule();
  }
}
