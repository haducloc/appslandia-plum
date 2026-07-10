// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.jsp;

import java.io.IOException;

import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.XmlEscaper;
import com.appslandia.plum.base.ConstGroupProvider;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.servlet.jsp.JspException;

/**
 *
 * @author Loc Ha
 *
 */
@Tag(name = "fmtConst", dynamicAttributes = false)
public class FmtConstTag extends TagBase {

  protected String fmt;
  protected Object value;

  @Override
  public void doTag() throws JspException, IOException {
    Arguments.notNull(fmt);

    if (!rendered || value == null) {
      return;
    }

    var provider = ServletUtils.getAppScoped(pageContext.getServletContext(), ConstGroupProvider.class);
    var descKey = provider.getDescKey(fmt, value);

    var content = (descKey != null) ? getRequestContext().res(descKey) : value.toString();
    XmlEscaper.escapeXml(pageContext.getOut(), content);
  }

  @Attribute(rtexprvalue = true, required = true, description = "Constant Group ID")
  public void setFmt(String fmt) {
    this.fmt = fmt;
  }

  @Attribute(rtexprvalue = true, required = true)
  public void setValue(Object value) {
    this.value = value;
  }
}
