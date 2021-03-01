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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appslandia.plum.base.ActionResult;
import com.appslandia.plum.base.AppConfig;
import com.appslandia.plum.base.RequestContext;
import com.appslandia.plum.utils.ServletUtils;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class LangJspResult implements ActionResult {

	private String action;
	private String controller;

	protected LangJspResult() {
	}

	public LangJspResult(String action) {
		this.action = action;
	}

	public LangJspResult(String action, String controller) {
		this.action = action;
		this.controller = controller;
	}

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext) throws Exception {
		AppConfig appConfig = ServletUtils.getAppScoped(request, AppConfig.class);

		String action = (this.action != null) ? this.action : requestContext.getActionDesc().getAction();
		String controller = (this.controller != null) ? this.controller : requestContext.getActionDesc().getController();

		String jspPath = appConfig.getViewPathBase().append("/").append(controller).append("/").append(action).append(".")
				.append(requestContext.getLanguageId()).append(".jsp").toString();

		if (requestContext.getActionDesc().getChildAction() == null) {
			ServletUtils.forward(request, response, jspPath);
		} else {
			ServletUtils.include(request, response, jspPath);
		}
	}

	public static final LangJspResult DEFAULT = new LangJspResult();
}
