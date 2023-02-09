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
import java.util.LinkedHashMap;
import java.util.Map;

import com.appslandia.common.utils.Asserts;
import com.appslandia.plum.base.ActionParser;
import com.appslandia.plum.utils.ServletUtils;
import com.appslandia.plum.utils.XmlEscaper;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.DynamicAttributes;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@Tag(name = "action")
public class ActionTag extends TagBase implements DynamicAttributes {

    protected String controller;
    protected String action;
    protected boolean absUrl;
    protected boolean escXml;

    protected Map<String, Object> _parameters;

    @Override
    public void setDynamicAttribute(String uri, String name, Object value) throws JspException {
	Asserts.isTrue(TagUtils.isForParameter(name));

	if (this._parameters == null) {
	    this._parameters = new LinkedHashMap<>();
	}
	this._parameters.put(TagUtils.getParameterName(name), value);
    }

    @Override
    public void doTag() throws JspException, IOException {
	if (this.controller == null) {
	    this.controller = getRequestContext().getActionDesc().getController();
	}
	ActionParser actionParser = ServletUtils.getAppScoped(this.pageContext.getServletContext(), ActionParser.class);
	String _url = actionParser.toActionUrl(this.getRequest(), this.controller, this.action, this._parameters, this.absUrl);
	_url = this.getResponse().encodeURL(_url);

	if (this.escXml) {
	    XmlEscaper.escapeXml(this.pageContext.getOut(), _url);
	} else {
	    this.pageContext.getOut().write(_url);
	}
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
    public void setAbsUrl(boolean absUrl) {
	this.absUrl = absUrl;
    }

    @Attribute(required = false, rtexprvalue = false)
    public void setEscXml(boolean escXml) {
	this.escXml = escXml;
    }
}
