// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.jsp;

import java.io.IOException;

import com.appslandia.common.utils.XmlEscaper;
import com.appslandia.plum.base.AppConfig;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.servlet.jsp.JspException;

/**
 *
 * @author Loc Ha
 *
 */
@Tag(name = "userName", dynamicAttributes = false)
public class UserNameTag extends TagBase {

  protected String module;
  protected String guestKey;

  @Override
  public void doTag() throws JspException, IOException {
    if (!rendered) {
      return;
    }
    if (module == null) {
      module = getAppConfig().getStringReq(AppConfig.CONFIG_DEFAULT_MODULE);
    }
    String content = null;

    // Principal
    var principal = ServletUtils.getPrincipal(getRequest());
    if (principal == null) {
      content = (guestKey != null) ? getRequestContext().res(guestKey) : "Guest";

    } else if (!module.equals(principal.getModule())) {
      content = principal.getDisplayName() + " (!)";
    } else {
      content = principal.getDisplayName();
    }

    XmlEscaper.escapeXml(pageContext.getOut(), content);
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setModule(String module) {
    this.module = module;
  }

  @Attribute(rtexprvalue = false, required = false)
  public void setGuestKey(String guestKey) {
    this.guestKey = guestKey;
  }
}
