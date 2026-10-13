// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.jsp;

import java.io.IOException;

import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.XmlEscaper;
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

  @Override
  public void doTag() throws JspException, IOException {
    if (!rendered) {
      return;
    }

    var modules = getAppConfig().getEnableModules();
    if (module == null && modules.size() == 1) {
      module = modules.iterator().next();
    }
    Arguments.isTrue(module != null && modules.contains(module), "The module is invalid or not supported.");

    // content
    String content = null;
    var principal = ServletUtils.getPrincipal(getRequest());

    if (principal == null) {
      content = getRequestContext().res("user.anonymous_name");

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
}
