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
      return evalPath(model, path);
    }
    return getRequest().getParameter(path);
  }

  @Override
  protected void initTag() throws JspException, IOException {
    if (type != null) {
      Arguments.isTrue(TagUtils.isValidInputType(type));
    }
    Arguments.notNull(path);

    // Form Context
    var formCtx = getFormContext();
    if (model == null) {
      model = (formCtx != null) && (formCtx.getModel() != null) ? formCtx.getModel()
          : ServletUtils.REQUEST_ATTRIBUTE_MODEL;
    }
    if (formCtx != null) {
      form = formCtx.getForm();
    }

    id = TagUtils.toTagId(path);
    var isValid = !Objects.equals(form, getModelState().getForm()) || getModelState().isValid(path);

    // Handling value
    var handlingValue = getHandlingValue(isValid);
    _handlingValue = formatInputValue(handlingValue, formatter);

    // Class
    if (!isValid) {
      var errClass = TagUtils.CSS_FIELD_ERROR;
      clazz = (clazz == null) ? errClass : clazz + " " + errClass;
    }
  }

  @Override
  protected void writeAttributes(JspWriter out) throws JspException, IOException {
    HtmlUtils.escAttribute(out, "id", id);
    HtmlUtils.escAttribute(out, "type", (type != null) ? type : "text");
    HtmlUtils.escAttribute(out, "name", path);

    if (_handlingValue != null) {
      HtmlUtils.escAttribute(out, "value", _handlingValue);
    }

    if (required) {
      HtmlUtils.required(out);
    }

    if (disabled) {
      HtmlUtils.disabled(out);
    } else if (readonly) {
      HtmlUtils.readonly(out);
    }

    if ((form != null) && !isInFormContext()) {
      HtmlUtils.escAttribute(out, "form", form);
    }

    if (placeholder != null) {
      HtmlUtils.escAttribute(out, "placeholder", placeholder);
    }

    if (min != null) {
      HtmlUtils.escAttribute(out, "min", formatInputValue(min, formatter));
    }

    if (max != null) {
      HtmlUtils.escAttribute(out, "max", formatInputValue(max, formatter));
    }

    if (step != null) {
      HtmlUtils.escAttribute(out, "step", formatInputValue(step, formatter));
    }

    if (pattern != null) {
      HtmlUtils.escAttribute(out, "pattern", pattern);
    }

    if (minlength != null) {
      HtmlUtils.escAttribute(out, "minlength", minlength.toString());
    }

    if (maxlength != null) {
      HtmlUtils.escAttribute(out, "maxlength", maxlength.toString());
    }

    if (autocomplete != null) {
      HtmlUtils.escAttribute(out, "autocomplete", autocomplete);
    }

    if (autocapitalize != null) {
      HtmlUtils.escAttribute(out, "autocapitalize", autocapitalize);
    }

    if (autofocus) {
      HtmlUtils.autofocus(out);
    }

    if (inputmode != null) {
      HtmlUtils.escAttribute(out, "inputmode", inputmode);
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
