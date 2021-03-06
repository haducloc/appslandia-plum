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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.JspTag;
import javax.servlet.jsp.tagext.SimpleTag;

import com.appslandia.plum.base.UserPrincipal;
import com.appslandia.plum.utils.ServletUtils;
import com.appslandia.plum.utils.XmlEscaper;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@Tag(name = "displayName", dynamicAttributes = false)
public class DisplayNameTag implements SimpleTag {

	protected PageContext pageContext;

	@Override
	public void doTag() throws JspException, IOException {
		UserPrincipal principal = ServletUtils.getRequiredPrincipal((HttpServletRequest) pageContext.getRequest());

		XmlEscaper.escapeXml(this.pageContext.getOut(), principal.getDisplayName());
	}

	@Override
	public void setParent(JspTag parent) {
	}

	@Override
	public JspTag getParent() {
		throw new UnsupportedOperationException();
	}

	public PageContext getPageContext() {
		return this.pageContext;
	}

	@Override
	public void setJspContext(JspContext pc) {
		this.pageContext = (PageContext) pc;
	}

	@Override
	public void setJspBody(JspFragment jspBody) {
	}
}
