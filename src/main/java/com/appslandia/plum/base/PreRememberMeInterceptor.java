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

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;

import com.appslandia.common.logging.AppLogger;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import jakarta.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import jakarta.security.enterprise.authentication.mechanism.http.RememberMe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@Interceptor
@RememberMe
// See @Priority org.glassfish.soteria.cdi.RememberMeInterceptor
@Priority(Interceptor.Priority.PLATFORM_BEFORE + 205)
public class PreRememberMeInterceptor implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    protected AppLogger appLogger;

    @Inject
    protected AppConfig appConfig;

    @Inject
    protected CookieHandler cookieHandler;

    @Inject
    protected PostRememberMe postRememberMe;

    @Inject
    protected RequestContextParser requestContextParser;

    @AroundInvoke
    public Object intercept(InvocationContext context) throws Exception {

	// If not intercepting HttpAuthenticationMechanism#validateRequest
	if (!isValidateRequest(context.getMethod())) {
	    return context.proceed();
	}
	Object[] parameters = context.getParameters();
	HttpServletRequest request = (HttpServletRequest) parameters[0];
	HttpServletResponse response = (HttpServletResponse) parameters[1];

	if (request.getUserPrincipal() != null) {
	    this.appLogger.warn("PreRememberMeInterceptor must be executed between AutoApplySessionInterceptor() and RememberMeInterceptor().");
	    return context.proceed();
	}

	// Try to authenticate with the next interceptor or actual authentication mechanism
	AuthenticationStatus status = (AuthenticationStatus) context.proceed();
	if (status != AuthenticationStatus.SUCCESS) {
	    return status;
	}

	// > AuthenticationStatus.SUCCESS

	// From RemMeIdentityStore?
	RemMeIdentityStore.ReissuedToken reissuedToken = (RemMeIdentityStore.ReissuedToken) ServletUtils.removeAttribute(request, RemMeIdentityStore.ReissuedToken.class.getName());
	if (reissuedToken != null) {

	    // Re-issue RememberMe cookie
	    this.cookieHandler.saveCookie(response, this.appConfig.getRequiredString(AppConfig.CONFIG_REMME_COOKIE_NAME), reissuedToken.getLoginToken(), reissuedToken.getMaxAge(),
		    (c) -> {
			c.setSecure(this.appConfig.getBool(AppConfig.CONFIG_REMME_COOKIE_SECURE));
			c.setHttpOnly(this.appConfig.getBool(AppConfig.CONFIG_REMME_COOKIE_HTTPONLY));
		    });

	    this.postRememberMe.apply(request, response, reissuedToken.getIdentity());
	}
	return AuthenticationStatus.SUCCESS;
    }

    static final Class<?>[] VALIDATE_REQUEST_PARAMETER_TYPES = new Class<?>[] { HttpServletRequest.class, HttpServletResponse.class, HttpMessageContext.class };

    static boolean isValidateRequest(Method ctxMth) {
	return HttpAuthenticationMechanism.class.isAssignableFrom(ctxMth.getDeclaringClass()) && "validateRequest".equals(ctxMth.getName())
		&& Arrays.equals(VALIDATE_REQUEST_PARAMETER_TYPES, ctxMth.getParameterTypes());
    }
}
