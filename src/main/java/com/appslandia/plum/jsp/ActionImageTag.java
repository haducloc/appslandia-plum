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

import com.appslandia.common.utils.StringUtils;
import com.appslandia.plum.base.ActionParser;
import com.appslandia.plum.utils.HtmlUtils;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@Tag(name = "actionImage")
public class ActionImageTag extends UITagBase {

    protected String controller;
    protected String action;

    protected String width;
    protected String height;
    protected String alt = StringUtils.EMPTY_STRING;

    protected String _src;
    protected Map<String, Object> _parameters;

    @Override
    protected String getTagName() {
	return "img";
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
	this._src = actionParser.toActionUrl(this.getRequest(), this.controller, this.action, this._parameters, false);
	this._src = this.getResponse().encodeURL(this._src);
    }

    @Override
    protected void writeAttributes(JspWriter out) throws JspException, IOException {
	if (this.id != null)
	    HtmlUtils.escAttribute(out, "id", this.id);
	HtmlUtils.escAttribute(out, "src", this._src);

	if (this.width != null)
	    HtmlUtils.escAttribute(out, "width", this.width);
	if (this.height != null)
	    HtmlUtils.escAttribute(out, "height", this.height);

	HtmlUtils.escAttribute(out, "alt", this.alt);
	if (this.hidden)
	    HtmlUtils.hidden(out);

	if (this.datatag != null)
	    HtmlUtils.escAttribute(out, "data-tag", this.datatag);
	if (this.clazz != null)
	    HtmlUtils.escAttribute(out, "class", this.clazz);
	if (this.style != null)
	    HtmlUtils.escAttribute(out, "style", this.style);
	if (this.title != null)
	    HtmlUtils.escAttribute(out, "title", this.title);
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
    public void setWidth(String width) {
	this.width = width;
    }

    @Attribute(required = false, rtexprvalue = false)
    public void setHeight(String height) {
	this.height = height;
    }

    @Attribute(required = false, rtexprvalue = true)
    public void setAlt(String alt) {
	this.alt = alt;
    }
}
