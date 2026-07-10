// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.jsp;

import java.io.IOException;
import java.util.Objects;

import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.XmlEscaper;
import com.appslandia.plum.tags.TagUtils;
import com.appslandia.plum.utils.HtmlUtils;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

/**
 *
 * @author Loc Ha
 *
 */
@Tag(name = "textarea")
public class TextAreaTag extends UITagBase {

  protected String path;
  protected String model;
  protected String form;
  protected String fmt;

  protected boolean hasFieldError;
  protected String describedby;

  protected boolean required;
  protected boolean readonly;
  protected boolean disabled;
  protected boolean autofocus;

  protected String placeholder;

  protected Integer rows;
  protected Integer cols;

  protected Integer minlength;
  protected Integer maxlength;

  protected String autocomplete;
  protected String autocapitalize;

  protected String inputmode;
  protected String wrap;

  protected boolean _isValid;
  protected String _handlingValue;

  @Override
  protected String getTagName() {
    return "textarea";
  }

  protected Object getHandlingValue(boolean isValid) {
    if (isValid) {
      return evalPath(model, path);
    }
    return getRequest().getParameter(path);
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
    } else if (readonly) {
      HtmlUtils.readonly(out);
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

    if (placeholder != null) {
      HtmlUtils.escAttribute(out, "placeholder", placeholder);
    }

    if (rows != null) {
      HtmlUtils.escAttribute(out, "rows", rows.toString());
    }

    if (cols != null) {
      HtmlUtils.escAttribute(out, "cols", cols.toString());
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

    if (inputmode != null) {
      HtmlUtils.escAttribute(out, "inputmode", inputmode);
    }

    if (wrap != null) {
      HtmlUtils.escAttribute(out, "wrap", wrap);
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

  @Override
  protected boolean hasClosing() {
    return true;
  }

  @Override
  protected void writeBody(JspWriter out) throws JspException, IOException {
    if (_handlingValue != null) {
      XmlEscaper.escapeContent(out, _handlingValue);
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
  public void setReadonly(boolean readonly) {
    this.readonly = readonly;
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
  public void setPlaceholder(String placeholder) {
    this.placeholder = placeholder;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setRows(Integer rows) {
    this.rows = rows;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setCols(Integer cols) {
    this.cols = cols;
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
  public void setInputmode(String inputmode) {
    this.inputmode = inputmode;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setWrap(String wrap) {
    this.wrap = wrap;
  }
}
