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

import com.appslandia.common.base.StringWriter;
import com.appslandia.common.utils.StringUtils;
import com.appslandia.plum.base.ModelState;
import com.appslandia.plum.base.RequestContext;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspContext;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.JspFragment;
import jakarta.servlet.jsp.tagext.JspTag;
import jakarta.servlet.jsp.tagext.SimpleTag;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public abstract class TagBase implements SimpleTag {

    protected JspTag parentTag;
    protected PageContext pageContext;
    protected JspFragment jspBody;

    protected String evalJspBody() throws JspException, IOException {
	if (this.jspBody != null) {
	    StringWriter out = new StringWriter();
	    this.jspBody.invoke(out);
	    return out.toString();
	}
	return StringUtils.EMPTY_STRING;
    }

    public PageContext getPageContext() {
	return this.pageContext;
    }

    public HttpServletRequest getRequest() {
	return (HttpServletRequest) this.pageContext.getRequest();
    }

    public HttpServletResponse getResponse() {
	return (HttpServletResponse) this.pageContext.getResponse();
    }

    public RequestContext getRequestContext() {
	return ServletUtils.getRequestContext((HttpServletRequest) this.pageContext.getRequest());
    }

    public ModelState getModelState() {
	return ServletUtils.getModelState((HttpServletRequest) this.pageContext.getRequest());
    }

    @Override
    public void setParent(JspTag parent) {
	this.parentTag = parent;
    }

    @Override
    public JspTag getParent() {
	return this.parentTag;
    }

    @Override
    public void setJspContext(JspContext pc) {
	this.pageContext = (PageContext) pc;
    }

    @Override
    public void setJspBody(JspFragment jspBody) {
	this.jspBody = jspBody;
    }
}
