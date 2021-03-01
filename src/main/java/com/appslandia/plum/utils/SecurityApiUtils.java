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

package com.appslandia.plum.utils;

import java.lang.reflect.Method;

import javax.el.ELProcessor;
import javax.enterprise.inject.spi.BeanManager;
import javax.interceptor.InvocationContext;
import javax.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import javax.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appslandia.common.utils.ReflectionUtils;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class SecurityApiUtils {

	public final static Method VALIDATE_REQUEST_METHOD = ReflectionUtils.getDeclaredMethod(HttpAuthenticationMechanism.class, "validateRequest",
			HttpServletRequest.class, HttpServletResponse.class, HttpMessageContext.class);

	public static boolean isValidateRequestImpl(InvocationContext context) {
		return ReflectionUtils.isImplementOf(context.getMethod(), VALIDATE_REQUEST_METHOD);
	}

	public static ELProcessor getElProcessor(InvocationContext invocationContext, HttpMessageContext httpMessageContext, BeanManager beanManager) {
		ELProcessor elProcessor = new ELProcessor();

		elProcessor.getELManager().addELResolver(beanManager.getELResolver());
		elProcessor.defineBean("self", invocationContext.getTarget());
		elProcessor.defineBean("httpMessageContext", httpMessageContext);

		return elProcessor;
	}
}
