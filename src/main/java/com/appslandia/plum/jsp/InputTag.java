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
@Tag(name = "input")
public class InputTag extends UITagBase {

  protected String type;
  protected String path;
  protected boolean required;
  protected boolean readonly;
  protected boolean disabled;

  protected String model;
  protected String form;
  protected String placeholder;
  protected String formatter;

  protected Object min;
  protected Object max;
  protected Object step;
  protected String pattern;

  protected Integer minlength;
  protected Integer maxlength;

  protected String autocomplete;
  protected String autocapitalize;
  protected boolean autofocus;
  protected String inputmode;

  protected String _handlingValue;

  @Override
  protected String getTagName() {
    return "input";
  }

  protected Object getHandlingValue(boolean isValid) {
    if (isValid) {
      return this.evalPath(this.model, this.path);
    }
    return this.getRequest().getParameter(this.path);
  }

  @Override
  protected void initTag() throws JspException, IOException {
    if (this.type != null) {
      Arguments.isTrue(TagUtils.isValidInputType(this.type));
    }
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
    HtmlUtils.escAttribute(out, "type", (this.type != null) ? this.type : "text");
    HtmlUtils.escAttribute(out, "name", this.path);

    if (this._handlingValue != null) {
      HtmlUtils.escAttribute(out, "value", this._handlingValue);
    }

    if (this.required) {
      HtmlUtils.required(out);
    }

    if (this.disabled) {
      HtmlUtils.disabled(out);
    } else if (this.readonly) {
      HtmlUtils.readonly(out);
    }

    if ((this.form != null) && !isInFormContext()) {
      HtmlUtils.escAttribute(out, "form", this.form);
    }

    if (this.placeholder != null) {
      HtmlUtils.escAttribute(out, "placeholder", this.placeholder);
    }

    if (this.min != null) {
      HtmlUtils.escAttribute(out, "min", formatInputValue(this.min, this.formatter));
    }

    if (this.max != null) {
      HtmlUtils.escAttribute(out, "max", formatInputValue(this.max, this.formatter));
    }

    if (this.step != null) {
      HtmlUtils.escAttribute(out, "step", formatInputValue(this.step, this.formatter));
    }

    if (this.pattern != null) {
      HtmlUtils.escAttribute(out, "pattern", this.pattern);
    }

    if (this.minlength != null) {
      HtmlUtils.escAttribute(out, "minlength", this.minlength.toString());
    }

    if (this.maxlength != null) {
      HtmlUtils.escAttribute(out, "maxlength", this.maxlength.toString());
    }

    if (this.autocomplete != null) {
      HtmlUtils.escAttribute(out, "autocomplete", this.autocomplete);
    }

    if (this.autocapitalize != null) {
      HtmlUtils.escAttribute(out, "autocapitalize", this.autocapitalize);
    }

    if (this.autofocus) {
      HtmlUtils.autofocus(out);
    }

    if (this.inputmode != null) {
      HtmlUtils.escAttribute(out, "inputmode", this.inputmode);
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

  @Attribute(rtexprvalue = true, required = false)
  public void setType(String type) {
    this.type = type;
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
  public void setReadonly(boolean readonly) {
    this.readonly = readonly;
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
  public void setPlaceholder(String placeholder) {
    this.placeholder = placeholder;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setFormatter(String formatter) {
    this.formatter = formatter;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setMin(Object min) {
    this.min = min;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setMax(Object max) {
    this.max = max;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setStep(Object step) {
    this.step = step;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setPattern(String pattern) {
    this.pattern = pattern;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setMinlength(Integer minlength) {
    this.minlength = minlength;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setMaxlength(Integer maxlength) {
    this.maxlength = maxlength;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setAutocomplete(String autocomplete) {
    this.autocomplete = autocomplete;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setAutocapitalize(String autocapitalize) {
    this.autocapitalize = autocapitalize;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setAutofocus(boolean autofocus) {
    this.autofocus = autofocus;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setInputmode(String inputmode) {
    this.inputmode = inputmode;
  }
}
