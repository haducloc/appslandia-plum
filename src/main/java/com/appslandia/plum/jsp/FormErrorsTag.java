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
import com.appslandia.common.utils.XmlEscaper;
import com.appslandia.plum.base.Message;
import com.appslandia.plum.base.ModelState;
import com.appslandia.plum.base.SimpleCsrfManager;
import com.appslandia.plum.tags.TagUtils;
import com.appslandia.plum.utils.HtmlUtils;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

/**
 *
 * @author Loc Ha
 *
 */
@Tag(name = "formErrors")
public class FormErrorsTag extends UITagBase {

  protected boolean modelOnly = true;
  protected String fieldOrder;
  protected String form;

  protected String listClass;
  protected String itemClass;

  @Override
  protected String getTagName() {
    return "div";
  }

  @Override
  protected void initTag() throws JspException, IOException {
    // Form Context
    var formCtx = getFormContext();
    if (formCtx != null) {
      form = formCtx.getForm();
    }

    // hasErrors
    var hasErrors = false;
    if (modelOnly) {
      hasErrors = Objects.equals(form, getModelState().getForm()) && (!getModelState().isValid(ModelState.FIELD_MODEL)
          || !getModelState().isValid(SimpleCsrfManager.PARAM_CSRF_ID));
    } else {
      hasErrors = !getModelState().isValid();
    }

    // Class
    if (!hasErrors) {
      var dNoneClass = TagUtils.CSS_D_NONE;
      clazz = (clazz == null) ? dNoneClass : clazz + " " + dNoneClass;
    }
  }

  @Override
  protected void writeAttributes(JspWriter out) throws JspException, IOException {
    if (id != null) {
      HtmlUtils.escAttribute(out, "id", id);
    }

    if (hidden) {
      HtmlUtils.hidden(out);
    }

    if (datatag != null) {
      HtmlUtils.escAttribute(out, "data-tag", datatag.toString());
    }
    if (clazz != null) {
      HtmlUtils.escAttribute(out, "class", clazz);
    }
    if (style != null) {
      HtmlUtils.escAttribute(out, "style", style);
    }
    if (title != null) {
      HtmlUtils.escAttribute(out, "title", title);
    }
  }

  @Override
  protected boolean hasClosing() {
    return true;
  }

  @Override
  protected void writeBody(JspWriter out) throws JspException, IOException {
    out.write("<ul");
    if (listClass != null) {
      HtmlUtils.escAttribute(out, "class", listClass);
    }
    out.write('>');

    if (!modelOnly) {
      List<Entry<String, List<Message>>> errors = new ArrayList<>(getModelState().getErrors().entrySet());
      if (fieldOrder != null) {
        Collections.sort(errors, toFieldComparator(fieldOrder));
      }

      for (Entry<String, List<Message>> fieldError : errors) {
        if (!fieldError.getKey().equals(ModelState.FIELD_MODEL)
            && !fieldError.getKey().equals(SimpleCsrfManager.PARAM_CSRF_ID)) {
          writeMessages(out, fieldError.getValue());
        }
      }
    }

    // Model errors
    var errors = getModelState().getErrors().get(ModelState.FIELD_MODEL);
    if (errors != null) {
      writeMessages(out, errors);
    }

    // CSRF
    errors = getModelState().getErrors().get(SimpleCsrfManager.PARAM_CSRF_ID);
    if (errors != null) {
      writeMessages(out, errors);
    }
    out.write("</ul>");
  }

  protected void writeMessages(JspWriter out, List<Message> messages) throws IOException {
    var itemClass = this.itemClass;
    for (Message message : messages) {
      out.write("<li");
      if (itemClass != null) {
        HtmlUtils.escAttribute(out, "class", itemClass);
      }
      out.write('>');

      if (message.isEscXml()) {
        XmlEscaper.escapeContent(out, message.getText());
      } else {
        out.write(message.getText());
      }
      out.write("</li>");
    }
  }

  private Comparator<Entry<String, List<Message>>> toFieldComparator(String fieldOrder) {
    final Map<String, Integer> posMap = new HashMap<>();
    var pos = 0;
    for (String field : SplitUtils.splitByComma(fieldOrder)) {
      posMap.put(field, ++pos);
    }
    return Comparator.comparing(entry -> posMap.getOrDefault(entry.getKey(), Integer.MAX_VALUE));
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setModelOnly(boolean modelOnly) {
    this.modelOnly = modelOnly;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setFieldOrder(String fieldOrder) {
    this.fieldOrder = fieldOrder;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setForm(String form) {
    this.form = form;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setListClass(String listClass) {
    this.listClass = listClass;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setItemClass(String itemClass) {
    this.itemClass = itemClass;
  }
}
