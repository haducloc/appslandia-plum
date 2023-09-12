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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

import com.appslandia.common.models.SelectItem;
import com.appslandia.common.models.SelectItemImpl;
import com.appslandia.common.utils.XmlEscaper;
import com.appslandia.plum.utils.HtmlUtils;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@Tag(name = "select", bodyContent = "scriptless")
public class SelectTag extends ValueTagBase {

    protected Iterable<SelectItem> items;
    protected String var = "item";
    protected String size;
    protected boolean multiple;

    protected String disabledExpr;
    protected boolean triggerSubmit = false;

    protected List<SelectItem> _items;
    protected SelectItem _selItem;

    public SelectTag() {
	this.type = "select";
    }

    @Override
    protected String getTagName() {
	return "select";
    }

    @Override
    protected void initTag() throws JspException, IOException {
	super.initTag();

	if (this.body != null) {
	    this.body.invoke(null);
	}
    }

    @Override
    protected boolean writeHiddenTag() {
	return this.readonly && (this._selItem != null);
    }

    @Override
    protected Object getInvalidValue() {
	return this.evaluate(this.path);
    }

    @Override
    protected Object getHiddenValue() {
	return this._selItem.getValue();
    }

    public void addOption(String name, Object value) {
	if (this._items == null) {
	    this._items = new ArrayList<>();
	}
	this._items.add(new SelectItemImpl(value, name));
    }

    @Override
    protected void writeAttributes(JspWriter out) throws JspException, IOException {
	HtmlUtils.escAttribute(out, "id", this.id);
	HtmlUtils.escAttribute(out, "name", this._name);

	if (this.size != null)
	    HtmlUtils.escAttribute(out, "size", this.size);
	if (this.multiple)
	    HtmlUtils.multiple(out);

	if (this.autocomplete != null)
	    HtmlUtils.escAttribute(out, "autocomplete", this.autocomplete);

	if (this.readonly)
	    HtmlUtils.disabled(out);
	if (this.required)
	    HtmlUtils.required(out);

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

    @Override
    protected boolean hasBody() {
	return true;
    }

    protected void writeOption(JspWriter out, SelectItem item, boolean selected, boolean disabled) throws JspException, IOException {
	out.newLine();

	out.write("<option");
	HtmlUtils.escAttribute(out, "value", getRequestContext().format(item.getValue(), this.converter, this._localize));

	if (selected) {
	    HtmlUtils.selected(out);
	}
	if (disabled) {
	    HtmlUtils.disabled(out);
	}
	out.write('>');

	if (item.getDisplayName() != null) {
	    XmlEscaper.escapeXml(out, item.getDisplayName());
	}
	out.write("</option>");
    }

    protected SelectItem writeItems(JspWriter out, Iterable<SelectItem> items) throws JspException, IOException {
	final Object bakVar = this.pageContext.getAttribute(this.var);

	// READONLY
	if (this.readonly) {

	    SelectItem selItem = StreamSupport.stream(items.spliterator(), false).filter(item -> {
		String itemVal = getRequestContext().format(item.getValue(), this.converter, this._localize);
		return Objects.equals(itemVal, this._value);

	    }).findFirst().orElse(null);

	    if (selItem != null) {

		writeOption(out, selItem, true, false);
		return selItem;
	    }
	    return null;
	}

	// NOT READONLY
	try {
	    SelectItem selItem = null;
	    for (SelectItem item : items) {
		this.pageContext.setAttribute(this.var, item);

		String itemVal = getRequestContext().format(item.getValue(), this.converter, this._localize);
		boolean selected = Objects.equals(itemVal, this._value);

		if (selected) {
		    selItem = item;
		}
		boolean disabled = false;
		if (this.disabledExpr != null) {
		    disabled = Boolean.TRUE.equals(ExpressionEvaluator.getDefault().getValue(this.pageContext, this.disabledExpr));
		}
		writeOption(out, item, selected, disabled);
	    }
	    return selItem;

	} finally {
	    this.pageContext.setAttribute(this.var, bakVar);
	}
    }

    @Override
    protected void writeBody(JspWriter out) throws JspException, IOException {
	SelectItem selItem1 = null;
	if (this._items != null) {
	    selItem1 = writeItems(out, this._items);
	}

	SelectItem selItem2 = null;
	if (this.items != null) {
	    selItem2 = writeItems(out, this.items);
	}

	this._selItem = (selItem1 != null) ? selItem1 : selItem2;
	out.newLine();
    }

    @Attribute(required = false, rtexprvalue = true)
    public void setItems(Iterable<SelectItem> items) {
	this.items = items;
    }

    @Attribute(required = false, rtexprvalue = false)
    public void setVar(String var) {
	this.var = var;
    }

    @Attribute(required = false, rtexprvalue = false)
    public void setSize(String size) {
	this.size = size;
    }

    @Attribute(required = false, rtexprvalue = false)
    public void setMultiple(boolean multiple) {
	this.multiple = multiple;
    }

    @Attribute(required = false, rtexprvalue = false)
    public void setDisabledExpr(String disabledExpr) {
	this.disabledExpr = disabledExpr;
    }

    @Attribute(required = false, rtexprvalue = false)
    public void setTriggerSubmit(boolean triggerSubmit) {
	this.triggerSubmit = triggerSubmit;
    }
}
