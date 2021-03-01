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

package com.appslandia.plum.tags;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.DynamicAttributes;

import com.appslandia.common.base.MemoryStream;
import com.appslandia.common.caching.AppCache;
import com.appslandia.common.caching.AppCacheManager;
import com.appslandia.common.utils.AssertUtils;
import com.appslandia.common.utils.IOUtils;
import com.appslandia.plum.base.ActionDesc;
import com.appslandia.plum.base.ActionDescProvider;
import com.appslandia.plum.base.ActionDescUtils;
import com.appslandia.plum.base.ActionInvoker;
import com.appslandia.plum.base.ActionResult;
import com.appslandia.plum.base.ContentResponseWrapper;
import com.appslandia.plum.base.ControllerProvider;
import com.appslandia.plum.base.RequestAttributes;
import com.appslandia.plum.base.RequestContext;
import com.appslandia.plum.base.ResponseKeyBuilder;
import com.appslandia.plum.utils.ServletUtils;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@Tag(name = "include")
public class IncludeTag extends TagBase implements DynamicAttributes {

	public static final String REQUEST_ATTRIBUTE_INCLUDE_PARAMS = "includeParams";

	protected String controller;
	protected String action;
	protected String errorKey;

	protected String cacheKey;
	protected String cacheName;

	protected Map<String, Object> params;

	@Override
	public void setDynamicAttribute(String uri, String name, Object value) throws JspException {
		if (this.params == null) {
			this.params = new LinkedHashMap<>();
		}
		this.params.put(name, value);
	}

	protected RequestAttributes createBackupAttributes(HttpServletRequest request) {
		return new RequestAttributes(request, attribute -> {
			if (attribute.equals(RequestContext.REQUEST_ATTRIBUTE_ID) || attribute.equals(REQUEST_ATTRIBUTE_INCLUDE_PARAMS)) {
				return true;
			}
			return false;
		});
	}

	protected String buildContent(RequestContext childContext) throws Exception {
		HttpServletRequest request = getRequest();
		HttpServletResponse response = getResponse();

		RequestAttributes backupAttributes = createBackupAttributes(request);
		request.setAttribute(REQUEST_ATTRIBUTE_INCLUDE_PARAMS, this.params);
		request.setAttribute(RequestContext.REQUEST_ATTRIBUTE_ID, childContext);

		try {
			boolean useWrapper = ActionDescUtils.isActionResultOrVoid(childContext.getActionDesc().getMethod());
			if (useWrapper) {
				response = new ContentResponseWrapper(response, false, false);
			}

			ControllerProvider controllerProvider = ServletUtils.getAppScoped(this.pageContext.getServletContext(), ControllerProvider.class);
			ActionInvoker actionInvoker = ServletUtils.getAppScoped(this.pageContext.getServletContext(), ActionInvoker.class);

			Object childController = controllerProvider.getController(childContext.getActionDesc().getControllerClass());
			Object result = actionInvoker.invokeAction(request, response, childContext, childController);

			if (useWrapper) {
				ContentResponseWrapper wrapper = (ContentResponseWrapper) response;
				if (childContext.getActionDesc().getMethod().getReturnType() != void.class) {
					AssertUtils.assertNotNull(result);

					((ActionResult) result).execute(request, response, childContext);
				}
				wrapper.finishWrapper();
				return wrapper.getContent().toString(response.getCharacterEncoding());
			}
			return resultToString(result, response.getCharacterEncoding());

		} finally {
			backupAttributes.restore(request);
		}
	}

	@Override
	public void doTag() throws JspException, IOException {
		final RequestContext childContext = createChildRequestContext();
		try {
			String content = null;
			if (this.cacheName == null) {
				content = buildContent(childContext);

			} else {
				String responseKey = ResponseKeyBuilder.getResponseKey(getRequest(), this.cacheKey, false);

				AppCacheManager appCacheManager = ServletUtils.getAppScoped(this.pageContext.getServletContext(), AppCacheManager.class);
				AppCache<String, String> cache = appCacheManager.getRequiredCache(this.cacheName);

				content = cache.get(responseKey);
				if (content == null) {

					content = buildContent(childContext);
					cache.putIfAbsent(responseKey, content);
				}
			}
			this.pageContext.getOut().write(content);

		} catch (Exception ex) {
			handleException(ex);
		}
	}

	protected String resultToString(Object result, String characterEncoding) throws IOException {
		if (result instanceof String) {
			return (String) result;
		}
		if (result instanceof Reader) {
			try (Reader r = (Reader) result) {
				return IOUtils.toString(r);
			}
		}
		if (result instanceof InputStream) {
			try (InputStream is = (InputStream) result) {
				return IOUtils.toString(is, characterEncoding);
			}
		}
		if (result instanceof File) {
			return new String(Files.readAllBytes(((File) result).toPath()), characterEncoding);
		}
		if (result instanceof MemoryStream) {
			return ((MemoryStream) result).toString(characterEncoding);
		}
		return String.valueOf(result);
	}

	protected void handleException(Exception ex) throws JspException, IOException {
		if (this.errorKey != null) {
			this.pageContext.getOut().write(this.getRequestContext().escCt(this.errorKey));
		} else {
			if (ex instanceof RuntimeException) {
				throw (RuntimeException) ex;
			}
			if (ex instanceof JspException) {
				throw (JspException) ex;
			}
			if (ex instanceof IOException) {
				throw (IOException) ex;
			}
			throw new JspException(ex);
		}
	}

	protected RequestContext createChildRequestContext() {
		ActionDescProvider actionDescProvider = ServletUtils.getAppScoped(this.pageContext.getServletContext(), ActionDescProvider.class);
		ActionDesc actionDesc = actionDescProvider.getActionDesc(this.controller, this.action);
		if (actionDesc == null) {
			throw new IllegalArgumentException("Action is required (controller=" + this.controller + ", action=" + this.action + ")");
		}
		if (actionDesc.getChildAction() == null) {
			throw new IllegalArgumentException("Child action is required (controller=" + this.controller + ", action=" + this.action + ")");
		}
		return getRequestContext().createRequestContext(actionDesc);
	}

	@Attribute(required = true, rtexprvalue = false)
	public void setController(String controller) {
		this.controller = controller;
	}

	@Attribute(required = true, rtexprvalue = false)
	public void setAction(String action) {
		this.action = action;
	}

	@Attribute(required = false, rtexprvalue = false)
	public void setErrorKey(String errorKey) {
		this.errorKey = errorKey;
	}

	@Attribute(required = true, rtexprvalue = false)
	public void setCacheKey(String cacheKey) {
		this.cacheKey = cacheKey;
	}

	@Attribute(required = false, rtexprvalue = false)
	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}
}
