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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appslandia.common.base.Language;
import com.appslandia.common.formatters.FormatterProvider;
import com.appslandia.plum.utils.ServletUtils;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@ApplicationScoped
public class RequestContextParser {

	@Inject
	protected PrefCookieHandler prefCookieHandler;

	@Inject
	protected LanguageProvider languageProvider;

	@Inject
	protected ResourcesProvider resourcesProvider;

	@Inject
	protected FormatterProvider formatterProvider;

	@Inject
	protected FormatProviderManager formatProviderManager;

	@Inject
	protected AppConfig appConfig;

	@Inject
	protected ActionParser actionParser;

	@Inject
	protected ClientIdParser clientIdParser;

	public RequestContext parse(HttpServletRequest request, HttpServletResponse response) {
		RequestContext context = (RequestContext) request.getAttribute(RequestContext.REQUEST_ATTRIBUTE_ID);
		if (context != null) {
			return context;
		}
		// PrefCookie
		this.prefCookieHandler.loadPrefCookie(request, response);

		// Initialize RequestContext
		context = new RequestContext();
		context.setRequestUrl(ServletUtils.getRequestUrl(request));
		context.setGetOrHead(HttpMethod.GET.equals(request.getMethod()) || HttpMethod.HEAD.equals(request.getMethod()));
		context.setFormatterProvider(this.formatterProvider);

		List<String> pathItems = parsePathItems(request);
		String pathLanguage = !pathItems.isEmpty() ? pathItems.get(0) : null;
		initLanguageContext(request, context, pathLanguage);

		if (context.isPathLanguage()) {
			pathItems.remove(0);
		}

		// ActionDesc
		Map<String, String> pathParamMap = new HashMap<>();
		ActionDesc actionDesc = this.actionParser.parse(pathItems, pathParamMap);
		context.setActionDesc(actionDesc);
		context.setPathParamMap(Collections.unmodifiableMap(pathParamMap));

		context.setClientId(this.clientIdParser.parseId(request));
		context.setModule(getModule(request, actionDesc));
		context.setLocalhost(ServletUtils.isLocalhost(request.getServerName()));

		request.setAttribute(RequestContext.REQUEST_ATTRIBUTE_ID, context);
		return context;
	}

	protected void initLanguageContext(HttpServletRequest request, RequestContext context, String pathLanguage) {
		Language language = null;
		if (pathLanguage == null) {
			language = parseLanguage(request);
		} else {
			language = this.languageProvider.getLanguage(pathLanguage);
			if (language == null) {
				language = parseLanguage(request);
			} else {
				context.setPathLanguage(true);
			}
		}
		context.setFormatProvider(this.formatProviderManager.get(language));
		context.setResources(this.resourcesProvider.getResources(language.getId()));
	}

	protected Language parseLanguage(HttpServletRequest request) {
		if (this.appConfig.isPrefLang()) {

			PrefCookie prefCookie = (PrefCookie) request.getAttribute(PrefCookie.REQUEST_ATTRIBUTE_ID);
			if ((prefCookie != null) && prefCookie.getLanguage() != null) {

				Language language = this.languageProvider.getLanguage(prefCookie.getLanguage());
				if (language != null) {
					return language;
				}
			}
		}
		return this.languageProvider.getLanguage(request);
	}

	protected String getModule(HttpServletRequest request, ActionDesc actionDesc) {
		if (actionDesc != null) {
			return actionDesc.getModule();
		}
		UserPrincipal principal = ServletUtils.getUserPrincipal(request);
		if (principal != null) {
			return principal.getModule();
		}
		return this.appConfig.getModule();
	}

	public static List<String> parsePathItems(HttpServletRequest request) {
		String requestURI = request.getRequestURI();
		String contextPath = request.getServletContext().getContextPath();

		int startIdx = 0;
		int endIdx;
		boolean isCtxPath = true;
		List<String> list = new ArrayList<>(8);

		while ((endIdx = requestURI.indexOf('/', startIdx)) != -1) {
			String pathItem = requestURI.substring(startIdx, endIdx);
			if (!pathItem.isEmpty()) {
				if (contextPath.isEmpty() || !isCtxPath) {
					list.add(pathItem);
				} else if (isCtxPath) {
					isCtxPath = false;
				}
			}
			startIdx = endIdx + 1;
		}
		if (startIdx < requestURI.length()) {
			String pathItem = requestURI.substring(startIdx);
			if (!pathItem.isEmpty()) {
				if (contextPath.isEmpty() || !isCtxPath) {
					list.add(pathItem);
				} else if (isCtxPath) {
					isCtxPath = false;
				}
			}
		}
		if (!list.isEmpty()) {
			String lastItem = list.get(list.size() - 1);
			// ;jsessionid=
			String incSid = String.format(";%s=", request.getServletContext().getSessionCookieConfig().getName().toLowerCase(Locale.ENGLISH));
			int idx = lastItem.indexOf(incSid);

			if (idx == 0) {
				list.remove(list.size() - 1);
			} else if (idx > 0) {
				list.set(list.size() - 1, lastItem.substring(0, idx));
			}
		}
		return list;
	}
}
