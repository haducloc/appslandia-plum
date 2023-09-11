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

package com.appslandia.plum.jsp;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.ObjectUtils;
import com.appslandia.plum.base.ActionDesc;
import com.appslandia.plum.base.ActionDescProvider;
import com.appslandia.plum.base.ActionDescUtils;
import com.appslandia.plum.base.ActionInvoker;
import com.appslandia.plum.base.ActionResult;
import com.appslandia.plum.base.ContentResponseWrapper;
import com.appslandia.plum.base.ControllerProvider;
import com.appslandia.plum.base.RequestAttributes;
import com.appslandia.plum.base.RequestContext;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.DynamicAttributes;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@Tag(name = "include", dynamicAttributes = true)
public class IncludeTag extends TagBase implements DynamicAttributes {

    public static final String REQUEST_ATTRIBUTE_INCLUDE_PARAMS = "includeParams";

    protected String controller;
    protected String action;
    protected String errorKey;

    protected Map<String, Object> _params;

    @Override
    public void setDynamicAttribute(String uri, String name, Object value) throws JspException {
	if (this._params == null) {
	    this._params = new LinkedHashMap<>();
	}
	this._params.put(name, value);
    }

    protected RequestAttributes backupAttributes(HttpServletRequest request) {
	return new RequestAttributes(request);
    }

    protected String buildContent(RequestContext childContext) throws Exception {
	HttpServletRequest request = getRequest();
	HttpServletResponse response = getResponse();

	RequestAttributes backupAttributes = backupAttributes(request);
	request.setAttribute(REQUEST_ATTRIBUTE_INCLUDE_PARAMS, this._params);
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
		    Asserts.notNull(result);

		    ((ActionResult) result).execute(request, response, childContext);
		}
		wrapper.finishWrapper();
		return wrapper.getContent().toString(response.getCharacterEncoding());
	    }
	    return ObjectUtils.toStringOrEmpty(result);

	} finally {
	    request.removeAttribute(REQUEST_ATTRIBUTE_INCLUDE_PARAMS);
	    // request.removeAttribute(RequestContext.REQUEST_ATTRIBUTE_ID);

	    backupAttributes.restore(request);
	}
    }

    @Override
    public void doTag() throws JspException, IOException {
	final RequestContext childContext = createChildRequestContext();

	try {
	    String content = buildContent(childContext);
	    this.pageContext.getOut().write(content);

	} catch (Exception ex) {
	    handleException(ex);
	}
    }

    protected void handleException(Exception ex) throws JspException, IOException {
	if (this.errorKey != null) {
	    this.pageContext.getOut().write(this.getRequestContext().escXml(this.errorKey));
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
	Asserts.notNull(actionDesc);
	Asserts.notNull(actionDesc.getChildAction());

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
}
