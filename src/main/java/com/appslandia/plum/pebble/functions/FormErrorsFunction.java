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

package com.appslandia.plum.pebble.functions;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import com.appslandia.common.base.StringWriter;
import com.appslandia.common.utils.SplitUtils;
import com.appslandia.common.utils.XmlEscaper;
import com.appslandia.plum.base.Message;
import com.appslandia.plum.base.ModelState;
import com.appslandia.plum.base.SimpleCsrfManager;
import com.appslandia.plum.pebble.DynPebbleFunction;
import com.appslandia.plum.pebble.TemplateEvaluationContext;
import com.appslandia.plum.utils.HtmlUtils;

import io.pebbletemplates.pebble.extension.escaper.SafeString;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class FormErrorsFunction extends DynPebbleFunction {

    @Override
    public String getDescription() {
	return "variables: orderBy, fieldErrors, form";
    }

    @Override
    protected Object doExecute(TemplateEvaluationContext context, int lineNumber) throws IOException {
	String orderBy = context.getArgument("orderBy");
	boolean fieldErrors = context.getBool("fieldErrors", true);
	String form = context.getArgument("form");

	String boxClass = context.getArgument("boxClass");
	String msgClass = context.getArgument("msgClass");

	boolean hasErrors = false;
	if (fieldErrors) {
	    hasErrors = Objects.equals(form, context.getModelState().getForm()) && !context.getModelState().isValid();
	} else {
	    hasErrors = Objects.equals(form, context.getModelState().getForm())
		    && !(context.getModelState().isValid(ModelState.MODEL_FIELD) && context.getModelState().isValid(SimpleCsrfManager.PARAM_CSRF_ID));
	}

	if (hasErrors) {
	    StringWriter out = new StringWriter(context.getModelState().getErrors().size() * 128);

	    out.write("<ul");
	    if (boxClass != null)
		HtmlUtils.escAttribute(out, "class", boxClass);
	    out.write(">");

	    // Write field errors
	    if (fieldErrors) {

		List<Entry<String, List<Message>>> errors = new ArrayList<>(context.getModelState().getErrors().entrySet());
		if (orderBy != null) {
		    Collections.sort(errors, toFieldComparator(orderBy));
		}

		for (Entry<String, List<Message>> fieldError : errors) {
		    if (!fieldError.getKey().equals(ModelState.MODEL_FIELD) && !fieldError.getKey().equals(SimpleCsrfManager.PARAM_CSRF_ID)) {

			this.writeMessages(out, fieldError.getValue(), msgClass);
		    }
		}
	    }

	    // Model errors
	    List<Message> errors = context.getModelState().getErrors().get(ModelState.MODEL_FIELD);
	    if (errors != null) {
		this.writeMessages(out, errors, msgClass);
	    }

	    // CSRF
	    errors = context.getModelState().getErrors().get(SimpleCsrfManager.PARAM_CSRF_ID);
	    if (errors != null) {
		this.writeMessages(out, errors, msgClass);
	    }

	    out.write("</ul>");
	    return new SafeString(out.toString());
	}
	return null;
    }

    protected void writeMessages(Writer out, List<Message> messages, String msgClass) throws IOException {
	for (Message message : messages) {
	    out.write(System.lineSeparator());

	    out.write("<li");
	    if (msgClass == null) {
		HtmlUtils.escAttribute(out, "class", "l-field-error");
	    } else {
		out.write(" class=\"");
		out.write(msgClass);
		out.write(" l-field-error\"");
	    }
	    out.write(">");

	    if (message.isEscXml()) {
		XmlEscaper.escapeXml(out, message.getText());
	    } else {
		out.write(message.getText());
	    }
	    out.write("</li>");
	}
    }

    protected static Comparator<Map.Entry<String, List<Message>>> toFieldComparator(String orderBy) {
	final Map<String, Integer> posMap = new HashMap<>();
	int pos = 0;

	for (String fieldName : SplitUtils.splitByComma(orderBy)) {
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
}
