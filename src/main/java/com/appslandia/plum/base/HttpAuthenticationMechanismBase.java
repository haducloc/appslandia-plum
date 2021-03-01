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

import javax.inject.Inject;
import javax.security.enterprise.AuthenticationException;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.authentication.mechanism.http.AuthenticationParameters;
import javax.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import javax.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStoreHandler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appslandia.plum.utils.ServletUtils;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public abstract class HttpAuthenticationMechanismBase implements HttpAuthenticationMechanism {

	@Inject
	protected IdentityStoreHandler identityStoreHandler;

	@Inject
	protected AuthHandlerProvider authHandlerProvider;

	@Inject
	protected RequestContextParser requestContextParser;

	/**
	 * 
	 * @param request
	 * @param response
	 * @param httpMessageContext
	 * @return AuthenticationStatus.SUCCESS or AuthenticationStatus.NOT_DONE
	 * @throws AuthenticationException
	 */
	@Override
	public AuthenticationStatus validateRequest(HttpServletRequest request, HttpServletResponse response, HttpMessageContext httpMessageContext)
			throws AuthenticationException {
		// AuthHandler
		RequestContext requestContext = this.requestContextParser.parse(request, response);
		AuthHandler authHandler = this.authHandlerProvider.getAuthHandler(requestContext.getModule());

		// Credential
		Credential credential = httpMessageContext.isAuthenticationRequest() ? httpMessageContext.getAuthParameters().getCredential()
				: authHandler.parseCredential(request);
		if (credential == null) {
			return httpMessageContext.doNothing();
		}

		// AuthCredential
		AuthenticationParameters parameters = httpMessageContext.getAuthParameters();
		boolean reauthentication = (parameters instanceof AuthParameters) ? ((AuthParameters) parameters).isReauthentication()
				: authHandler.isReauthentication(request);

		AuthCredential authCredential = new AuthCredential(credential, requestContext.getModule(), httpMessageContext.isAuthenticationRequest(),
				reauthentication, parameters.isRememberMe());

		// Validate authCredential
		CredentialValidationResult result = null;
		try {
			result = this.identityStoreHandler.validate(authCredential);
		} catch (RuntimeException ex) {
			result = AuthFailureResult.ID_STORE_EXCEPTION;
		}

		// NOT VALID
		if (result.getStatus() != CredentialValidationResult.Status.VALID) {
			request.setAttribute(CredentialValidationResult.class.getName(), result);

			return httpMessageContext.doNothing();
		}
		// VALID
		return httpMessageContext.notifyContainerAboutLogin(result);
	}

	@Override
	public AuthenticationStatus secureResponse(HttpServletRequest request, HttpServletResponse response, HttpMessageContext httpMessageContext)
			throws AuthenticationException {
		RequestContext requestContext = this.requestContextParser.parse(request, response);
		return this.authHandlerProvider.getAuthHandler(requestContext.getModule()).secureResponse(request, response, httpMessageContext);
	}

	private static final String REQUEST_ATTRIBUTE_CLEAN_SUBJECT = HttpAuthenticationMechanismBase.class.getName() + ".cleanSubject";

	@Override
	public void cleanSubject(HttpServletRequest request, HttpServletResponse response, HttpMessageContext httpMessageContext) {
		// FIX: cleanSubject was called twice
		if (request.getAttribute(REQUEST_ATTRIBUTE_CLEAN_SUBJECT) == null) {
			request.setAttribute(REQUEST_ATTRIBUTE_CLEAN_SUBJECT, true);

			this.authHandlerProvider.getAuthHandler(ServletUtils.getRequestContext(request).getModule()).cleanSubject(request, response, httpMessageContext);
		}
	}

	// //@formatter:off
	// @RememberMe(
	// isRememberMeExpression = "#{self.rememberMe(httpMessageContext)}",
	// cookieName = "#{self.rememberMeCookieName()}",
	// cookieMaxAgeSecondsExpression = "#{self.rememberMeCookieAge()}",
	// cookieSecureOnlyExpression="#{self.rememberMeCookieSecure()}",
	// cookieHttpOnlyExpression="#{self.rememberMeCookieHttpOnly()}"
	// )
	// //@formatter:on

	@Inject
	protected AppConfig appConfig;

	public boolean rememberMe(HttpMessageContext httpMessageContext) {
		RequestContext requestContext = ServletUtils.getRequestContext(httpMessageContext.getRequest());
		return this.authHandlerProvider.getAuthHandler(requestContext.getModule()).isRememberMe(httpMessageContext);
	}

	public String rememberMeCookieName() {
		return this.appConfig.getRequiredString(AppConfig.CONFIG_REMME_COOKIE_NAME);
	}

	public int rememberMeCookieAge() {
		return this.appConfig.getRequiredInt(AppConfig.CONFIG_REMME_COOKIE_AGE);
	}

	public boolean rememberMeCookieSecure() {
		return this.appConfig.getRequiredBool(AppConfig.CONFIG_REMME_COOKIE_SECURE);
	}

	public boolean rememberMeCookieHttpOnly() {
		return this.appConfig.getRequiredBool(AppConfig.CONFIG_REMME_COOKIE_HTTPONLY);
	}
}
