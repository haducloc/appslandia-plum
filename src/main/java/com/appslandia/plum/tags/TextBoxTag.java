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

import com.appslandia.plum.utils.HtmlUtils;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@Tag(name = "textBox")
public class TextBoxTag extends ValueTagBase {

	protected String type = "text";
	protected String maxlength;

	protected String placeholder;
	protected String hintKey;
	protected String autocomplete;

	@Override
	protected String getTagName() {
		return "input";
	}

	@Override
	protected boolean writeHiddenTag() {
		return false;
	}

	@Override
	protected void writeAttributes(JspWriter out) throws JspException, IOException {
		if (this.id != null)
			HtmlUtils.attribute(out, "id", this.id);
		HtmlUtils.attribute(out, "type", this.type);
		HtmlUtils.attribute(out, "name", this.name);
		HtmlUtils.escAttribute(out, "value", (String) this.value);

		if (this.autocomplete != null)
			HtmlUtils.attribute(out, "autocomplete", this.autocomplete);

		if (this.maxlength != null)
			HtmlUtils.attribute(out, "maxlength", this.maxlength);
		if (this.readonly)
			HtmlUtils.readonly(out);

		if (this.hintKey != null)
			HtmlUtils.escAttribute(out, "placeholder", this.getRequestContext().res(this.hintKey));

		else if (this.placeholder != null)
			HtmlUtils.escAttribute(out, "placeholder", this.placeholder);

		if (this.required)
			HtmlUtils.required(out);

		if (this.autofocus)
			HtmlUtils.autofocus(out);

		if (this.hidden)
			HtmlUtils.hidden(out);

		if (this.form != null)
			HtmlUtils.attribute(out, "form", this.form);

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

	@Attribute(required = false, rtexprvalue = false)
	public void setType(String type) {
		this.type = type;
	}

	@Attribute(required = false, rtexprvalue = false)
	public void setMaxlength(String maxlength) {
		this.maxlength = maxlength;
	}

	@Attribute(required = false, rtexprvalue = true)
	public void setPlaceholder(String placeholder) {
		this.placeholder = placeholder;
	}

	@Attribute(required = false, rtexprvalue = false)
	public void setHintKey(String hintKey) {
		this.hintKey = hintKey;
	}

	@Attribute(required = false, rtexprvalue = false)
	public void setAutocomplete(String autocomplete) {
		this.autocomplete = autocomplete;
	}
}
