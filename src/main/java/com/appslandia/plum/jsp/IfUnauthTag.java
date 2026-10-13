// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.jsp;

import java.io.IOException;

import com.appslandia.common.utils.Arguments;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.servlet.jsp.JspException;

/**
 *
 * @author Loc Ha
 *
 */
@Tag(name = "ifUnauth", dynamicAttributes = false, bodyContent = "scriptless")
public class IfUnauthTag extends TagBase {

  protected String module;

  @Override
  public void doTag() throws JspException, IOException {
    if (!rendered || body == null) {
      return;
    }

    var modules = getAppConfig().getEnableModules();
    if (module == null && modules.size() == 1) {
      module = modules.iterator().next();
    }
    Arguments.isTrue(module != null && modules.contains(module), "The module is invalid or not supported.");

    // Principal
    var principal = ServletUtils.getPrincipal(getRequest());
    if ((principal != null) && module.equals(principal.getModule())) {
      return;
    }

    // Invoke body
    body.invoke(null);
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setModule(String module) {
    this.module = module;
  }
}
