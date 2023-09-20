// The MIT License (MIT)
// Copyright © 2015 AppsLandia. All rights reserved.

// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software withhtml restriction, including withhtml limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:

// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.

// THE SOFTWARE IS PROVIDED "AS IS", WITHhtml WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// out OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

package com.appslandia.plum.pebble.functions;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import com.appslandia.common.models.SelectItem;
import com.appslandia.common.utils.StringUtils;
import com.appslandia.common.utils.XmlEscaper;
import com.appslandia.plum.pebble.DynPebbleFunction;
import com.appslandia.plum.pebble.TemplateEvaluationContext;
import com.appslandia.plum.utils.HtmlUtils;

import io.pebbletemplates.pebble.extension.escaper.SafeString;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class OptionsFunction extends DynPebbleFunction {

    @Override
    public String getDescription() {
	return "variables: selectedValue*, items*, converter, readonly";
    }

    @Override
    protected Object doExecute(TemplateEvaluationContext context, int lineNumber) throws IOException {
	Object value = context.getArgument("selectedValue");
	String converter = context.getArgument("converter");
	boolean readonly = context.getBool("readonly", false);
	List<SelectItem> items = context.getRequiredArgument("items");

	String fmtValue = context.getRequestContext().format(value, converter, false);
	StringWriter out = new StringWriter(items.size() * 80);

	// readonly
	if (readonly) {
	    SelectItem selItem = items.stream().filter(item -> {

		String codeValue = context.getRequestContext().format(item.getValue(), converter, false);
		return StringUtils.iequals(codeValue, fmtValue);

	    }).findFirst().orElse(null);

	    if (selItem != null) {
		String codeValue = context.getRequestContext().format(selItem.getValue(), converter, false);
		out.write(System.lineSeparator());

		out.write("<option");
		HtmlUtils.escAttribute(out, "value", codeValue);
		HtmlUtils.selected(out);
		out.write(">");

		if (selItem.getDisplayName() != null) {
		    XmlEscaper.escapeXml(out, selItem.getDisplayName());
		}
		out.write("</option>");
	    }

	} else {
	    // Not readonly
	    for (SelectItem item : items) {
		String codeValue = context.getRequestContext().format(item.getValue(), converter, false);
		out.write(System.lineSeparator());

		out.write("<option");
		HtmlUtils.escAttribute(out, "value", codeValue);

		if (StringUtils.iequals(codeValue, fmtValue)) {
		    HtmlUtils.selected(out);
		}
		out.write(">");

		if (item.getDisplayName() != null) {
		    XmlEscaper.escapeXml(out, item.getDisplayName());
		}
		out.write("</option>");
	    }
	}
	return new SafeString(out.toString());
    }
}
