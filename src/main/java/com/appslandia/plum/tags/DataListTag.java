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

import com.appslandia.plum.utils.HtmlUtils;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@Tag(name = "dataList", bodyContent = "scriptless", dynamicAttributes = false)
public class DataListTag extends UITagBase {

    protected Iterable<Object> items;
    protected List<Object> _items;

    @Override
    protected String getTagName() {
	return "datalist";
    }

    @Override
    protected void initTag() throws JspException, IOException {
	if (this.jspBody != null) {
	    this.jspBody.invoke(null);
	}
    }

    // @Override
    public void addItem(Object value) {
	if (this._items == null) {
	    this._items = new ArrayList<>();
	}
	this._items.add(value);
    }

    @Override
    protected void writeAttributes(JspWriter out) throws JspException, IOException {
	HtmlUtils.attribute(out, "id", this.id);
	if (this.datatag != null)
	    HtmlUtils.escAttribute(out, "data-tag", this.datatag);
    }

    @Override
    protected boolean hasBody() {
	return true;
    }

    protected void writeItems(JspWriter out, Iterable<Object> items) throws JspException, IOException {
	for (Object item : items) {
	    if (item == null) {
		continue;
	    }
	    out.write("<option");
	    HtmlUtils.escAttribute(out, "value", item.toString());
	    out.write("/>");
	}
    }

    @Override
    protected void writeBody(JspWriter out) throws JspException, IOException {
	if (this._items != null) {
	    writeItems(out, this._items);
	}
	if (this.items != null) {
	    writeItems(out, this.items);
	}
    }

    @Attribute(required = true, rtexprvalue = false)
    public void setId(String id) {
	this.id = id;
    }

    @Attribute(required = true, rtexprvalue = true)
    public void setItems(Iterable<Object> items) {
	this.items = items;
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
}
