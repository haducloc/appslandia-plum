// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.jsp;

import java.io.IOException;

import com.appslandia.common.utils.XmlEscaper;
import com.appslandia.plum.utils.HtmlUtils;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

/**
 *
 * @author Loc Ha
 *
 */
@Tag(name = "button", bodyContent = "scriptless")
public class ButtonTag extends UITagBase {

  protected String labelKey;
  protected String type;
  protected String name;
  protected String value;
  protected String form;

  protected boolean disabled;
  protected boolean autofocus;

  @Override
  protected String getTagName() {
    return "button";
  }

  @Override
  protected void initTag() throws JspException, IOException {
    var formCtx = getFormContext();
    form = getForm(form, formCtx);
  }

  @Override
  protected void writeAttributes(JspWriter out) throws JspException, IOException {
    if (id != null) {
      HtmlUtils.escAttribute(out, "id", id);
    }

    HtmlUtils.escAttribute(out, "type", (type != null) ? type : "button");

    if (name != null) {
      HtmlUtils.escAttribute(out, "name", name);
    }

    if (value != null) {
      HtmlUtils.escAttribute(out, "value", value);
    }

    if (disabled) {
      HtmlUtils.disabled(out);
    }

    if (form != null) {
      HtmlUtils.escAttribute(out, "form", form);
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
    return true;
  }

  @Override
  protected void writeTag(JspWriter out) throws JspException, IOException {
    out.newLine();
    super.writeTag(out);
  }

  @Override
  protected void writeBody(JspWriter out) throws JspException, IOException {
    if (labelKey != null) {
      var label = getRequestContext().res(labelKey);
      XmlEscaper.escapeContent(out, label);
    } else if (body != null) {
      body.invoke(out);
    } else {
      throw new JspException("Couldn't determine the link label.");
    }
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setLabelKey(String labelKey) {
    this.labelKey = labelKey;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setType(String type) {
    this.type = type;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setName(String name) {
    this.name = name;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setValue(String value) {
    this.value = value;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setForm(String form) {
    this.form = form;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setDisabled(boolean disabled) {
    this.disabled = disabled;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setAutofocus(boolean autofocus) {
    this.autofocus = autofocus;
  }
}
