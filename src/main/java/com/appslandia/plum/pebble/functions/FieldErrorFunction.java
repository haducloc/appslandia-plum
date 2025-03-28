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
import java.util.Objects;

import com.appslandia.common.utils.XmlEscaper;
import com.appslandia.plum.base.Message;
import com.appslandia.plum.pebble.DynPebbleFunction;
import com.appslandia.plum.pebble.TemplateEvaluationContext;
import com.appslandia.plum.utils.HtmlUtils;

import io.pebbletemplates.pebble.extension.escaper.SafeString;

/**
 *
 * @author Loc Ha
 *
 */
public class FieldErrorFunction extends DynPebbleFunction {

  @Override
  public String getDescription() {
    return "variables: fieldName*, form, class, errorClass";
  }

  @Override
  protected Object doExecute(TemplateEvaluationContext context, int lineNumber) throws IOException {
    String fieldName = context.getArgReq("fieldName");
    String form = context.getArg("form");
    String errorClass = context.getArg("errorClass", "l-field-error");

    boolean isValid = !Objects.equals(form, context.getModelState().getForm())
        || context.getModelState().isValid(fieldName);
    if (!isValid) {

      Message error = context.getModelState().getFieldErrors(fieldName).get(0);
      StringWriter out = new StringWriter(128);

      String clazz = context.getArg("class");
      clazz = (clazz == null) ? errorClass : (clazz + " " + errorClass);

      out.append("<div");
      HtmlUtils.escAttribute(out, "class", clazz);
      out.append(">");

      if (error.isEscXml()) {
        XmlEscaper.escapeXml(out, error.getText());
      } else {
        out.write(error.getText());
      }

      out.append("</div>");
      return new SafeString(out.toString());
    }
    return null;
  }
}
