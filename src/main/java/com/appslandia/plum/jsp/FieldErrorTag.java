// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.jsp;

import java.io.IOException;
import java.util.Objects;

import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.ValueUtils;
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
@Tag(name = "fieldError")
public class FieldErrorTag extends UITagBase {

  protected String path;
  protected String form;
  protected String role;

  protected boolean _isValid;

  @Override
  protected String getTagName() {
    return "div";
  }

  @Override
  protected void initTag() throws JspException, IOException {
    Arguments.notNull(path);
    id = TagUtils.toFieldErrorId(path);

    var formCtx = getFormContext();
    form = getForm(form, formCtx);
    var isValid = !Objects.equals(form, getModelState().getForm()) || getModelState().isValid(path);

    // Class
    var clazz = ValueUtils.valueOrAlt(this.clazz, TagUtils.CSS_ERROR_MSG);
    if (isValid) {
      clazz = clazz + " " + TagUtils.CSS_D_NONE;
    }

    this.clazz = clazz;
    _isValid = isValid;
  }

  @Override
  protected void writeAttributes(JspWriter out) throws JspException, IOException {
    HtmlUtils.escAttribute(out, "id", id);

    if (hidden) {
      HtmlUtils.hidden(out);
    }

    if (role != null) {
      HtmlUtils.escAttribute(out, "role", role);
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
    return true;
  }

  @Override
  protected void writeBody(JspWriter out) throws JspException, IOException {
    if (_isValid) {
      return;
    }

    var error = getModelState().getFieldErrors(path).get(0);
    if (error.isEscXml()) {
      XmlEscaper.escapeContent(out, error.getText());
    } else {
      out.write(error.getText());
    }
  }

  @Attribute(rtexprvalue = true, required = true)
  public void setPath(String path) {
    this.path = path;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setForm(String form) {
    this.form = form;
  }

  @Attribute(rtexprvalue = true, required = false, description = "alert")
  public void setRole(String role) {
    this.role = role;
  }
}
