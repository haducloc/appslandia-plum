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
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;

import com.appslandia.common.base.StringWriter;
import com.appslandia.common.utils.XmlEscaper;
import com.appslandia.plum.base.Message;
import com.appslandia.plum.base.ModelState;
import com.appslandia.plum.base.SimpleCsrfManager;
import com.appslandia.plum.jsp.FormErrorsTag;
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
    return "variables: includeFields, fieldOrders, form, listClass, itemClass";
  }

  @Override
  protected Object doExecute(TemplateEvaluationContext context, int lineNumber) throws IOException {
    String fieldOrders = context.getArgument("fieldOrders");
    boolean includeFields = context.getBool("includeFields", true);
    String form = context.getArgument("form");

    String listClass = context.getArgument("listClass");
    String itemClass = context.getArgument("itemClass");

    boolean hasErrors = false;
    if (includeFields) {
      hasErrors = Objects.equals(form, context.getModelState().getForm()) && !context.getModelState().isValid();
    } else {
      hasErrors = Objects.equals(form, context.getModelState().getForm())
          && !(context.getModelState().isValid(ModelState.MODEL_FIELD)
              && context.getModelState().isValid(SimpleCsrfManager.PARAM_CSRF_ID));
    }

    if (hasErrors) {
      StringWriter out = new StringWriter(context.getModelState().getErrors().size() * 128);

      out.write("<ul");
      if (listClass != null)
        HtmlUtils.escAttribute(out, "class", listClass);
      out.write(">");

      // Write field errors
      if (includeFields) {

        List<Entry<String, List<Message>>> errors = new ArrayList<>(context.getModelState().getErrors().entrySet());
        if (fieldOrders != null) {
          Collections.sort(errors, FormErrorsTag.toFieldComparator(fieldOrders));
        }

        for (Entry<String, List<Message>> fieldError : errors) {
          if (!fieldError.getKey().equals(ModelState.MODEL_FIELD)
              && !fieldError.getKey().equals(SimpleCsrfManager.PARAM_CSRF_ID)) {

            this.writeMessages(out, fieldError.getValue(), itemClass);
          }
        }
      }

      // Model errors
      List<Message> errors = context.getModelState().getErrors().get(ModelState.MODEL_FIELD);
      if (errors != null) {
        this.writeMessages(out, errors, itemClass);
      }

      // CSRF
      errors = context.getModelState().getErrors().get(SimpleCsrfManager.PARAM_CSRF_ID);
      if (errors != null) {
        this.writeMessages(out, errors, itemClass);
      }

      out.write(System.lineSeparator());
      out.write("</ul>");
      return new SafeString(out.toString());
    }
    return null;
  }

  protected void writeMessages(Writer out, List<Message> messages, String itemClass) throws IOException {
    for (Message message : messages) {
      out.write(System.lineSeparator());

      out.write("<li");
      if (itemClass == null) {
        HtmlUtils.escAttribute(out, "class", "l-fi-error");
      } else {
        out.write(" class=\"");
        out.write(itemClass);
        out.write(" l-fi-error\"");
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
}
