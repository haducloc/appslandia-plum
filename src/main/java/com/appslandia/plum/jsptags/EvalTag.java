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

import com.appslandia.plum.utils.XmlEscaper;

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
@Tag(name = "eval", dynamicAttributes = false)
public class EvalTag implements SimpleTag {

    protected String path;
    protected boolean esc = true;

    protected PageContext pageContext;

    @Override
    public void doTag() throws JspException, IOException {
	Object value = ExpressionEvaluator.getDefault().getValue(this.pageContext, this.path);
	if (value == null) {
	    return;
	}
	if (this.esc) {
	    XmlEscaper.escapeXml(this.pageContext.getOut(), value.toString());
	} else {
	    this.pageContext.getOut().write(value.toString());
	}
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

    @Attribute(required = true, rtexprvalue = false)
    public void setPath(String path) {
	this.path = path;
    }

    @Attribute(required = false, rtexprvalue = false)
    public void setEsc(boolean esc) {
	this.esc = esc;
    }
}
