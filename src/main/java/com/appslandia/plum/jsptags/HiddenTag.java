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

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@Tag(name = "hidden")
public class HiddenTag extends ValueTagBase {

    @Override
    protected String getTagName() {
	return "input";
    }

    @Override
    protected boolean writeHiddenTag() {
	return false;
    }

    @Override
    protected String format(Object value, String converter) {
	return getRequestContext().fmt(value, converter, false);
    }

    @Override
    protected void writeAttributes(JspWriter out) throws JspException, IOException {
	if (this.id != null)
	    HtmlUtils.attribute(out, "id", this.id);
	HtmlUtils.attribute(out, "type", "hidden");
	HtmlUtils.attribute(out, "name", this.name);
	HtmlUtils.escAttribute(out, "value", format(this.value, this.converter));

	if (this.required)
	    HtmlUtils.required(out);

	if (this.form != null)
	    HtmlUtils.attribute(out, "form", this.form);

	if (this.datatag != null)
	    HtmlUtils.escAttribute(out, "data-tag", this.datatag);
    }

    @Override
    public void setHidden(boolean hidden) {
	throw new UnsupportedOperationException();
    }

    @Override
    public void setClazz(String clazz) {
	throw new UnsupportedOperationException();
    }

    @Override
    public void setStyle(String style) {
	throw new UnsupportedOperationException();
    }

    @Override
    public void setTitle(String title) {
	throw new UnsupportedOperationException();
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
