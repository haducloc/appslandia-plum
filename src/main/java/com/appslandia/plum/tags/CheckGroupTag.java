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
import java.util.Objects;

import com.appslandia.plum.utils.HtmlUtils;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@Tag(name = "checkGroup", bodyContent = "scriptless")
public class CheckGroupTag extends UITagBase {

    protected boolean asDiv = true;
    protected String form;
    protected String field;

    @Override
    protected String getTagName() {
	return asDiv ? "div" : "ul";
    }

    @Override
    protected void initTag() throws JspException, IOException {
	boolean isInvalid = Objects.equals(this.form, getModelState().getForm()) && !getModelState().isValid(this.field);

	if (isInvalid) {
	    this.clazz = (this.clazz == null) ? "field-error" : this.clazz + " field-error";
	}
    }

    @Override
    protected void writeAttributes(JspWriter out) throws JspException, IOException {
	if (this.id != null)
	    HtmlUtils.attribute(out, "id", this.id);
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
	return this.jspBody != null;
    }

    @Override
    protected void writeBody(JspWriter out) throws JspException, IOException {
	this.jspBody.invoke(out);
    }

    @Attribute(required = false, rtexprvalue = false)
    public void setAsDiv(boolean asDiv) {
	this.asDiv = asDiv;
    }

    @Attribute(required = false, rtexprvalue = false)
    public void setForm(String form) {
	this.form = form;
    }

    @Attribute(required = true, rtexprvalue = false)
    public void setField(String field) {
	this.field = field;
    }
}
