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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.appslandia.common.models.SelectItem;
import com.appslandia.common.models.SelectItemImpl;
import com.appslandia.common.utils.ObjectUtils;
import com.appslandia.plum.utils.HtmlUtils;
import com.appslandia.plum.utils.XmlEscaper;

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
	protected String autocomplete;

	protected String disabledExpr;
	protected boolean triggerSubmit = false;

	protected List<SelectItem> _items;
	protected boolean _hasSelected = false;

	@Override
	protected String getTagName() {
		return "select";
	}

	@Override
	protected void initTag() throws JspException, IOException {
		super.initTag();

		if (this.jspBody != null) {
			this.jspBody.invoke(null);
		}
	}

	@Override
	protected boolean writeHiddenTag() {
		return this.readonly && this._hasSelected;
	}

	// @Override
	public void addItem(String name, Object value) {
		if (this._items == null) {
			this._items = new ArrayList<>();
		}
		this._items.add(new SelectItemImpl(value, name));
	}

	@Override
	protected void writeAttributes(JspWriter out) throws JspException, IOException {
		if (this.id != null)
			HtmlUtils.attribute(out, "id", this.id);
		HtmlUtils.attribute(out, "name", this.name);

		if (this.value == null) {
			HtmlUtils.attribute(out, "unset", "unset");
		}

		if (this.size != null)
			HtmlUtils.attribute(out, "size", this.size);
		if (this.multiple)
			HtmlUtils.multiple(out);

		if (this.autocomplete != null)
			HtmlUtils.attribute(out, "autocomplete", this.autocomplete);

		if (this.readonly)
			HtmlUtils.disabled(out);
		if (this.required)
			HtmlUtils.required(out);

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

	@Override
	protected boolean hasBody() {
		return true;
	}

	protected void writeOption(JspWriter out, SelectItem item, boolean selected, boolean disabled) throws JspException, IOException {
		out.write("<option");
		HtmlUtils.escAttribute(out, "value", ObjectUtils.toStringOrNull(item.getValue()));

		if (selected) {
			HtmlUtils.selected(out);
		}
		if (disabled) {
			HtmlUtils.disabled(out);
		}
		out.write('>');

		if (item.getDisplayName() != null) {
			XmlEscaper.escapeXmlContent(out, item.getDisplayName());
		}
		out.write("</option>");
	}

	protected boolean writeItems(JspWriter out, Iterable<SelectItem> items) throws JspException, IOException {
		final Object bakVar = this.pageContext.getAttribute(this.var);

		// READONLY
		if (this.readonly) {
			SelectItem selected = StreamSupport.stream(items.spliterator(), false)
					.filter(item -> Objects.equals(getRequestContext().fmtVal(item.getValue(), this.fmt), this.value)).findFirst().orElse(null);

			if (selected != null) {
				writeOption(out, selected, true, false);
				return true;
			}
			return false;
		}

		// NOT READONLY
		try {
			boolean hasSelected = false;
			for (SelectItem item : items) {
				this.pageContext.setAttribute(this.var, item);

				boolean selected = Objects.equals(getRequestContext().fmtVal(item.getValue(), this.fmt), this.value);
				if (selected) {
					hasSelected = true;
				}

				boolean disabled = false;
				if (this.disabledExpr != null) {
					disabled = Boolean.TRUE.equals(ExpressionEvaluator.getDefault().getValue(this.pageContext, this.disabledExpr));
				}

				writeOption(out, item, selected, disabled);
			}
			return hasSelected;

		} finally {
			this.pageContext.setAttribute(this.var, bakVar);
		}
	}

	@Override
	protected void writeBody(JspWriter out) throws JspException, IOException {
		boolean hasSelected1 = false;
		if (this._items != null) {
			hasSelected1 = writeItems(out, this._items);
		}

		boolean hasSelected2 = false;
		if (this.items != null) {
			hasSelected2 = writeItems(out, this.items);
		}
		this._hasSelected = hasSelected1 || hasSelected2;
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
	public void setAutocomplete(String autocomplete) {
		this.autocomplete = autocomplete;
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
