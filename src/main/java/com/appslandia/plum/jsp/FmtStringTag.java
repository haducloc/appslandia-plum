// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.jsp;

import java.io.IOException;

import com.appslandia.common.utils.XmlEscaper;
import com.appslandia.plum.base.GroupFormatProvider;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.servlet.jsp.JspException;

/**
 *
 * @author Loc Ha
 *
 */
@Tag(name = "fmtString", dynamicAttributes = false)
public class FmtStringTag extends TagBase {

  protected String fmt;
  protected String value;

  @Override
  public void doTag() throws JspException, IOException {
    if (!rendered || value == null) {
      return;
    }

    var provider = ServletUtils.getAppScoped(pageContext.getServletContext(), GroupFormatProvider.class);
    var format = provider.getGroupFormat(fmt);

    var formattedValue = format.format(value);
    XmlEscaper.escapeXml(pageContext.getOut(), formattedValue);
  }

  @Attribute(rtexprvalue = true, required = true, description = "GroupFormat ID")
  public void setFmt(String fmt) {
    this.fmt = fmt;
  }

  @Attribute(rtexprvalue = true, required = true)
  public void setValue(String value) {
    this.value = value;
  }
}
