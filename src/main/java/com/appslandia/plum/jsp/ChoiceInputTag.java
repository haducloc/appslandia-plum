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
    var id = TagUtils.toTagId(this._path);
    if (multiple) {
      id = id + "_" + TagUtils.toIdPart(this.value);
    }
    return id;
  }

  protected Object getHandlingValue(boolean isValid) {
    return this.evalPath(this.model, this._path);
  }

  @Override
  protected void initTag() throws JspException, IOException {
    // Form Context
    var formCtx = getFormContext();
    if (this.model == null) {
      this.model = (formCtx != null) && (formCtx.getModel() != null) ? formCtx.getModel()
          : ServletUtils.REQUEST_ATTRIBUTE_MODEL;
    }
    if (formCtx != null) {
      this.form = formCtx.getForm();
    }

    // choiceBox
    var choiceBox = findParent(ChoiceBoxTag.class);
    Asserts.notNull(choiceBox, "No ChoiceBoxTag found.");

    this.value = formatInputValue(this.value, choiceBox.formatter);
    this._path = choiceBox.path;
    this.id = getTagId(choiceBox.multiple);

    var isValid = !Objects.equals(this.form, this.getModelState().getForm())
        || this.getModelState().isValid(this._path);

    // Handling value
    var handlingValue = getHandlingValue(isValid);
    this._handlingValue = formatInputValue(handlingValue, choiceBox.formatter);
  }

  protected boolean isChecked() {
    if (this.checkbox) {
      return TagUtils.isCheckboxChecked((String) this.value, this._handlingValue);
    } else {
      return TagUtils.isRadioChecked((String) this.value, this._handlingValue);
    }
  }

  @Override
  protected void writeAttributes(JspWriter out) throws JspException, IOException {
    HtmlUtils.escAttribute(out, "id", this.id);
    HtmlUtils.escAttribute(out, "type", this.checkbox ? "checkbox" : "radio");
    HtmlUtils.escAttribute(out, "name", this._path);
    HtmlUtils.escAttribute(out, "value", (String) this.value);

    if (this.isChecked()) {
      HtmlUtils.checked(out);
    }

    if (this.required) {
      HtmlUtils.required(out);
    }

    if (this.disabled) {
      HtmlUtils.disabled(out);
    }

    if ((this.form != null) && !isInFormContext()) {
      HtmlUtils.escAttribute(out, "form", this.form);
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
