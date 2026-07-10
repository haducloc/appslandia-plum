// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.jsp;

import java.io.IOException;

import com.appslandia.plum.base.AppConfig;
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
    if (!rendered) {
      return;
    }
    if (module == null) {
      module = getAppConfig().getStringReq(AppConfig.CONFIG_DEFAULT_MODULE);
    }

    // Principal
    var principal = ServletUtils.getPrincipal(getRequest());
    if ((principal != null) && module.equals(principal.getModule())) {
      return;
    }
    if (body != null) {
      body.invoke(null);
    }
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setModule(String module) {
    this.module = module;
  }
}
