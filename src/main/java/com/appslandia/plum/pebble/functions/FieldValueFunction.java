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
import java.util.Objects;

import com.appslandia.common.utils.Asserts;
import com.appslandia.plum.jsp.TextBoxUtils;
import com.appslandia.plum.pebble.DynPebbleFunction;
import com.appslandia.plum.pebble.TemplateEvaluationContext;

import io.pebbletemplates.pebble.extension.escaper.SafeString;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class FieldValueFunction extends DynPebbleFunction {

    @Override
    public String getDescription() {
	return "variables: path*, type, converter, localize, form";
    }

    @Override
    protected Object doExecute(TemplateEvaluationContext context, int lineNumber) throws IOException {
	String path = context.getRequiredArgument("path");
	String type = context.getArgument("type");

	String converter = context.getArgument("converter");
	Boolean localize = context.getBoolObj("localize");
	String form = context.getArgument("form");

	int nameIdx = path.indexOf('.');
	Asserts.isTrue(nameIdx > 0 && nameIdx < path.length() - 1, "path is invalid.");
	String name = path.substring(nameIdx + 1);

	Object value = null;
	boolean isValid = !Objects.equals(form, context.getModelState().getForm()) || context.getModelState().isValid(name);

	if (isValid) {
	    value = context.getELProcessor().eval(path);
	} else {
	    value = context.getRequest().getParameter(name);
	}

	// Format value
	String fmtValue = null;
	if (localize != null) {
	    fmtValue = context.getRequestContext().fmt(value, converter, localize);

	} else {
	    if (type == null) {
		fmtValue = context.getRequestContext().fmt(value, converter, true);

	    } else if ("hidden".equals(type)) {
		fmtValue = context.getRequestContext().fmt(value, converter, false);

	    } else {
		Integer feature = TextBoxUtils.getBrowserFeature(type);
		Integer browserFeatures = context.getRequestContext().getBrowserFeatures();

		if (browserFeatures != null && feature != null && (browserFeatures & feature) == feature) {
		    fmtValue = context.getRequestContext().fmt(value, converter, false);

		} else {
		    fmtValue = context.getRequestContext().fmt(value, converter, true);
		}
	    }
	}
	return fmtValue != null ? new SafeString(fmtValue) : null;
    }
}
