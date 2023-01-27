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

import com.appslandia.common.utils.AssertUtils;
import com.appslandia.common.utils.StringUtils;
import com.appslandia.plum.utils.HtmlUtils;
import com.appslandia.plum.utils.XmlEscaper;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public abstract class ValueTagBase extends UITagBase {

    protected String form;
    protected String name;
    protected Object value;
    protected String path;

    protected String converter;
    protected boolean required;
    protected boolean autofocus;
    protected boolean readonly;

    protected String enterFn;
    protected String enterBtn;

    protected Object getInvalidValue() {
	return StringUtils.trimToNull(this.getRequest().getParameter(this.name));
    }

    protected boolean cssFieldError() {
	return true;
    }

    @Override
    protected void initTag() throws JspException, IOException {
	boolean isValid = false;

	// Path Expression?
	if (this.path != null) {
	    int nameIdx = this.path.indexOf('.');
	    AssertUtils.assertTrue(nameIdx > 0);

	    this.name = this.path.substring(nameIdx + 1);

	    isValid = !Objects.equals(this.form, getModelState().getForm()) || getModelState().isValid(this.name);
	    if (!isValid) {
		this.value = getInvalidValue();
	    } else {
		this.value = ExpressionEvaluator.getDefault().getValue(this.pageContext, this.path);
	    }
	} else {
	    // name/value
	    AssertUtils.assertNotNull(this.name, "name is required.");

	    isValid = !Objects.equals(this.form, getModelState().getForm()) || getModelState().isValid(this.name);
	    if (!isValid) {
		this.value = getInvalidValue();
	    }
	}
	if (this.id == null) {
	    this.id = HtmlUtils.buildId(this.name);
	}

	if (!isValid && cssFieldError()) {
	    this.clazz = (this.clazz == null) ? "field-error" : this.clazz + " field-error";
	}
    }

    protected abstract String format(Object value, String converter);

    protected abstract boolean writeHiddenTag();

    @Override
    protected void writeTag(JspWriter out) throws JspException, IOException {
	super.writeTag(out);

	if (writeHiddenTag()) {
	    out.write(" <input name=\"");
	    out.write(this.name);

	    out.write("\" value=\"");
	    if (this.value != null) {
		XmlEscaper.escapeXml(out, format(this.value, this.converter));
	    }
	    out.write("\" type=\"hidden\" />");
	}
    }

    @Attribute(required = false, rtexprvalue = false)
    public void setForm(String form) {
	this.form = form;
    }

    @Attribute(required = false, rtexprvalue = false)
    public void setName(String name) {
	this.name = name;
    }

    @Attribute(required = false, rtexprvalue = true)
    public void setValue(Object value) {
	this.value = value;
    }

    @Attribute(required = false, rtexprvalue = false)
    public void setPath(String path) {
	this.path = path;
    }

    @Attribute(required = false, rtexprvalue = false)
    public void setConverter(String converter) {
	this.converter = converter;
    }

    @Attribute(required = false, rtexprvalue = true)
    public void setRequired(boolean required) {
	this.required = required;
    }

    @Attribute(required = false, rtexprvalue = true)
    public void setAutofocus(boolean autofocus) {
	this.autofocus = autofocus;
    }

    @Attribute(required = false, rtexprvalue = true)
    public void setReadonly(boolean readonly) {
	this.readonly = readonly;
    }

    @Attribute(required = false, rtexprvalue = false)
    public void setEnterFn(String enterFn) {
	this.enterFn = enterFn;
    }

    @Attribute(required = false, rtexprvalue = false)
    public void setEnterBtn(String enterBtn) {
	this.enterBtn = enterBtn;
    }
}
