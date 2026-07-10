// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.jsp;

import java.io.IOException;
import java.util.Objects;

import com.appslandia.common.utils.Asserts;
import com.appslandia.plum.tags.TagUtils;
import com.appslandia.plum.utils.HtmlUtils;

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
  protected boolean hasFieldError;
  protected String describedby;

  protected boolean disabled;
  protected boolean required;
  protected boolean autofocus;

  protected String autocomplete;

  protected String _path;
  protected String _type;
  protected String _model;
  protected String _form;
  protected boolean _multi;

  protected boolean _isValid;
  protected String _handlingValue;

  @Override
  protected String getTagName() {
    return "input";
  }

  protected String getTagId(boolean multi) {
    var id = TagUtils.toTagId(_path);
    if (multi) {
      id = id + "_" + TagUtils.toIdPart(value);
    }
    return id;
  }

  protected Object getHandlingValue(boolean isValid) {
    return evalPath(_model, _path);
  }

  @Override
  protected void initTag() throws JspException, IOException {
    var choiceBox = findParent(ChoiceGroupTag.class);
    Asserts.notNull(choiceBox, "No ChoiceGroupTag parent found.");

    _path = choiceBox.path;
    _type = choiceBox.type;
    _model = choiceBox.model;
    _form = choiceBox.form;
    _multi = choiceBox.multi;

    this.value = formatValue(value, choiceBox.fmt);
    id = getTagId(_multi);

    _isValid = !Objects.equals(_form, getModelState().getForm()) || getModelState().isValid(_path);
    var handlingValue = getHandlingValue(_isValid);
    _handlingValue = formatValue(handlingValue, choiceBox.fmt);
  }

  protected boolean isChecked() {
    if ("checkbox".equals(_type)) {
      return TagUtils.isCheckboxChecked((String) value, _handlingValue);
    } else {
      return TagUtils.isRadioChecked((String) value, _handlingValue);
    }
  }

  @Override
  protected void writeAttributes(JspWriter out) throws JspException, IOException {
    HtmlUtils.escAttribute(out, "id", id);
    HtmlUtils.escAttribute(out, "type", _type);
    HtmlUtils.escAttribute(out, "name", _path);

    if (value != null) {
      HtmlUtils.escAttribute(out, "value", (String) value);
    }

    if (hidden) {
      HtmlUtils.hidden(out);
    }

    if (isChecked()) {
      HtmlUtils.checked(out);
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

    if (_form != null) {
      HtmlUtils.escAttribute(out, "form", _form);
    }

    if (autocomplete != null) {
      HtmlUtils.escAttribute(out, "autocomplete", autocomplete);
    }

    if (!_multi && !_isValid) {
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
      var fieldErrId = TagUtils.toFieldErrorId(_path);
      descBy = (descBy == null) ? fieldErrId : descBy + " " + fieldErrId;
    }
    if (descBy != null) {
      HtmlUtils.escAttribute(out, "aria-describedby", descBy);
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

  @Attribute(rtexprvalue = true, required = false, description = "Field error is defined")
  public void setHasFieldError(boolean hasFieldError) {
    this.hasFieldError = hasFieldError;
  }

  @Attribute(rtexprvalue = true, required = false, description = "aria-describedby")
  public void setDescribedby(String describedby) {
    this.describedby = describedby;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setDisabled(boolean disabled) {
    this.disabled = disabled;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setRequired(boolean required) {
    this.required = required;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setAutofocus(boolean autofocus) {
    this.autofocus = autofocus;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setAutocomplete(String autocomplete) {
    this.autocomplete = autocomplete;
  }
}
