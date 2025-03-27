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

import java.io.Serializable;
import java.security.Principal;

import javax.security.auth.callback.Callback;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.security.auth.message.callback.CallerPrincipalCallback;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.authentication.mechanism.http.AutoApplySession;
import jakarta.security.enterprise.authentication.mechanism.http.HttpMessageContext;

/**
 *
 * @author Loc Ha
 *
 */
@Interceptor
@AutoApplySession
@Priority(Interceptor.Priority.PLATFORM_BEFORE + 202)
// AutoApplySessionInterceptor  @Priority(PLATFORM_BEFORE + 200)
public class ConditionalAutoApplySessionInterceptor implements Serializable {
  private static final long serialVersionUID = 1L;

  @Inject
  protected AuthHandlerProvider authHandlerProvider;

  @Inject
  protected RequestContextParser requestContextParser;

  @AroundInvoke
  public Object intercept(InvocationContext context) throws Exception {

    // If not intercepting HttpAuthenticationMechanism#validateRequest
    if (!HttpAuthenticationMechanismBase.isValidateRequest(context.getMethod())) {
      return context.proceed();
    }

    HttpMessageContext httpMessageContext = (HttpMessageContext) context.getParameters()[2];
    Principal userPrincipal = httpMessageContext.getRequest().getUserPrincipal();

    if (userPrincipal != null) {
      httpMessageContext.getHandler()
          .handle(new Callback[] { new CallerPrincipalCallback(httpMessageContext.getClientSubject(), userPrincipal) });

      return AuthenticationStatus.SUCCESS;
    }

    // context.proceed()
    Object result = context.proceed();
    if (AuthenticationStatus.SUCCESS.equals(result)) {

      RequestContext requestContext = this.requestContextParser.parse(httpMessageContext.getRequest(),
          httpMessageContext.getResponse());
      AuthHandler authHandler = this.authHandlerProvider.getAuthHandler(requestContext.getModule());

      // Register a session only for qualified authentication methods.
      if (authHandler.getAuthMethod().isHttpSession()) {

        httpMessageContext.getMessageInfo().getMap().put("jakarta.servlet.http.registerSession",
            Boolean.TRUE.toString());
      }
    }
    return result;
  }
}
