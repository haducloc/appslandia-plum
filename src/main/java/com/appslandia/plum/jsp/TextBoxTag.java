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

import com.appslandia.plum.base.BrowserFeatures;
import com.appslandia.plum.utils.HtmlUtils;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@Tag(name = "textBox")
public class TextBoxTag extends ValueTagBase {

    protected String type = "text";
    protected String maxlength;

    protected Object min;
    protected Object max;
    protected Object step;
    protected String pattern;

    protected String placeholder;
    protected String alt;
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
    protected String format(Object value, String converter) {
	if (value == null) {
	    return null;
	}
	if (value.getClass() == String.class) {
	    return (String) value;
	}

	// hidden
	if ("hidden".equals(this.type)) {
	    return getRequestContext().fmt(value, converter, false);
	}

	// text
	if ("text".equals(this.type)) {
	    return getRequestContext().fmt(value, converter, true);
	}

	// number
	if ("number".equals(this.type)) {
	    if (getRequestContext().getBrowserFeatures() == null) {
		return getRequestContext().fmt(value, converter, false);
	    } else {
		return getRequestContext().fmt(value, converter, (getRequestContext().getBrowserFeatures() & BrowserFeatures.INPUT_NUMBER) != BrowserFeatures.INPUT_NUMBER);
	    }
	}

	// range
	if ("range".equals(this.type)) {
	    if (getRequestContext().getBrowserFeatures() == null) {
		return getRequestContext().fmt(value, converter, false);
	    } else {
		return getRequestContext().fmt(value, converter, (getRequestContext().getBrowserFeatures() & BrowserFeatures.INPUT_RANGE) != BrowserFeatures.INPUT_RANGE);
	    }
	}

	// date
	if ("date".equals(this.type)) {
	    if (getRequestContext().getBrowserFeatures() == null) {
		return getRequestContext().fmt(value, converter, false);
	    } else {
		return getRequestContext().fmt(value, converter, (getRequestContext().getBrowserFeatures() & BrowserFeatures.INPUT_DATE) != BrowserFeatures.INPUT_DATE);
	    }
	}

	// time
	if ("time".equals(this.type)) {
	    if (getRequestContext().getBrowserFeatures() == null) {
		return getRequestContext().fmt(value, converter, false);
	    } else {
		return getRequestContext().fmt(value, converter, (getRequestContext().getBrowserFeatures() & BrowserFeatures.INPUT_TIME) != BrowserFeatures.INPUT_TIME);
	    }
	}

	// datetime-local
	if ("datetime-local".equals(this.type)) {
	    if (getRequestContext().getBrowserFeatures() == null) {
		return getRequestContext().fmt(value, converter, false);
	    } else {
		return getRequestContext().fmt(value, converter,
			(getRequestContext().getBrowserFeatures() & BrowserFeatures.INPUT_DATETIME_LOCAL) != BrowserFeatures.INPUT_DATETIME_LOCAL);
	    }
	}

	// month
	if ("month".equals(this.type)) {
	    if (getRequestContext().getBrowserFeatures() == null) {
		return getRequestContext().fmt(value, converter, false);
	    } else {
		return getRequestContext().fmt(value, converter, (getRequestContext().getBrowserFeatures() & BrowserFeatures.INPUT_MONTH) != BrowserFeatures.INPUT_MONTH);
	    }
	}

	// week
	if ("week".equals(this.type)) {
	    if (getRequestContext().getBrowserFeatures() == null) {
		return getRequestContext().fmt(value, converter, false);
	    } else {
		return getRequestContext().fmt(value, converter, (getRequestContext().getBrowserFeatures() & BrowserFeatures.INPUT_WEEK) != BrowserFeatures.INPUT_WEEK);
	    }
	}

	// Others: localize = true
	return getRequestContext().fmt(value, converter, true);
    }

    @Override
    protected void writeAttributes(JspWriter out) throws JspException, IOException {
	if (this.id != null)
	    HtmlUtils.attribute(out, "id", this.id);
	HtmlUtils.attribute(out, "type", this.type);
	HtmlUtils.attribute(out, "name", this.name);

	HtmlUtils.escAttribute(out, "value", format(this.value, this.converter));
	if (this.min != null)
	    HtmlUtils.escAttribute(out, "min", format(this.min, this.converter));

	if (this.max != null)
	    HtmlUtils.escAttribute(out, "max", format(this.max, this.converter));

	if (this.step != null)
	    HtmlUtils.escAttribute(out, "step", format(this.step, this.converter));

	if (this.pattern != null)
	    HtmlUtils.escAttribute(out, "pattern", this.pattern);

	if (this.autocomplete != null)
	    HtmlUtils.attribute(out, "autocomplete", this.autocomplete);

	if (this.maxlength != null)
	    HtmlUtils.attribute(out, "maxlength", this.maxlength);
	if (this.readonly)
	    HtmlUtils.readonly(out);

	if (this.placeholder != null)
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

	if (this.alt != null)
	    HtmlUtils.escAttribute(out, "alt", this.alt);

	// OnEnter
	if (this.enterFn != null) {
	    HtmlUtils.attribute(out, "onkeyup", String.format("return __on_enter(event, %s);", this.enterFn));

	} else if (this.enterBtn != null) {
	    HtmlUtils.attribute(out, "onkeyup", String.format("return __click_btn_on_enter(event, '%s');", this.enterBtn));
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
    public void setMin(Object min) {
	this.min = min;
    }

    @Attribute(required = false, rtexprvalue = true)
    public void setMax(Object max) {
	this.max = max;
    }

    @Attribute(required = false, rtexprvalue = true)
    public void setStep(Object step) {
	this.step = step;
    }

    @Attribute(required = false, rtexprvalue = false)
    public void setPattern(String pattern) {
	this.pattern = pattern;
    }

    @Attribute(required = false, rtexprvalue = true)
    public void setPlaceholder(String placeholder) {
	this.placeholder = placeholder;
    }

    @Attribute(required = false, rtexprvalue = true)
    public void setAlt(String alt) {
	this.alt = alt;
    }

    @Attribute(required = false, rtexprvalue = false)
    public void setAutocomplete(String autocomplete) {
	this.autocomplete = autocomplete;
    }
}