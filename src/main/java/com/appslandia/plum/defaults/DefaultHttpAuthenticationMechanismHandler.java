// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.defaults;

import com.appslandia.plum.base.HttpAuthMechanismProvider;
import com.appslandia.plum.base.RequestContextParser;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptor;
import jakarta.security.enterprise.AuthenticationException;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanismHandler;
import jakarta.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
@Alternative
@Priority(Interceptor.Priority.LIBRARY_BEFORE + 150)
public class DefaultHttpAuthenticationMechanismHandler implements HttpAuthenticationMechanismHandler {

  @Inject
  protected RequestContextParser requestContextParser;

  @Inject
  protected HttpAuthMechanismProvider httpAuthMechanismProvider;

  @Override
  public AuthenticationStatus validateRequest(HttpServletRequest request, HttpServletResponse response,
      HttpMessageContext httpMessageContext) throws AuthenticationException {

    var requestContext = requestContextParser.initRequestContext(request, response);
    var mechanism = httpAuthMechanismProvider.getMechanism(requestContext.getModule());

    return mechanism.validateRequest(request, response, httpMessageContext);
  }

  @Override
  public AuthenticationStatus secureResponse(HttpServletRequest request, HttpServletResponse response,
      HttpMessageContext httpMessageContext) throws AuthenticationException {

    var requestContext = ServletUtils.getRequestContext(request);
    var mechanism = httpAuthMechanismProvider.getMechanism(requestContext.getModule());

    return mechanism.secureResponse(request, response, httpMessageContext);
  }

  @Override
  public void cleanSubject(HttpServletRequest request, HttpServletResponse response,
      HttpMessageContext httpMessageContext) {

    var requestContext = ServletUtils.getRequestContext(request);
    var mechanism = httpAuthMechanismProvider.getMechanism(requestContext.getModule());

    mechanism.cleanSubject(request, response, httpMessageContext);
  }
}
