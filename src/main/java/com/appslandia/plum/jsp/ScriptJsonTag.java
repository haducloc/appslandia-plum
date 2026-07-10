// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.jsp;

import java.io.IOException;

import com.appslandia.common.json.JsonProcessor;
import com.appslandia.common.utils.Arguments;
import com.appslandia.plum.utils.HtmlUtils;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

/**
 *
 * @author Loc Ha
 *
 */
@Tag(name = "scriptJson", dynamicAttributes = false)
public class ScriptJsonTag extends UITagBase {

  protected String id;
  protected String type;
  protected Object value;

  @Override
  protected String getTagName() {
    return "script";
  }

  @Override
  protected void initTag() throws JspException, IOException {
    Arguments.notNull(id);
  }

  @Override
  protected void writeAttributes(JspWriter out) throws JspException, IOException {
    HtmlUtils.escAttribute(out, "id", id);
    HtmlUtils.escAttribute(out, "type", (type != null) ? type : "application/json");

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
    if (value != null) {
      var jsonProcessor = ServletUtils.getAppScoped(getRequest(), JsonProcessor.class);
      jsonProcessor.write(out, value);
    }
  }

  @Override
  @Attribute(rtexprvalue = true, required = true)
  public void setId(String id) {
    this.id = id;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setType(String type) {
    this.type = type;
  }

  @Attribute(rtexprvalue = true, required = true)
  public void setValue(Object value) {
    this.value = value;
  }
}
