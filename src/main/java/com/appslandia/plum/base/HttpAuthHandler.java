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

import javax.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import javax.security.enterprise.credential.Credential;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appslandia.plum.utils.ServletUtils;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public abstract class HttpAuthHandler implements AuthHandler {

	protected abstract Credential parseCredential(String credential) throws Exception;

	@Override
	public Credential parseCredential(HttpServletRequest request) {
		String authorization = request.getHeader("Authorization");
		if (authorization == null) {
			return null;
		}
		int idx = authorization.indexOf(' ');
		if (idx <= 0) {
			return null;
		}
		String authMethod = authorization.substring(0, idx);
		String credential = authorization.substring(idx + 1);

		if (!getAuthMethod().equalsIgnoreCase(authMethod)) {
			return null;
		}
		if (credential.isEmpty()) {
			return null;
		}
		try {
			return parseCredential(credential);
		} catch (Exception ex) {
			return null;
		}
	}

	@Override
	public boolean isRememberMe(HttpMessageContext httpMessageContext) {
		return Boolean.TRUE.toString().equalsIgnoreCase(httpMessageContext.getRequest().getParameter("rememberMe"));
	}

	@Override
	public void askAuthenticate(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext) throws Exception {
		ServletUtils.setWWWAuthenticate(response, getAuthMethod(), requestContext.getModule());
	}

	@Override
	public void askReauthenticate(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext) throws Exception {
		throw new UnsupportedOperationException();
	}
}
