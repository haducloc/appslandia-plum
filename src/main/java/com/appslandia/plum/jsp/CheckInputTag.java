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

import com.appslandia.common.utils.Asserts;
import com.appslandia.plum.utils.HtmlUtils;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public abstract class CheckInputTag extends ValueTagBase {

    protected Object codeValue;
    protected boolean triggerSubmit = false;

    protected String autocomplete;

    @Override
    protected String getTagName() {
	return "input";
    }

    protected abstract boolean isChecked();

    @Override
    protected void initTag() throws JspException, IOException {
	Asserts.notNull(this.codeValue, "codeValue is required.");

	super.initTag();
    }

    @Override
    protected boolean writeHiddenTag() {
	return this.readonly && isChecked();
    }

    @Override
    protected Object getInvalidValue() {
	return this.evaluate(this.path);
    }

    @Override
    protected Object getHiddenValue() {
	return this.codeValue;
    }

    @Override
    protected void writeAttributes(JspWriter out) throws JspException, IOException {
	HtmlUtils.escAttribute(out, "id", this.id);
	HtmlUtils.escAttribute(out, "type", this.type);
	HtmlUtils.escAttribute(out, "name", this._name);
	HtmlUtils.escAttribute(out, "value", getRequestContext().format(this.codeValue, this.converter, this._localize));

	if (this.isChecked())
	    HtmlUtils.checked(out);

	if (this.readonly)
	    HtmlUtils.disabled(out);
	if (this.required)
	    HtmlUtils.required(out);

	if (this.autocomplete != null)
	    HtmlUtils.escAttribute(out, "autocomplete", this.autocomplete);

	if (this.autofocus)
	    HtmlUtils.autofocus(out);

	if (this.hidden)
	    HtmlUtils.hidden(out);

	if (this.form != null)
	    HtmlUtils.escAttribute(out, "form", this.form);

	if (this.triggerSubmit)
	    HtmlUtils.escAttribute(out, "onchange", "this.form.submit();");

	if (this.datatag != null)
	    HtmlUtils.escAttribute(out, "data-tag", this.datatag);
	if (this.clazz != null)
	    HtmlUtils.escAttribute(out, "class", this.clazz);
	if (this.style != null)
	    HtmlUtils.escAttribute(out, "style", this.style);
	if (this.title != null)
	    HtmlUtils.escAttribute(out, "title", this.title);

	// OnEnter
	if (this.enterFn != null) {
	    HtmlUtils.escAttribute(out, "onkeyup", String.format("return __on_enter(event, %s);", this.enterFn));

	} else if (this.enterBtn != null) {
	    HtmlUtils.escAttribute(out, "onkeyup", String.format("return __click_btn_on_enter(event, '%s');", this.enterBtn));
	}
    }

    @Attribute(required = true, rtexprvalue = true)
    public void setCodeValue(Object codeValue) {
	this.codeValue = codeValue;
    }

    @Attribute(required = false, rtexprvalue = false)
    public void setTriggerSubmit(boolean triggerSubmit) {
	this.triggerSubmit = triggerSubmit;
    }

    @Attribute(required = false, rtexprvalue = false)
    public void setAutocomplete(String autocomplete) {
	this.autocomplete = autocomplete;
    }
}
