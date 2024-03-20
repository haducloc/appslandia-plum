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
@Tag(name = "errors", dynamicAttributes = false, bodyContent = "scriptless")
public class FormErrorsTag extends TagBase {

  protected String form;
  protected String fieldOrders;
  protected boolean includeFields = true;

  protected String divClass;
  protected String listClass;
  protected String itemClass;

  @Override
  public void doTag() throws JspException, IOException {
    boolean hasErrors = false;
    if (this.includeFields) {
      hasErrors = Objects.equals(this.form, this.getModelState().getForm()) && !this.getModelState().isValid();
    } else {
      hasErrors = Objects.equals(this.form, this.getModelState().getForm())
          && !(this.getModelState().isValid(ModelState.MODEL_FIELD)
              && this.getModelState().isValid(SimpleCsrfManager.PARAM_CSRF_ID));
    }

    if (hasErrors) {
      JspWriter out = this.pageContext.getOut();

      out.write("<div");
      HtmlUtils.escAttribute(out, "class", this.divClass);
      out.write(">");

      out.write("<ul");
      if (this.listClass != null) {
        HtmlUtils.escAttribute(out, "class", this.listClass);
      }
      out.write(">");

      // Write field errors
      if (this.includeFields) {
        List<Entry<String, List<Message>>> errors = new ArrayList<>(this.getModelState().getErrors().entrySet());
        if (this.fieldOrders != null) {
          Collections.sort(errors, toFieldComparator(this.fieldOrders));
        }

        for (Entry<String, List<Message>> fieldError : errors) {
          if (!fieldError.getKey().equals(ModelState.MODEL_FIELD)
              && !fieldError.getKey().equals(SimpleCsrfManager.PARAM_CSRF_ID)) {
            this.writeMessages(out, fieldError.getValue());
          }
        }
      }

      // Model errors
      List<Message> errors = this.getModelState().getErrors().get(ModelState.MODEL_FIELD);
      if (errors != null) {
        this.writeMessages(out, errors);
      }

      // CSRF
      errors = this.getModelState().getErrors().get(SimpleCsrfManager.PARAM_CSRF_ID);
      if (errors != null) {
        this.writeMessages(out, errors);
      }

      out.newLine();
      out.write("</ul>");
      out.write("</div>");
    }
  }

  protected void writeMessages(JspWriter out, List<Message> messages) throws IOException {
    for (Message message : messages) {
      out.newLine();

      out.write("<li");
      if (this.itemClass != null) {
        HtmlUtils.escAttribute(out, "class", this.itemClass);
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

  public static Comparator<Map.Entry<String, List<Message>>> toFieldComparator(String fieldOrders) {
    final Map<String, Integer> posMap = new HashMap<>();
    int pos = 0;
    String[] fieldNames = SplitUtils.splitByComma(fieldOrders);

    for (String fieldName : fieldNames) {
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

  @Attribute(required = false, rtexprvalue = false, description = "username,password,etc.")
  public void setFieldOrders(String fieldOrders) {
    this.fieldOrders = fieldOrders;
  }

  @Attribute(required = false, rtexprvalue = false)
  public void setIncludeFields(boolean includeFields) {
    this.includeFields = includeFields;
  }

  @Attribute(required = true, rtexprvalue = false)
  public void setDivClass(String divClass) {
    this.divClass = divClass;
  }

  @Attribute(required = false, rtexprvalue = false)
  public void setListClass(String listClass) {
    this.listClass = listClass;
  }

  @Attribute(required = false, rtexprvalue = false)
  public void setItemClass(String itemClass) {
    this.itemClass = itemClass;
  }
}
