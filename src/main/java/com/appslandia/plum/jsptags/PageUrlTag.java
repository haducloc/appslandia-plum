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

package com.appslandia.plum.jsptags;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.appslandia.common.utils.Asserts;
import com.appslandia.plum.base.ActionParser;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.DynamicAttributes;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@Tag(name = "pageUrl", bodyContent = "empty")
public class PageUrlTag extends TagBase implements DynamicAttributes {

    protected Map<String, Object> _parameters;

    public static final String ATTRIBUTE_PAGE_URL = "currentPageUrl";

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
	String controller = getRequestContext().getActionDesc().getController();
	String action = getRequestContext().getActionDesc().getController();

	ActionParser actionParser = ServletUtils.getAppScoped(this.pageContext.getServletContext(), ActionParser.class);

	String _url = actionParser.toActionUrl(this.getRequest(), controller, action, this._parameters, false);
	_url = this.getResponse().encodeURL(_url);

	this.pageContext.setAttribute(ATTRIBUTE_PAGE_URL, _url);
    }
}
