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

import com.appslandia.common.utils.Asserts;
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
@Tag(name = "choiceInput")
public class ChoiceInputTag extends UITagBase {

  protected Object value;
  protected boolean checkbox = true;

  protected boolean required;
  protected boolean disabled;

  protected String model;
  protected String form;

  protected String autocomplete;
  protected boolean autofocus;

  protected String _path;
  protected String _handlingValue;

  @Override
  protected String getTagName() {
    return "input";
  }

  protected String getTagId(boolean multiple) {
    var id = TagUtils.toTagId(_path);
    if (multiple) {
      id = id + "_" + TagUtils.toIdPart(value);
    }
    return id;
  }

  protected Object getHandlingValue(boolean isValid) {
    return evalPath(model, _path);
  }

  @Override
  protected void initTag() throws JspException, IOException {
    // Form Context
    var formCtx = getFormContext();
    if (model == null) {
      model = (formCtx != null) && (formCtx.getModel() != null) ? formCtx.getModel()
          : ServletUtils.REQUEST_ATTRIBUTE_MODEL;
    }
    if (formCtx != null) {
      form = formCtx.getForm();
    }

    // choiceBox
    var choiceBox = findParent(ChoiceBoxTag.class);
    Asserts.notNull(choiceBox, "No ChoiceBoxTag found.");

    value = formatInputValue(value, choiceBox.formatter);
    _path = choiceBox.path;
    id = getTagId(choiceBox.multiple);

    var isValid = !Objects.equals(form, getModelState().getForm()) || getModelState().isValid(_path);

    // Handling value
    var handlingValue = getHandlingValue(isValid);
    _handlingValue = formatInputValue(handlingValue, choiceBox.formatter);
  }

  protected boolean isChecked() {
    if (checkbox) {
      return TagUtils.isCheckboxChecked((String) value, _handlingValue);
    } else {
      return TagUtils.isRadioChecked((String) value, _handlingValue);
    }
  }

  @Override
  protected void writeAttributes(JspWriter out) throws JspException, IOException {
    HtmlUtils.escAttribute(out, "id", id);
    HtmlUtils.escAttribute(out, "type", checkbox ? "checkbox" : "radio");
    HtmlUtils.escAttribute(out, "name", _path);
    HtmlUtils.escAttribute(out, "value", (String) value);

    if (isChecked()) {
      HtmlUtils.checked(out);
    }

    if (required) {
      HtmlUtils.required(out);
    }

    if (disabled) {
      HtmlUtils.disabled(out);
    }

    if ((form != null) && !isInFormContext()) {
      HtmlUtils.escAttribute(out, "form", form);
    }

    if (autocomplete != null) {
      HtmlUtils.escAttribute(out, "autocomplete", autocomplete);
    }

    if (autofocus) {
      HtmlUtils.autofocus(out);
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
    return false;
  }

  @Override
  protected void writeBody(JspWriter out) throws JspException, IOException {
  }

  @Override
  public void setId(String id) {
    throw new UnsupportedOperationException();
  }

  @Attribute(rtexprvalue = true, required = true)
  public void setValue(Object value) {
    this.value = value;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setCheckbox(boolean checkbox) {
    this.checkbox = checkbox;
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
  public void setAutocomplete(String autocomplete) {
    this.autocomplete = autocomplete;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setAutofocus(boolean autofocus) {
    this.autofocus = autofocus;
  }
}
