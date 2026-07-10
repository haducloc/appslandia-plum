// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.jsp;

import java.io.IOException;
import java.util.Objects;

import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.XmlEscaper;
import com.appslandia.plum.base.SelectItem;
import com.appslandia.plum.tags.TagUtils;
import com.appslandia.plum.utils.HtmlUtils;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

/**
 *
 * @author Loc Ha
 *
 */
@Tag(name = "select")
public class SelectTag extends UITagBase {

  protected String path;
  protected String model;
  protected String form;
  protected String fmt;

  protected Iterable<SelectItem> items;
  protected String optBlank;

  protected boolean hasFieldError;
  protected String describedby;

  protected boolean required;
  protected boolean disabled;
  protected boolean autofocus;

  protected Integer size;
  protected String autocomplete;

  protected boolean _isValid;
  protected String _handlingValue;

  @Override
  protected String getTagName() {
    return "select";
  }

  protected Object getHandlingValue(boolean isValid) {
    return evalPath(model, path);
  }

  @Override
  protected void initTag() throws JspException, IOException {
    Arguments.notNull(path);
    id = TagUtils.toTagId(path);

    var formCtx = getFormContext();
    model = getModel(model, formCtx);
    form = getForm(form, formCtx);

    _isValid = !Objects.equals(form, getModelState().getForm()) || getModelState().isValid(path);
    var handlingValue = getHandlingValue(_isValid);
    _handlingValue = formatValue(handlingValue, fmt);

    // Class
    if (!_isValid) {
      var errClass = TagUtils.CSS_FIELD_ERROR;
      clazz = (clazz == null) ? errClass : clazz + " " + errClass;
    }
  }

  @Override
  protected void writeAttributes(JspWriter out) throws JspException, IOException {
    HtmlUtils.escAttribute(out, "id", id);
    HtmlUtils.escAttribute(out, "name", path);

    if (hidden) {
      HtmlUtils.hidden(out);
    }

    if (disabled) {
      HtmlUtils.disabled(out);
    }

    if (required) {
      HtmlUtils.required(out);
    }

    if (autofocus) {
      HtmlUtils.autofocus(out);
    }

    if (form != null) {
      HtmlUtils.escAttribute(out, "form", form);
    }

    if (size != null) {
      HtmlUtils.escAttribute(out, "size", size.toString());
    }

    if (autocomplete != null) {
      HtmlUtils.escAttribute(out, "autocomplete", autocomplete);
    }

    if (!_isValid) {
      out.write(" aria-invalid=\"true\"");
    }

    // aria-describedby
    writeDescribedby(out);

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

  protected void writeDescribedby(JspWriter out) throws IOException {
    var descBy = this.describedby;

    if (!_isValid && hasFieldError) {
      var fieldErrId = TagUtils.toFieldErrorId(path);
      descBy = (descBy == null) ? fieldErrId : descBy + " " + fieldErrId;
    }
    if (descBy != null) {
      HtmlUtils.escAttribute(out, "aria-describedby", descBy);
    }
  }

  protected void writeOptBlank(JspWriter out, String optLabel, boolean selected) throws JspException, IOException {
    out.write("<option value=\"\"");
    if (selected) {
      HtmlUtils.selected(out);
    }

    out.write('>');
    if (optLabel != null) {
      XmlEscaper.escapeContent(out, optLabel);
    }
    out.write("</option>");
  }

  protected void writeOption(JspWriter out, SelectItem item, boolean selected, boolean optDisabled)
      throws JspException, IOException {
    out.write("<option");
    if (item.getValue() != null) {
      HtmlUtils.escAttribute(out, "value", formatValue(item.getValue(), fmt));
    }

    if (selected) {
      HtmlUtils.selected(out);
    }

    if (optDisabled) {
      HtmlUtils.disabled(out);
    }
    out.write('>');

    if (item.getDisplayName() != null) {
      XmlEscaper.escapeContent(out, item.getDisplayName());
    }
    out.write("</option>");
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
    // optBlank
    if (optBlank != null) {
      var selected = (_handlingValue == null);
      writeOptBlank(out, optBlank.strip(), selected);
    }

    // items
    if (items != null) {
      for (SelectItem item : items) {
        var itemVal = formatValue(item.getValue(), fmt);
        var selected = Objects.equals(itemVal, _handlingValue);

        writeOption(out, item, selected, item.isDisabled());
      }
    }
  }

  @Override
  public void setId(String id) {
    throw new UnsupportedOperationException();
  }

  @Attribute(rtexprvalue = true, required = true)
  public void setPath(String path) {
    this.path = path;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setModel(String model) {
    this.model = model;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setForm(String form) {
    this.form = form;
  }

  @Attribute(rtexprvalue = true, required = false, description = "Formatter ID")
  public void setFmt(String fmt) {
    this.fmt = fmt;
  }

  @Attribute(rtexprvalue = true, required = true)
  public void setItems(Iterable<SelectItem> items) {
    this.items = items;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setOptBlank(String optBlank) {
    this.optBlank = optBlank;
  }

  @Attribute(rtexprvalue = true, required = false, description = "Field error is defined")
  public void setHasFieldError(boolean hasFieldError) {
    this.hasFieldError = hasFieldError;
  }

  @Attribute(rtexprvalue = true, required = false, description = "aria-describedby")
  public void setDescribedby(String describedby) {
    this.describedby = describedby;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setRequired(boolean required) {
    this.required = required;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setDisabled(boolean disabled) {
    this.disabled = disabled;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setAutofocus(boolean autofocus) {
    this.autofocus = autofocus;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setSize(Integer size) {
    this.size = size;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setAutocomplete(String autocomplete) {
    this.autocomplete = autocomplete;
  }
}
