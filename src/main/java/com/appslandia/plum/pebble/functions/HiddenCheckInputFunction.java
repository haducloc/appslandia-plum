// The MIT License (MIT)
// Copyright © 2015 Loc Ha

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

import com.appslandia.common.utils.Arguments;
import com.appslandia.plum.pebble.DynPebbleFunction;
import com.appslandia.plum.pebble.TemplateEvaluationContext;
import com.appslandia.plum.utils.HtmlUtils;

import io.pebbletemplates.pebble.extension.escaper.SafeString;

/**
 *
 * @author Loc Ha
 *
 */
public abstract class HiddenCheckInputFunction extends DynPebbleFunction {

  @Override
  public String getDescription() {
    return "variables: path*, codeValue*, readonly*, converter";
  }

  protected abstract boolean isChecked(TemplateEvaluationContext context, String codeValue, String modelValue);

  @Override
  protected Object doExecute(TemplateEvaluationContext context, int lineNumber) throws IOException {
    String path = context.getArgReq("path");
    Object codeValue = context.getArgReq("codeValue");

    String converter = context.getArg("converter");
    boolean readonly = context.getBool("readonly");

    int nameIdx = path.indexOf('.');
    Arguments.isTrue(nameIdx > 0 && nameIdx < path.length() - 1, "path is invalid.");

    String name = path.substring(nameIdx + 1);
    Object value = context.evaluate(path);

    String codeVal = context.getRequestContext().format(codeValue, converter, false);
    String fmtValue = context.getRequestContext().format(value, converter, false);

    if (readonly && isChecked(context, codeVal, fmtValue)) {
      StringWriter out = new StringWriter(128);
      out.append("<input type=\"hidden\"");

      HtmlUtils.escAttribute(out, "name", name);
      HtmlUtils.escAttribute(out, "value", codeVal);
      out.append(" />");

      return new SafeString(out.toString());
    }
    return null;
  }
}
