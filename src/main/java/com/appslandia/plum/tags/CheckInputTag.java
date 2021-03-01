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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.appslandia.common.utils.AssertUtils;
import com.appslandia.plum.utils.HtmlUtils;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public abstract class CheckInputTag extends ValueTagBase {

	protected Object submitValue;
	protected boolean triggerSubmit = false;

	protected String autocomplete;

	@Override
	protected String getTagName() {
		return "input";
	}

	protected abstract String getType();

	protected abstract boolean isChecked();

	@Override
	protected void initTag() throws JspException, IOException {
		AssertUtils.assertNotNull(this.submitValue, "submitValue is required.");

		super.initTag();
	}

	@Override
	protected boolean writeHiddenTag() {
		return this.readonly && isChecked();
	}

	@Override
	protected void writeAttributes(JspWriter out) throws JspException, IOException {
		if (this.id != null)
			HtmlUtils.attribute(out, "id", this.id);
		HtmlUtils.attribute(out, "type", this.getType());
		HtmlUtils.attribute(out, "name", this.name);

		HtmlUtils.escAttribute(out, "value", getRequestContext().fmtVal(this.submitValue, this.fmt));
		if (this.isChecked())
			HtmlUtils.checked(out);

		if (this.readonly)
			HtmlUtils.disabled(out);
		if (this.required)
			HtmlUtils.required(out);

		if (this.autocomplete != null)
			HtmlUtils.attribute(out, "autocomplete", this.autocomplete);

		if (this.autofocus)
			HtmlUtils.autofocus(out);

		if (this.hidden)
			HtmlUtils.hidden(out);

		if (this.form != null)
			HtmlUtils.attribute(out, "form", this.form);

		if (this.triggerSubmit)
			HtmlUtils.attribute(out, "onchange", "this.form.submit();");

		if (this.datatag != null)
			HtmlUtils.escAttribute(out, "data-tag", this.datatag);
		if (this.clazz != null)
			HtmlUtils.attribute(out, "class", this.clazz);
		if (this.style != null)
			HtmlUtils.attribute(out, "style", this.style);
		if (this.title != null)
			HtmlUtils.escAttribute(out, "title", this.title);

		// OnEnter
		if (this.enterFn != null) {
			HtmlUtils.attribute(out, "onkeydown", String.format("return __on_enter(event, %s);", this.enterFn));

		} else if (this.enterBtn != null) {
			HtmlUtils.attribute(out, "onkeydown", String.format("return __click_btn_on_enter(event, '%s');", this.enterBtn));
		}
	}

	@Attribute(required = true, rtexprvalue = true)
	public void setSubmitValue(Object submitValue) {
		this.submitValue = submitValue;
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
