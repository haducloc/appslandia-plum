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
import java.io.StringWriter;
import java.util.Objects;

import com.appslandia.common.utils.Arguments;
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
public class InputFunction extends DynPebbleFunction {

  @Override
  public String getDescription() {
    return "variables: path*, id, type, readonly, converter, form, min, max, step";
  }

  @Override
  protected Object doExecute(TemplateEvaluationContext context, int lineNumber) throws IOException {
    String path = context.getRequiredArgument("path");
    String type = context.getArgument("type");
    boolean readonly = context.getBool("readonly", false);

    String id = context.getArgument("id");
    String converter = context.getArgument("converter");
    String form = context.getArgument("form");

    Object min = context.getArgument("min");
    Object max = context.getArgument("max");
    Object step = context.getArgument("step");
    Arguments.isTrue(!"checkbox".equals(type) && !"radio".equals(type), "checkbox|radio type is unsupported.");

    int nameIdx = path.indexOf('.');
    Arguments.isTrue(nameIdx > 0 && nameIdx < path.length() - 1, "path is invalid.");
    String name = path.substring(nameIdx + 1);

    if (id == null) {
      id = HtmlUtils.toValueTagId(name);
    }
    if (type == null) {
      type = "text";
    }

    // value
    Object value = null;
    boolean isValid = !Objects.equals(form, context.getModelState().getForm()) || context.getModelState().isValid(name);

    if (isValid) {
      value = context.evaluate(path);
    } else {
      value = context.getRequest().getParameter(name);
    }

    // Write HTML
    StringWriter out = new StringWriter(160);

    out.write("id=\"");
    XmlEscaper.escapeXml(out, id);
    out.write("\"");

    HtmlUtils.escAttribute(out, "name", name);
    HtmlUtils.escAttribute(out, "value", context.getRequestContext().format(value, converter, false));

    if (type != null)
      HtmlUtils.escAttribute(out, "type", type);

    if (min != null)
      HtmlUtils.escAttribute(out, "min", context.getRequestContext().format(min, converter, false));

    if (max != null)
      HtmlUtils.escAttribute(out, "max", context.getRequestContext().format(max, converter, false));

    if (step != null)
      HtmlUtils.escAttribute(out, "step", context.getRequestContext().format(step, converter, false));

    if (readonly)
      HtmlUtils.readonly(out);

    return new SafeString(out.toString());
  }
}
