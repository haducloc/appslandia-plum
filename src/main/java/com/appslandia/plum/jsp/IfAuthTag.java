// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.jsp;

import java.io.IOException;
import java.util.Arrays;

import com.appslandia.plum.base.AppConfig;
import com.appslandia.plum.utils.SecurityUtils;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.servlet.jsp.JspException;

/**
 *
 * @author Loc Ha
 *
 */
@Tag(name = "ifAuth", dynamicAttributes = false, bodyContent = "scriptless")
public class IfAuthTag extends TagBase {

  protected String module;
  protected String roles;

  @Override
  public void doTag() throws JspException, IOException {
    if (!rendered) {
      return;
    }
    if (module == null) {
      module = getAppConfig().getStringReq(AppConfig.CONFIG_DEFAULT_MODULE);
    }
    final var request = getRequest();

    // Principal
    var principal = ServletUtils.getPrincipal(request);
    if ((principal == null) || !module.equals(principal.getModule())) {
      return;
    }

    if (roles != null) {
      var userRoles = SecurityUtils.parseUserRoles(roles);
      if (!Arrays.stream(userRoles).anyMatch(role -> request.isUserInRole(role))) {
        return;
      }
    }
    if (body != null) {
      body.invoke(null);
    }
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setModule(String module) {
    this.module = module;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setRoles(String roles) {
    this.roles = roles;
  }
}
