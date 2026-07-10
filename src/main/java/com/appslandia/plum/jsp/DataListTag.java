// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.jsp;

import java.io.IOException;

import com.appslandia.common.utils.Arguments;
import com.appslandia.plum.utils.HtmlUtils;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

/**
 *
 * @author Loc Ha
 *
 */
@Tag(name = "datalist", dynamicAttributes = false)
public class DataListTag extends UITagBase {

  protected Iterable<Object> items;
  protected String fmt;

  @Override
  protected String getTagName() {
    return "datalist";
  }

  @Override
  protected void initTag() throws JspException, IOException {
    Arguments.notNull(id);
  }

  @Override
  protected void writeAttributes(JspWriter out) throws JspException, IOException {
    HtmlUtils.escAttribute(out, "id", id);
    if (datatag != null) {
      HtmlUtils.escAttribute(out, "data-tag", datatag.toString());
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
    if (items != null) {

      for (Object item : items) {
        if (item == null) {
          continue;
        }
        out.write("<option");
        HtmlUtils.escAttribute(out, "value", formatValue(item, fmt));
        out.write("></option>");
      }
    }
  }

  @Override
  @Attribute(rtexprvalue = true, required = true)
  public void setId(String id) {
    this.id = id;
  }

  @Attribute(rtexprvalue = true, required = true)
  public void setItems(Iterable<Object> items) {
    this.items = items;
  }

  @Attribute(rtexprvalue = true, required = false, description = "Formatter ID")
  public void setFmt(String fmt) {
    this.fmt = fmt;
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
