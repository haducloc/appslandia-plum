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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.appslandia.common.utils.ObjectUtils;
import com.appslandia.plum.utils.HtmlUtils;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.DynamicAttributes;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public abstract class UITagBase extends TagBase implements DynamicAttributes {

    // Global attributes
    protected String id;
    protected boolean hidden;
    protected String datatag;

    protected String clazz;
    protected String style;
    protected String title;

    protected boolean render = true;
    protected Map<String, Object> dynamicAttributes;

    @Override
    public void doTag() throws JspException, IOException {
	this.initTag();
	if (!this.render) {
	    return;
	}
	this.writeTag(this.pageContext.getOut());
    }

    protected abstract void writeAttributes(JspWriter out) throws JspException, IOException;

    protected void writeDynamicAttributes(JspWriter out) throws JspException, IOException {
	for (Entry<String, Object> attr : this.dynamicAttributes.entrySet()) {
	    HtmlUtils.escAttribute(out, attr.getKey(), (attr.getValue() != null) ? attr.getValue().toString() : null);
	}
    }

    @Override
    public void setDynamicAttribute(String uri, String name, Object value) throws JspException {
	if (this.dynamicAttributes == null) {
	    this.dynamicAttributes = new HashMap<>();
	}
	this.dynamicAttributes.put(name, value);
    }

    abstract protected void initTag() throws JspException, IOException;

    protected abstract String getTagName();

    protected boolean hasBody() {
	return false;
    }

    protected void writeBody(JspWriter out) throws JspException, IOException {
    }

    protected void writeTag(JspWriter out) throws JspException, IOException {
	out.write('<');
	out.write(this.getTagName());

	this.writeAttributes(out);
	if (this.dynamicAttributes != null) {
	    this.writeDynamicAttributes(out);
	}
	if (this.hasBody()) {
	    out.write('>');
	    // out.newLine();
	    this.writeBody(out);
	    out.write("</");
	    out.write(this.getTagName());
	    out.write('>');

	} else {
	    out.write(" />");
	}
    }

    @Attribute(required = false, rtexprvalue = true)
    public void setId(String id) {
	this.id = id;
    }

    @Attribute(required = false, rtexprvalue = true)
    public void setHidden(boolean hidden) {
	this.hidden = hidden;
    }

    @Attribute(required = false, rtexprvalue = true)
    public void setDatatag(Object datatag) {
	this.datatag = ObjectUtils.toStringOrNull(datatag);
    }

    @Attribute(required = false, rtexprvalue = true)
    public void setClazz(String clazz) {
	this.clazz = clazz;
    }

    @Attribute(required = false, rtexprvalue = true)
    public void setStyle(String style) {
	this.style = style;
    }

    @Attribute(required = false, rtexprvalue = true)
    public void setTitle(String title) {
	this.title = title;
    }

    @Attribute(required = false, rtexprvalue = true)
    public void setRender(boolean render) {
	this.render = render;
    }
}
