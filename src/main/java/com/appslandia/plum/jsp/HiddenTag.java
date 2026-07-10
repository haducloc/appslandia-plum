// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.jsp;

import java.io.IOException;
import java.util.Objects;

import com.appslandia.common.utils.Arguments;
import com.appslandia.plum.tags.TagUtils;
import com.appslandia.plum.utils.HtmlUtils;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

/**
 *
 * @author Loc Ha
 *
 */
@Tag(name = "hidden")
public class HiddenTag extends UITagBase {

  protected String path;
  protected String form;
  protected String model;
  protected String fmt;
  protected boolean disabled;

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
    Arguments.notNull(path);
    id = TagUtils.toTagId(path);

    var formCtx = getFormContext();
    model = getModel(model, formCtx);
    form = getForm(form, formCtx);

    var isValid = !Objects.equals(form, getModelState().getForm()) || getModelState().isValid(path);
    var handlingValue = getHandlingValue(isValid);
    _handlingValue = formatValue(handlingValue, fmt);
  }

  @Override
  protected void writeAttributes(JspWriter out) throws JspException, IOException {
    HtmlUtils.escAttribute(out, "id", id);
    HtmlUtils.escAttribute(out, "type", "hidden");
    HtmlUtils.escAttribute(out, "name", path);

    if (_handlingValue != null) {
      HtmlUtils.escAttribute(out, "value", _handlingValue);
    }

    if (disabled) {
      HtmlUtils.disabled(out);
    }

    if (form != null) {
      HtmlUtils.escAttribute(out, "form", form);
    }

    if (datatag != null) {
      HtmlUtils.escAttribute(out, "data-tag", datatag.toString());
    }
  }

  @Override
  protected boolean hasClosing() {
    return false;
  }

  @Override
  protected void writeTag(JspWriter out) throws JspException, IOException {
    out.newLine();
    super.writeTag(out);
  }

  @Override
  protected void writeBody(JspWriter out) throws JspException, IOException {
  }

  @Attribute(rtexprvalue = true, required = true)
  public void setPath(String path) {
    this.path = path;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setForm(String form) {
    this.form = form;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setModel(String model) {
    this.model = model;
  }

  @Attribute(rtexprvalue = true, required = false, description = "Formatter ID")
  public void setFmt(String fmt) {
    this.fmt = fmt;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setDisabled(boolean disabled) {
    this.disabled = disabled;
  }

  @Override
  public void setId(String id) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setHidden(boolean hidden) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setClazz(String clazz) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setStyle(String style) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setTitle(String title) {
    throw new UnsupportedOperationException();
  }
}
