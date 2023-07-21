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

package com.appslandia.plum.results;

import com.appslandia.common.utils.Asserts;
import com.appslandia.plum.base.ActionResult;
import com.appslandia.plum.base.AppConfig;
import com.appslandia.plum.base.RequestContext;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class JspResult implements ActionResult {

    private String action;
    private String controller;
    private String path;

    public JspResult() {
    }

    public JspResult(String action) {
	this.action = action;
    }

    public JspResult(String action, String controller) {
	this.action = action;
	this.controller = controller;
    }

    public JspResult path(String path) {
	this.path = path;
	return this;
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext) throws Exception {
	AppConfig appConfig = ServletUtils.getAppScoped(request, AppConfig.class);
	String jspPath = null;

	if (this.path == null) {
	    String action = (this.action != null) ? this.action : requestContext.getActionDesc().getAction();
	    String controller = (this.controller != null) ? this.controller : requestContext.getActionDesc().getController();

	    jspPath = appConfig.getViewBase().append("/").append(controller).append("/").append(action).append(".jsp").toString();

	} else {
	    jspPath = appConfig.getViewBase().append(this.path).toString();
	}

	Asserts.isTrue(!request.isAsyncStarted());

	if (requestContext.getActionDesc().getChildAction() == null) {
	    ServletUtils.forward(request, response, jspPath);
	} else {
	    ServletUtils.include(request, response, jspPath);
	}
    }

    public static final JspResult DEFAULT = new JspResult() {

	@Override
	public JspResult path(String path) {
	    throw new UnsupportedOperationException();
	}
    };
}
