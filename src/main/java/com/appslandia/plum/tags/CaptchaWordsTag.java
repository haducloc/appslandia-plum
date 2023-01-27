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

import com.appslandia.plum.base.SimpleCaptchaManager;
import com.appslandia.plum.utils.HtmlUtils;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@Tag(name = "captchaWords")
public class CaptchaWordsTag extends UITagBase {

    protected String form;
    protected String maxlength;
    protected String placeholder;
    protected boolean required;

    @Override
    protected String getTagName() {
	return "input";
    }

    @Override
    protected void initTag() throws JspException, IOException {
	boolean isValid = !Objects.equals(this.form, getModelState().getForm()) || getModelState().isValid(SimpleCaptchaManager.PARAM_CAPTCHA_WORDS);
	if (!isValid) {

	    this.clazz = (this.clazz == null) ? "field-error" : this.clazz + " field-error";
	}
    }

    @Override
    protected void writeAttributes(JspWriter out) throws JspException, IOException {
	HtmlUtils.attribute(out, "id", SimpleCaptchaManager.PARAM_CAPTCHA_WORDS);
	HtmlUtils.attribute(out, "type", "text");
	HtmlUtils.attribute(out, "name", SimpleCaptchaManager.PARAM_CAPTCHA_WORDS);

	if (this.maxlength != null)
	    HtmlUtils.attribute(out, "maxlength", this.maxlength);
	if (this.placeholder != null)
	    HtmlUtils.escAttribute(out, "placeholder", this.placeholder);

	if (this.required)
	    HtmlUtils.required(out);
	HtmlUtils.attribute(out, "autocomplete", "off");

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
    }

    @Attribute(required = false, rtexprvalue = false)
    public void setForm(String form) {
	this.form = form;
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
    public void setRequired(boolean required) {
	this.required = required;
    }
}
