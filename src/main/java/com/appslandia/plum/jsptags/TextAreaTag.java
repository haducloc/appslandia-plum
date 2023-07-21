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

import com.appslandia.plum.utils.HtmlUtils;
import com.appslandia.plum.utils.XmlEscaper;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@Tag(name = "textArea")
public class TextAreaTag extends ValueTagBase {

    protected String maxlength;
    protected String placeholder;
    protected String autocomplete;

    protected String cols;
    protected String rows;
    protected boolean hardWrap;

    @Override
    protected String getTagName() {
	return "textarea";
    }

    @Override
    protected boolean hasBody() {
	return true;
    }

    @Override
    protected String format(Object value, String converter) {
	return getRequestContext().fmt(value, converter, true);
    }

    @Override
    protected boolean writeHiddenTag() {
	return false;
    }

    @Override
    protected void writeAttributes(JspWriter out) throws JspException, IOException {
	if (this.id != null)
	    HtmlUtils.attribute(out, "id", this.id);
	HtmlUtils.attribute(out, "name", this.name);

	if (this.maxlength != null)
	    HtmlUtils.attribute(out, "maxlength", this.maxlength);
	if (this.readonly)
	    HtmlUtils.readonly(out);
	if (this.placeholder != null)
	    HtmlUtils.escAttribute(out, "placeholder", this.placeholder);
	if (this.autocomplete != null)
	    HtmlUtils.attribute(out, "autocomplete", this.autocomplete);

	if (this.rows != null)
	    HtmlUtils.attribute(out, "rows", this.rows);
	if (this.cols != null)
	    HtmlUtils.attribute(out, "cols", this.cols);
	if (this.hardWrap)
	    HtmlUtils.hardWrap(out);

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
    }

    @Override
    protected void writeBody(JspWriter out) throws JspException, IOException {
	if (this.value != null) {
	    XmlEscaper.escapeXmlContent(out, format(this.value, this.converter));
	}
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
    public void setAutocomplete(String autocomplete) {
	this.autocomplete = autocomplete;
    }

    @Attribute(required = false, rtexprvalue = false)
    public void setRows(String rows) {
	this.rows = rows;
    }

    @Attribute(required = false, rtexprvalue = false)
    public void setCols(String cols) {
	this.cols = cols;
    }

    @Attribute(required = false, rtexprvalue = false)
    public void setHardWrap(boolean hardWrap) {
	this.hardWrap = hardWrap;
    }

    @Override
    public void setEnterFn(String enterFn) {
	throw new UnsupportedOperationException();
    }

    @Override
    public void setEnterBtn(String enterBtn) {
	throw new UnsupportedOperationException();
    }
}
