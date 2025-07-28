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

package com.appslandia.plum.defaults;

import com.appslandia.plum.base.HttpAuthMechanismProvider;
import com.appslandia.plum.base.RequestContextParser;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.inject.Inject;
import jakarta.security.enterprise.AuthenticationException;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
//@ApplicationScoped
public class DefaultHttpAuthenticationMechanismHandler implements HttpAuthenticationMechanismHandler {

  @Inject
  protected RequestContextParser requestContextParser;

  @Inject
  protected HttpAuthMechanismProvider httpAuthMechanismProvider;

  @Override
  public AuthenticationStatus validateRequest(HttpServletRequest request, HttpServletResponse response,
      HttpMessageContext httpMessageContext) throws AuthenticationException {

    var requestContext = this.requestContextParser.initRequestContext(request, response);
    var mechanism = this.httpAuthMechanismProvider.getMechanism(requestContext.getModule());

    return mechanism.validateRequest(request, response, httpMessageContext);
  }

  @Override
  public AuthenticationStatus secureResponse(HttpServletRequest request, HttpServletResponse response,
      HttpMessageContext httpMessageContext) throws AuthenticationException {

    var requestContext = ServletUtils.getRequestContext(request);
    var mechanism = this.httpAuthMechanismProvider.getMechanism(requestContext.getModule());

    return mechanism.secureResponse(request, response, httpMessageContext);
  }

  @Override
  public void cleanSubject(HttpServletRequest request, HttpServletResponse response,
      HttpMessageContext httpMessageContext) {

    var requestContext = ServletUtils.getRequestContext(request);
    var mechanism = this.httpAuthMechanismProvider.getMechanism(requestContext.getModule());

    mechanism.cleanSubject(request, response, httpMessageContext);
  }
}
