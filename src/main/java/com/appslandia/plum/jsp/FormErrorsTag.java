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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import com.appslandia.common.utils.SplitUtils;
import com.appslandia.common.utils.StringUtils;
import com.appslandia.common.utils.XmlEscaper;
import com.appslandia.plum.base.Message;
import com.appslandia.plum.base.ModelState;
import com.appslandia.plum.base.SimpleCsrfManager;
import com.appslandia.plum.utils.HtmlUtils;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@Tag(name = "formErrors", bodyContent = "scriptless")
public class FormErrorsTag extends UITagBase {

    protected String form;
    protected boolean modelLevelOnly;

    protected String _fieldOrders;
    protected ModelState _modelState;

    @Override
    protected String getTagName() {
	return "ul";
    }

    public void setFieldOrders(String fieldOrders) {
	this._fieldOrders = fieldOrders;
    }

    @Override
    protected void initTag() throws JspException, IOException {
	this._modelState = getModelState();
	if (this.clazz == null) {
	    this.clazz = "messages";
	}
	this.clazz = this.clazz + " " + MessagesTag.getClassName(Message.TYPE_ERROR);

	boolean hasErrors = false;
	if (this.modelLevelOnly) {
	    hasErrors = Objects.equals(this.form, this._modelState.getForm()) && !this._modelState.isValid(ModelState.MODEL_FIELD);
	} else {
	    hasErrors = Objects.equals(this.form, this._modelState.getForm()) && !this._modelState.isValid();
	}
	if (hasErrors) {
	    // Parse field orders
	    if (!this.modelLevelOnly && (this.jspBody != null)) {
		this.jspBody.invoke(null);
	    }
	} else {
	    this.style = (this.style == null) ? "display:none" : (this.style + "display:none");
	}
    }

    @Override
    protected void writeAttributes(JspWriter out) throws JspException, IOException {
	if (this.id != null)
	    HtmlUtils.escAttribute(out, "id", this.id);
	if (this.hidden)
	    HtmlUtils.hidden(out);

	if (this.datatag != null)
	    HtmlUtils.escAttribute(out, "data-tag", this.datatag);
	if (this.clazz != null)
	    HtmlUtils.escAttribute(out, "class", this.clazz);
	if (this.style != null)
	    HtmlUtils.escAttribute(out, "style", this.style);
	if (this.title != null)
	    HtmlUtils.escAttribute(out, "title", this.title);
    }

    @Override
    protected boolean hasBody() {
	return true;
    }

    @Override
    protected void writeBody(JspWriter out) throws JspException, IOException {
	// Field errors
	if (!this.modelLevelOnly) {
	    if ((this._modelState.getErrors().size() > 1) && !StringUtils.isNullOrEmpty(this._fieldOrders)) {

		List<Entry<String, List<Message>>> entries = new ArrayList<>(this._modelState.getErrors().entrySet());
		Collections.sort(entries, buildFieldComparator(this._fieldOrders));

		for (Entry<String, List<Message>> entry : entries) {
		    if (!entry.getKey().equals(ModelState.MODEL_FIELD) && !entry.getKey().equals(SimpleCsrfManager.PARAM_CSRF_ID)) {
			this.writeMessages(out, entry.getValue());
		    }
		}
	    } else {
		for (Entry<String, List<Message>> entry : this._modelState.getErrors().entrySet()) {
		    if (!entry.getKey().equals(ModelState.MODEL_FIELD) && !entry.getKey().equals(SimpleCsrfManager.PARAM_CSRF_ID)) {
			this.writeMessages(out, entry.getValue());
		    }
		}
	    }
	}

	// Model errors
	List<Message> errors = this._modelState.getErrors().get(ModelState.MODEL_FIELD);
	if (errors != null) {
	    this.writeMessages(out, errors);
	}

	// CSRF
	errors = this._modelState.getErrors().get(SimpleCsrfManager.PARAM_CSRF_ID);
	if (errors != null) {
	    this.writeMessages(out, errors);
	}
    }

    protected void writeMessages(JspWriter out, List<Message> fieldErrors) throws IOException {
	for (Message error : fieldErrors) {
	    out.write("<li>");

	    if (error.isEscXml()) {
		XmlEscaper.escapeXmlContent(out, error.getText());
	    } else {
		out.write(error.getText());
	    }
	    out.write("</li>");
	}
    }

    protected static Comparator<Map.Entry<String, List<Message>>> buildFieldComparator(String fieldOrders) {
	final Map<String, Integer> posMap = new HashMap<>();
	int pos = 0;
	for (String fieldName : SplitUtils.split(fieldOrders, ',')) {
	    posMap.put(fieldName, ++pos);
	}
	return new Comparator<Map.Entry<String, List<Message>>>() {

	    @Override
	    public int compare(Entry<String, List<Message>> f1, Entry<String, List<Message>> f2) {
		Integer p1 = posMap.get(f1.getKey());
		Integer p2 = posMap.get(f2.getKey());
		if (p1 == null) {
		    p1 = Integer.MAX_VALUE;
		}
		if (p2 == null) {
		    p2 = Integer.MAX_VALUE;
		}
		return p1.compareTo(p2);
	    }
	};
    }

    @Attribute(required = false, rtexprvalue = false)
    public void setForm(String form) {
	this.form = form;
    }

    @Attribute(required = false, rtexprvalue = false)
    public void setModelLevelOnly(boolean modelLevelOnly) {
	this.modelLevelOnly = modelLevelOnly;
    }

    @Override
    public void setRender(boolean render) {
    }
}
