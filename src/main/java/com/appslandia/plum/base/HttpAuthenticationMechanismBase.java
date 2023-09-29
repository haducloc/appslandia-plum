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

import com.appslandia.plum.utils.ServletUtils;

import jakarta.inject.Inject;
import jakarta.security.enterprise.AuthenticationException;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.authentication.mechanism.http.AuthenticationParameters;
import jakarta.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import jakarta.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import jakarta.security.enterprise.credential.Credential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStoreHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
    public AuthenticationStatus validateRequest(HttpServletRequest request, HttpServletResponse response, HttpMessageContext httpMessageContext) throws AuthenticationException {
	// AuthHandler
	RequestContext requestContext = this.requestContextParser.parse(request, response);
	AuthHandler authHandler = this.authHandlerProvider.getAuthHandler(requestContext.getModule());

	// Credential
	Credential credential = httpMessageContext.isAuthenticationRequest() ? httpMessageContext.getAuthParameters().getCredential() : authHandler.parseCredential(request);
	if (credential == null) {
	    return httpMessageContext.doNothing();
	}

	// AuthCredential
	AuthenticationParameters parameters = httpMessageContext.getAuthParameters();
	boolean reauthentication = (parameters instanceof AuthParameters) ? ((AuthParameters) parameters).isReauthentication() : authHandler.isReauthentication(request);

	AuthCredential authCredential = new AuthCredential(credential, requestContext.getModule(), httpMessageContext.isAuthenticationRequest(), reauthentication,
		parameters.isRememberMe());

	// Validate authCredential
	CredentialValidationResult result = null;
	try {
	    result = this.identityStoreHandler.validate(authCredential);
	} catch (RuntimeException ex) {
	    result = InvalidAuthResult.ID_STORE_EXCEPTION;
	}

	// Store CredentialValidationResult
	request.setAttribute(CredentialValidationResult.class.getName(), result);

	// VALID
	if (result.getStatus() == CredentialValidationResult.Status.VALID) {
	    return httpMessageContext.notifyContainerAboutLogin(result);
	}
	return httpMessageContext.doNothing();
    }

    @Override
    public AuthenticationStatus secureResponse(HttpServletRequest request, HttpServletResponse response, HttpMessageContext httpMessageContext) throws AuthenticationException {
	RequestContext requestContext = this.requestContextParser.parse(request, response);
	AuthHandler authHandler = this.authHandlerProvider.getAuthHandler(requestContext.getModule());
	return authHandler.secureResponse(request, response, httpMessageContext);
    }

    private static final String REQUEST_ATTRIBUTE_CLEAN_SUBJECT = HttpAuthenticationMechanismBase.class.getName() + ".cleanSubject";

    @Override
    public void cleanSubject(HttpServletRequest request, HttpServletResponse response, HttpMessageContext httpMessageContext) {
	// FIX: cleanSubject was called twice
	if (request.getAttribute(REQUEST_ATTRIBUTE_CLEAN_SUBJECT) == null) {
	    request.setAttribute(REQUEST_ATTRIBUTE_CLEAN_SUBJECT, true);

	    String module = ServletUtils.getRequestContext(request).getModule();
	    AuthHandler authHandler = this.authHandlerProvider.getAuthHandler(module);
	    authHandler.cleanSubject(request, response, httpMessageContext);
	}
    }

    // @RememberMe(
    // isRememberMeExpression = "#{self.rememberMe(httpMessageContext)}",
    // cookieName = "#{self.rememberMeCookieName()}",
    // cookieMaxAgeSecondsExpression = "#{self.rememberMeCookieAge()}",
    // cookieSecureOnlyExpression="#{self.rememberMeCookieSecure()}",
    // cookieHttpOnlyExpression="#{self.rememberMeCookieHttpOnly()}"
    // )

    @Inject
    protected AppConfig appConfig;

    public boolean rememberMe(HttpMessageContext httpMessageContext) {
	String module = ServletUtils.getRequestContext(httpMessageContext.getRequest()).getModule();
	AuthHandler authHandler = this.authHandlerProvider.getAuthHandler(module);
	return authHandler.isRememberMe(httpMessageContext);
    }

    public String rememberMeCookieName() {
	return this.appConfig.getStringReq(AppConfig.CONFIG_REMME_COOKIE_NAME);
    }

    public int rememberMeCookieAge() {
	return this.appConfig.getInt(AppConfig.CONFIG_REMME_COOKIE_AGE);
    }

    public boolean rememberMeCookieSecure() {
	return this.appConfig.getBool(AppConfig.CONFIG_REMME_COOKIE_SECURE);
    }

    public boolean rememberMeCookieHttpOnly() {
	return this.appConfig.getBool(AppConfig.CONFIG_REMME_COOKIE_HTTPONLY);
    }
}
