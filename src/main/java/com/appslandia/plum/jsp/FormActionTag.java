// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.jsp;

import java.io.IOException;

import com.appslandia.plum.utils.HtmlUtils;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

/**
 *
 * @author Loc Ha
 *
 */
@Tag(name = "formAction", dynamicAttributes = false)
public class FormActionTag extends UITagBase {

  protected String form;

  @Override
  protected String getTagName() {
    return "input";
  }

  @Override
  protected void initTag() throws JspException, IOException {
    var formCtx = getFormContext();
    form = getForm(form, formCtx);
  }

  @Override
  protected void writeAttributes(JspWriter out) throws JspException, IOException {
    HtmlUtils.escAttribute(out, "id", ServletUtils.PARAM_FORM_ACTION);
    HtmlUtils.escAttribute(out, "type", "hidden");
    HtmlUtils.escAttribute(out, "name", ServletUtils.PARAM_FORM_ACTION);

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

  @Attribute(rtexprvalue = true, required = false)
  public void setForm(String form) {
    this.form = form;
  }

  @Override
  @Attribute(rtexprvalue = true, required = false)
  public void setDatatag(Object datatag) {
    this.datatag = datatag;
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
