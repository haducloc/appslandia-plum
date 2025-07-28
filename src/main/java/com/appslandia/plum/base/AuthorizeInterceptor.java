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

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

/**
 *
 * @author Loc Ha
 *
 */
@Authorize
@Interceptor
@Priority(Interceptor.Priority.LIBRARY_BEFORE + 150)
public class AuthorizeInterceptor implements Serializable {
  private static final long serialVersionUID = 1L;

  @Inject
  protected AuthContext authContext;

  @Inject
  protected AuthorizePolicyProvider authorizePolicyProvider;

  @AroundInvoke
  public Object intercept(InvocationContext context) throws Exception {

    // Bypass Controller
    if (context.getMethod().getDeclaringClass().getAnnotation(Controller.class) != null) {
      return context.proceed();
    }

    // Authorize
    var authorize = context.getMethod().getAnnotation(Authorize.class);
    if (authorize == null) {
      authorize = context.getTarget().getClass().getAnnotation(Authorize.class);
    }
    if (authorize == null) {
      return context.proceed();
    }

    // UserPrincipal
    var principal = this.authContext.getPrincipal();
    if (principal == null) {
      throw new UnauthorizedException(context.getMethod().toString());
    }
    var roles = authorize.roles();
    var policies = authorize.policies();

    if ((roles.length == 0) && (policies.length == 0)) {
      return context.proceed();
    }
    if (roles.length > 0) {
      if (this.authContext.isUserInRoles(roles)) {
        return context.proceed();
      }
    }
    if (policies.length > 0) {
      if (this.authorizePolicyProvider.authorize(principal, policies)) {
        return context.proceed();
      }
    }
    throw new ForbiddenException(context.getMethod().toString());
  }
}
