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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

import com.appslandia.common.utils.MimeTypes;
import com.appslandia.plum.base.ActionParser;
import com.appslandia.plum.base.HttpMethod;
import com.appslandia.plum.utils.HtmlUtils;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@Tag(name = "form", bodyContent = "scriptless")
public class FormTag extends UITagBase {

    protected String name;
    protected String controller;
    protected String action;

    protected boolean actionType;
    protected boolean csrf;

    protected String method = HttpMethod.POST;
    protected String acceptCharset = StandardCharsets.UTF_8.name();
    protected String enctype = MimeTypes.APP_FORM_URLENCODED;

    protected boolean novalidate;
    protected String autocomplete;

    protected String _action;
    protected Map<String, Object> _parameters;

    @Override
    protected String getTagName() {
	return "form";
    }

    @Override
    public void setDynamicAttribute(String uri, String name, Object value) throws JspException {
	if (TagUtils.isForParameter(name)) {
	    if (this._parameters == null) {
		this._parameters = new LinkedHashMap<>();
	    }
	    this._parameters.put(TagUtils.getParameterName(name), value);
	} else {
	    super.setDynamicAttribute(uri, name, value);
	}
    }

    @Override
    protected void initTag() throws JspException, IOException {
	if (this.controller == null) {
	    this.controller = getRequestContext().getActionDesc().getController();
	}
	ActionParser actionParser = ServletUtils.getAppScoped(this.pageContext.getServletContext(), ActionParser.class);
	this._action = actionParser.toActionUrl(this.getRequest(), this.controller, this.action, this._parameters, false);
	this._action = this.getResponse().encodeURL(this._action);
    }

    @Override
    protected void writeAttributes(JspWriter out) throws JspException, IOException {
	if (this.id != null)
	    HtmlUtils.attribute(out, "id", this.id);
	if (this.name != null)
	    HtmlUtils.attribute(out, "name", this.name);
	HtmlUtils.escAttribute(out, "action", this._action);

	if (this.method != null)
	    HtmlUtils.attribute(out, "method", this.method);
	if (this.acceptCharset != null)
	    HtmlUtils.attribute(out, "accept-charset", this.acceptCharset);
	if (this.enctype != null)
	    HtmlUtils.attribute(out, "enctype", this.enctype);

	if (this.novalidate)
	    HtmlUtils.novalidate(out);
	if (this.autocomplete != null)
	    HtmlUtils.attribute(out, "autocomplete", this.autocomplete);

	if (this.hidden)
	    HtmlUtils.hidden(out);

	if (this.datatag != null)
	    HtmlUtils.escAttribute(out, "data-tag", this.datatag);
	if (this.clazz != null)
	    HtmlUtils.attribute(out, "class", this.clazz);
	if (this.style != null)
	    HtmlUtils.attribute(out, "style", this.style);
	if (this.title != null)
	    HtmlUtils.escAttribute(out, "title", this.title);
    }

    @Override
    protected boolean hasBody() {
	return true;
    }

    @Override
    protected void writeBody(JspWriter out) throws JspException, IOException {
	if (this.csrf) {
	    out.println("<input type=\"hidden\" id=\"csrfId\" name=\"csrfId\" value=\"\" />");
	}
	if (this.actionType) {
	    out.println("<input type=\"hidden\" id=\"actionType\" name=\"actionType\" value=\"\" />");
	}
	if (this.jspBody != null) {
	    this.jspBody.invoke(out);
	}
    }

    @Attribute(required = false, rtexprvalue = false)
    public void setName(String name) {
	this.name = name;
    }

    @Attribute(required = false, rtexprvalue = false)
    public void setController(String controller) {
	this.controller = controller;
    }

    @Attribute(required = true, rtexprvalue = false)
    public void setAction(String action) {
	this.action = action;
    }

    @Attribute(required = false, rtexprvalue = false)
    public void setCsrf(boolean csrf) {
	this.csrf = csrf;
    }

    @Attribute(required = false, rtexprvalue = false)
    public void setActionType(boolean actionType) {
	this.actionType = actionType;
    }

    @Attribute(required = false, rtexprvalue = false)
    public void setMethod(String method) {
	this.method = method;
    }

    @Attribute(required = false, rtexprvalue = false)
    public void setAcceptCharset(String acceptCharset) {
	this.acceptCharset = acceptCharset;
    }

    @Attribute(required = false, rtexprvalue = false)
    public void setEnctype(String enctype) {
	this.enctype = enctype;
    }

    @Attribute(required = false, rtexprvalue = false)
    public void setNovalidate(boolean novalidate) {
	this.novalidate = novalidate;
    }

    @Attribute(required = false, rtexprvalue = false)
    public void setAutocomplete(String autocomplete) {
	this.autocomplete = autocomplete;
    }
}
