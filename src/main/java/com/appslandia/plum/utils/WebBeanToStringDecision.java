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

import java.lang.reflect.Field;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;

import com.appslandia.common.base.ToStringBuilder;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class WebBeanToStringDecision extends ToStringBuilder.FieldDecision {

	final boolean printRequest;
	final boolean printResponse;
	final boolean printSession;
	final boolean printServletContext;

	public WebBeanToStringDecision() {
		this(false, false, false, false);
	}

	public WebBeanToStringDecision(boolean printRequest, boolean printResponse, boolean printSession, boolean printServletContext) {
		this.printRequest = printRequest;
		this.printResponse = printResponse;
		this.printSession = printSession;
		this.printServletContext = printServletContext;
	}

	@Override
	protected boolean useHashInfo(Object value, Field field) {
		if (value instanceof ServletRequest) {
			return !this.printRequest;
		}
		if (value instanceof ServletResponse) {
			return !this.printResponse;
		}
		if (value instanceof HttpSession) {
			return !this.printSession;
		}
		if (value instanceof ServletContext) {
			return !this.printServletContext;
		}
		if (value.getClass().getName().endsWith("$Proxy$_$$_WeldClientProxy")) {
			return true;
		}
		if (field != null) {
			if (field.getName().startsWith("weld$$$") || field.getName().startsWith("weld_proxy_field$$$")) {
				return true;
			}
			if (field.getType().getName().startsWith("org.jboss.weld")) {
				return true;
			}
		}
		return false;
	}
}
