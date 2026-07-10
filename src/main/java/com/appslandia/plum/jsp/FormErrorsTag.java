// Licensed under the MIT License.
// See LICENSE file in the project root for details.

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
  protected String role;

  @Override
  protected String getTagName() {
    return "div";
  }

  @Override
  protected void initTag() throws JspException, IOException {
    var formCtx = getFormContext();
    form = getForm(form, formCtx);

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

    if (role != null) {
      HtmlUtils.escAttribute(out, "role", role);
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
  protected void writeTag(JspWriter out) throws JspException, IOException {
    out.newLine();
    super.writeTag(out);
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

  @Attribute(rtexprvalue = true, required = false, description = "alert")
  public void setRole(String role) {
    this.role = role;
  }
}
