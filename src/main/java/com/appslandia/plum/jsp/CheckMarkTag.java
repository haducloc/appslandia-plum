// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.jsp;

import java.io.IOException;

import com.appslandia.plum.utils.HtmlUtils;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

/**
 *
 * @author Loc Ha
 *
 */
@Tag(name = "checkMark", dynamicAttributes = false)
public class CheckMarkTag extends UITagBase {

  protected Boolean value;
  protected boolean heavy;

  @Override
  protected String getTagName() {
    return "span";
  }

  @Override
  protected void initTag() throws JspException, IOException {
  }

  @Override
  protected void writeTag(JspWriter out) throws JspException, IOException {
    if ((value == null) || !value) {
      return;
    }
    super.writeTag(out);
  }

  @Override
  protected void writeAttributes(JspWriter out) throws JspException, IOException {
    if (id != null) {
      HtmlUtils.escAttribute(out, "id", id);
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
  protected void writeBody(JspWriter out) throws JspException, IOException {
    var provider = getHtmlSymbolProvider();
    var symbol = provider.getHtmlSymbol(heavy ? "check-heavy" : "check");
    out.write(symbol.getCode());
  }

  @Attribute(rtexprvalue = true, required = true)
  public void setValue(Boolean value) {
    this.value = value;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setHeavy(boolean heavy) {
    this.heavy = heavy;
  }
}
