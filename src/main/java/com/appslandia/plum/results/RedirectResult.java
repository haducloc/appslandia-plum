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

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appslandia.common.utils.AssertUtils;
import com.appslandia.common.utils.URLUtils;
import com.appslandia.plum.base.ActionParser;
import com.appslandia.plum.base.ActionResult;
import com.appslandia.plum.base.AppConfig;
import com.appslandia.plum.base.RequestContext;
import com.appslandia.plum.base.TempData;
import com.appslandia.plum.base.TempDataManager;
import com.appslandia.plum.utils.ServletUtils;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class RedirectResult implements ActionResult {

	private String controller;
	private String action;
	private Map<String, Object> params;

	private String location;
	private int status = HttpServletResponse.SC_MOVED_TEMPORARILY;
	private boolean external;

	public RedirectResult() {
	}

	public RedirectResult(String action) {
		this.action = action;
	}

	public RedirectResult(String action, String controller) {
		this.action = action;
		this.controller = controller;
	}

	public RedirectResult status(int status) {
		this.status = status;
		return this;
	}

	public RedirectResult location(String location) {
		this.location = location;
		this.external = false;
		return this;
	}

	public RedirectResult externalUrl(String url) {
		this.location = url;
		this.external = true;
		return this;
	}

	public RedirectResult query(String name, Object value) {
		getParams().put(name, value);
		return this;
	}

	public RedirectResult queryString(String queryString) {
		getParams().put(ActionParser.PARAM_QUERY_STRING, queryString);
		return this;
	}

	private Map<String, Object> getParams() {
		if (this.params == null) {
			this.params = new LinkedHashMap<>();
		}
		return this.params;
	}

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext) throws Exception {
		AppConfig appConfig = ServletUtils.getAppScoped(request, AppConfig.class);

		// location?
		if (this.location != null) {
			if (!this.external) {
				saveTempData(request, response);
			}
			if (this.params != null) {
				sendRedirect(response, URLUtils.toUrl(this.location, this.params), appConfig.isEnableSession());
			} else {
				sendRedirect(response, this.location, appConfig.isEnableSession());
			}
			return;
		}

		// Internal controller/action
		this.external = false;
		AssertUtils.assertNotNull(this.action);

		if (this.controller == null) {
			this.controller = requestContext.getActionDesc().getController();
		}
		saveTempData(request, response);

		// URL
		ActionParser actionParser = ServletUtils.getAppScoped(request, ActionParser.class);
		String url = actionParser.toActionUrl(request, this.controller, this.action, this.params, false);
		sendRedirect(response, url, appConfig.isEnableSession());
	}

	protected void sendRedirect(HttpServletResponse response, String url, boolean enableSession) throws Exception {
		if (this.external) {
			ServletUtils.sendRedirect(response, url, this.status);
		} else {
			if (this.status == HttpServletResponse.SC_MOVED_TEMPORARILY) {
				response.sendRedirect(enableSession ? response.encodeRedirectURL(url) : url);
			} else {
				ServletUtils.sendRedirect(response, enableSession ? response.encodeRedirectURL(url) : url, this.status);
			}
		}
	}

	protected void saveTempData(HttpServletRequest request, HttpServletResponse response) throws Exception {
		TempDataManager tempDataManager = ServletUtils.getAppScoped(request, TempDataManager.class);

		String tempDataId = tempDataManager.saveTempData(request, response);
		if (tempDataId != null) {
			query(TempDataManager.PARAM_TEMP_DATA_ID, tempDataId);
		}
	}

	public static final RedirectResult ROOT = new RedirectResult() {

		@Override
		public void execute(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext) throws Exception {
			AppConfig appConfig = ServletUtils.getAppScoped(request, AppConfig.class);
			TempDataManager tempDataManager = ServletUtils.getAppScoped(request, TempDataManager.class);

			StringBuilder url = new StringBuilder();
			url.append(request.getServletContext().getContextPath());

			if (requestContext.isPathLanguage()) {
				url.append('/').append(requestContext.getLanguageId());
			}
			if (url.length() == 0) {
				url.append('/');
			}
			TempData tempData = tempDataManager.buildTempData(request);
			if ((tempData != null) && !tempData.isEmpty()) {

				String tempDataId = tempDataManager.saveTempData(request, response, tempData);
				if (tempDataId != null) {
					url.append('?').append(TempDataManager.PARAM_TEMP_DATA_ID).append('=').append(tempDataId);
				}
			}
			response.sendRedirect(appConfig.isEnableSession() ? response.encodeRedirectURL(url.toString()) : url.toString());
		}

		@Override
		public RedirectResult status(int status) {
			throw new UnsupportedOperationException();
		}

		@Override
		public RedirectResult location(String location) {
			throw new UnsupportedOperationException();
		}

		@Override
		public RedirectResult externalUrl(String url) {
			throw new UnsupportedOperationException();
		}

		@Override
		public RedirectResult query(String name, Object value) {
			throw new UnsupportedOperationException();
		}
	};
}
