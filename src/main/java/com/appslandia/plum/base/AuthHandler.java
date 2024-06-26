// The MIT License (MIT)
// Copyright © 2015 AppsLandia. All rights reserved.

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

import jakarta.security.enterprise.AuthenticationException;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import jakarta.security.enterprise.credential.Credential;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public interface AuthHandler {

  String getAuthMethod();

  Credential parseCredential(HttpServletRequest request);

  default String getLoginPage() {
    throw new UnsupportedOperationException();
  }

  default boolean isReauthentication(HttpServletRequest request) {
    return false;
  }

  default public AuthenticationStatus secureResponse(HttpServletRequest request, HttpServletResponse response,
      HttpMessageContext httpMessageContext) throws AuthenticationException {
    return AuthenticationStatus.SUCCESS;
  }

  default public void cleanSubject(HttpServletRequest request, HttpServletResponse response,
      HttpMessageContext httpMessageContext) {
    httpMessageContext.cleanClientSubject();
  }

  boolean isRememberMe(HttpMessageContext httpMessageContext);

  void askAuthenticate(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext)
      throws Exception;

  default public void askReauthenticate(HttpServletRequest request, HttpServletResponse response,
      RequestContext requestContext) throws Exception {
    askAuthenticate(request, response, requestContext);
  }
}
