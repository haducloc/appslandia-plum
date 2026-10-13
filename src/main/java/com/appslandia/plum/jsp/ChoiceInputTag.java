// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.jsp;

import java.io.IOException;

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
  protected String describedby;

  protected boolean disabled;
  protected boolean autofocus;
  protected String autocomplete;

  protected ChoiceGroupTag _choiceGroup;
  protected String _handlingValue;

  @Override
  protected String getTagName() {
    return "input";
  }

  protected Object getHandlingValue(boolean isValid) {
    return evalPath(_choiceGroup.model, _choiceGroup.path);
  }

  @Override
  protected void initTag() throws JspException, IOException {
    _choiceGroup = findParent(ChoiceGroupTag.class);
    Asserts.notNull(_choiceGroup, "No ChoiceGroupTag parent found.");

    value = formatValue(value, _choiceGroup.fmt);
    id = _choiceGroup.toChoiceId(value);

    var handlingValue = getHandlingValue(_choiceGroup._isValid);
    _handlingValue = formatValue(handlingValue, _choiceGroup.fmt);
  }

  protected boolean isChecked() {
    if ("checkbox".equals(_choiceGroup.type)) {
      return TagUtils.isCheckboxChecked((String) value, _handlingValue);
    } else {
      return TagUtils.isRadioChecked((String) value, _handlingValue);
    }
  }

  @Override
  protected void writeAttributes(JspWriter out) throws JspException, IOException {
    HtmlUtils.escAttribute(out, "id", id);
    HtmlUtils.escAttribute(out, "type", _choiceGroup.type);
    HtmlUtils.escAttribute(out, "name", _choiceGroup.path);

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

    if (!_choiceGroup.multi && _choiceGroup.required) {
      HtmlUtils.required(out);
    }

    if (autofocus) {
      HtmlUtils.autofocus(out);
    }

    if (_choiceGroup.form != null) {
      HtmlUtils.escAttribute(out, "form", _choiceGroup.form);
    }

    if (autocomplete != null) {
      HtmlUtils.escAttribute(out, "autocomplete", autocomplete);
    }

    if (!_choiceGroup.multi && !_choiceGroup._isValid) {
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

    if (!_choiceGroup.multi && !_choiceGroup._isValid && _choiceGroup.hasErrorFeedback) {
      var fieldErrId = TagUtils.toFieldErrorId(_choiceGroup.path);
      descBy = (descBy == null) ? fieldErrId : descBy + " " + fieldErrId;
    }
    if (descBy != null) {
      HtmlUtils.escAttribute(out, "aria-describedby", descBy);
    }
  }

  @Override
  protected void writeTag(JspWriter out) throws JspException, IOException {
    out.newLine();
    super.writeTag(out);
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

  @Attribute(rtexprvalue = true, required = false, description = "aria-describedby")
  public void setDescribedby(String describedby) {
    this.describedby = describedby;
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
  public void setAutocomplete(String autocomplete) {
    this.autocomplete = autocomplete;
  }
}
