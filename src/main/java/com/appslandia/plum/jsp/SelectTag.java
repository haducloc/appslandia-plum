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
import java.util.Objects;

import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.XmlEscaper;
import com.appslandia.plum.base.SelectItem;
import com.appslandia.plum.tags.TagUtils;
import com.appslandia.plum.utils.HtmlUtils;
import com.appslandia.plum.utils.ServletUtils;

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
  protected boolean required;
  protected boolean disabled;

  protected String model;
  protected String form;
  protected String formatter;
  protected Integer size;

  protected String autocomplete;
  protected boolean autofocus;

  protected Iterable<SelectItem> items;
  protected boolean optional;
  protected String optLabel;

  protected String _handlingValue;

  @Override
  protected String getTagName() {
    return "select";
  }

  protected Object getHandlingValue(boolean isValid) {
    return this.evalPath(this.model, this.path);
  }

  @Override
  protected void initTag() throws JspException, IOException {
    Arguments.notNull(this.path);

    // Form Context
    var formCtx = getFormContext();
    if (this.model == null) {
      this.model = (formCtx != null) && (formCtx.getModel() != null) ? formCtx.getModel()
          : ServletUtils.REQUEST_ATTRIBUTE_MODEL;
    }
    if (formCtx != null) {
      this.form = formCtx.getForm();
    }

    this.id = TagUtils.toTagId(this.path);
    var isValid = !Objects.equals(this.form, this.getModelState().getForm()) || this.getModelState().isValid(this.path);

    // Handling value
    var handlingValue = getHandlingValue(isValid);
    this._handlingValue = formatInputValue(handlingValue, this.formatter);

    // Class
    if (!isValid) {
      var errClass = TagUtils.CSS_FIELD_ERROR;
      this.clazz = (this.clazz == null) ? errClass : this.clazz + " " + errClass;
    }
  }

  @Override
  protected void writeAttributes(JspWriter out) throws JspException, IOException {
    HtmlUtils.escAttribute(out, "id", this.id);
    HtmlUtils.escAttribute(out, "name", this.path);

    if (this.required) {
      HtmlUtils.required(out);
    }

    if (this.disabled) {
      HtmlUtils.disabled(out);
    }

    if ((this.form != null) && !isInFormContext()) {
      HtmlUtils.escAttribute(out, "form", this.form);
    }

    if (this.size != null) {
      HtmlUtils.escAttribute(out, "size", this.size.toString());
    }

    if (this.autocomplete != null) {
      HtmlUtils.escAttribute(out, "autocomplete", this.autocomplete);
    }

    if (this.autofocus) {
      HtmlUtils.autofocus(out);
    }

    if (this.hidden) {
      HtmlUtils.hidden(out);
    }

    if (this.datatag != null) {
      HtmlUtils.escAttribute(out, "data-tag", this.datatag.toString());
    }
    if (this.clazz != null) {
      HtmlUtils.escAttribute(out, "class", this.clazz);
    }
    if (this.style != null) {
      HtmlUtils.escAttribute(out, "style", this.style);
    }
    if (this.title != null) {
      HtmlUtils.escAttribute(out, "title", this.title);
    }
  }

  protected void writeOptItem(JspWriter out, String optLabel, boolean selected) throws JspException, IOException {
    out.write("<option");
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
      HtmlUtils.escAttribute(out, "value", formatInputValue(item.getValue(), this.formatter));
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
  protected void writeBody(JspWriter out) throws JspException, IOException {
    // optional
    if (this.optional) {
      var selected = (this._handlingValue == null);
      writeOptItem(out, this.optLabel, selected);
    }

    // items
    if (this.items != null) {
      for (SelectItem item : this.items) {

        var itemVal = formatInputValue(item.getValue(), this.formatter);
        var selected = Objects.equals(itemVal, this._handlingValue);
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
  public void setRequired(boolean required) {
    this.required = required;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setDisabled(boolean disabled) {
    this.disabled = disabled;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setModel(String model) {
    this.model = model;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setForm(String form) {
    this.form = form;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setFormatter(String formatter) {
    this.formatter = formatter;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setSize(Integer size) {
    this.size = size;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setAutocomplete(String autocomplete) {
    this.autocomplete = autocomplete;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setAutofocus(boolean autofocus) {
    this.autofocus = autofocus;
  }

  @Attribute(rtexprvalue = true, required = true)
  public void setItems(Iterable<SelectItem> items) {
    this.items = items;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setOptional(boolean optional) {
    this.optional = optional;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setOptLabel(String optLabel) {
    this.optLabel = optLabel;
  }
}
