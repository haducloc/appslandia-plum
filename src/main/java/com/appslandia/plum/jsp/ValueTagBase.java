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
import java.util.Objects;

import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.XmlEscaper;
import com.appslandia.plum.utils.HtmlUtils;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public abstract class ValueTagBase extends UITagBase {

    protected String type;
    protected String form;
    protected String path;

    protected String converter;
    protected boolean required;
    protected boolean autofocus;
    protected boolean readonly;
    protected String autocomplete;

    protected String enterFn;
    protected String enterBtn;

    protected String _name;
    protected Object _value;
    protected boolean _isValid;
    protected boolean _localize;

    protected boolean writeHiddenTag() {
	return false;
    }

    protected Object getHiddenValue() {
	return this._value;
    }

    protected Object getBindingValue() {
	if (this._isValid || InputUtils.getDTNInputFeature(this.type) != null) {
	    return this.evaluate(this.path);
	}
	return this.getRequest().getParameter(this._name);
    }

    @Override
    protected void initTag() throws JspException, IOException {
	int nameIdx = this.path.indexOf('.');
	Asserts.isTrue(nameIdx > 0 && nameIdx < this.path.length() - 1, "path is invalid.");
	this._name = this.path.substring(nameIdx + 1);

	// localize
	this._localize = InputUtils.getLocalize(this.getRequest(), this.type);

	if ((this.type == null) || (this._localize && InputUtils.getDTNInputFeature(this.type) != null)) {
	    this.type = "text";
	}

	// value
	this._isValid = !Objects.equals(this.form, this.getModelState().getForm()) || this.getModelState().isValid(this._name);
	this._value = getBindingValue();

	// Format value
	this._value = getRequestContext().format(this._value, this.converter, this._localize);

	// id
	if (this.id == null)
	    this.id = HtmlUtils.toValueTagId(this._name);

	// class
	if (!this._isValid) {
	    this.clazz = (this.clazz == null) ? "l-error-field" : this.clazz + " l-error-field";
	}
    }

    @Override
    protected void writeTag(JspWriter out) throws JspException, IOException {
	super.writeTag(out);

	// Write hidden?
	if (writeHiddenTag()) {
	    out.newLine();

	    out.write("<input name=\"");
	    XmlEscaper.escapeXml(out, this._name);

	    out.write("\" value=\"");
	    Object hiddenValue = getHiddenValue();

	    if (hiddenValue != null)
		XmlEscaper.escapeXml(out, getRequestContext().format(hiddenValue, this.converter, this._localize));

	    out.write("\" type=\"hidden\" />");
	}
    }

    @Attribute(required = false, rtexprvalue = true)
    public void setId(String id) {
	this.id = id;
    }

    @Attribute(required = false, rtexprvalue = false)
    public void setForm(String form) {
	this.form = form;
    }

    @Attribute(required = true, rtexprvalue = true)
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
    public void setAutocomplete(String autocomplete) {
	this.autocomplete = autocomplete;
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
