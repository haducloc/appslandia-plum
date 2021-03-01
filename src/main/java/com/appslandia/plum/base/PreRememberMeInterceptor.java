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

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.authentication.mechanism.http.RememberMe;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appslandia.common.logging.AppLogger;
import com.appslandia.common.utils.AssertUtils;
import com.appslandia.plum.base.RemMeIdentityStore.RemMeCookieUpdater;
import com.appslandia.plum.utils.SecurityApiUtils;
import com.appslandia.plum.utils.ServletUtils;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@Interceptor
@RememberMe
@Priority(Interceptor.Priority.PLATFORM_BEFORE + 205)
public class PreRememberMeInterceptor implements Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	protected AppLogger appLogger;

	@Inject
	protected PostRememberMe postRememberMe;

	@Inject
	protected RequestContextParser requestContextParser;

	@AroundInvoke
	public Object intercept(InvocationContext context) throws Exception {
		// > Not validateRequest method
		if (!SecurityApiUtils.isValidateRequestImpl(context)) {
			return context.proceed();
		}

		Object[] parameters = context.getParameters();
		HttpServletRequest request = (HttpServletRequest) parameters[0];
		HttpServletResponse response = (HttpServletResponse) parameters[1];

		if (request.getUserPrincipal() != null) {
			this.appLogger.warn("PreRememberMeInterceptor must be executed between AutoApplySessionInterceptor(200) and RememberMeInterceptor(210).");
			return context.proceed();
		}

		// Parse RequestContext before RememberMeInterceptor
		this.requestContextParser.parse(request, response);

		AuthenticationStatus status = (AuthenticationStatus) context.proceed();
		if (status != AuthenticationStatus.SUCCESS) {
			return status;
		}

		// > AuthenticationStatus.SUCCESS

		// From com.appslandia.plum.base.RemMeIdentityStore?
		RemMeCookieUpdater remMeCookieUpdater = (RemMeCookieUpdater) ServletUtils.removeAttribute(request, RemMeCookieUpdater.class.getName());
		if (remMeCookieUpdater != null) {
			remMeCookieUpdater.apply(request, response);

			String tokenIdentity = (String) ServletUtils.removeAttribute(request, LoginToken.class.getName());
			AssertUtils.assertNotNull(tokenIdentity);

			this.postRememberMe.apply(request, response, tokenIdentity);
		}
		return AuthenticationStatus.SUCCESS;
	}
}
