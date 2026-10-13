// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.facelet;

import java.io.IOException;
import java.util.Arrays;

import com.appslandia.common.utils.Arguments;
import com.appslandia.plum.utils.SecurityUtils;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.faces.component.UIComponent;
import jakarta.faces.view.facelets.FaceletContext;
import jakarta.faces.view.facelets.TagConfig;

/**
 *
 * @author Loc Ha
 *
 */
// @formatter:off
@Tag(name = "ifAuth", bodyContent = true, dynamicAttributes = false, attributes = {
  @Attribute(name="module", type=String.class),
  @Attribute(name="roles", type=String.class),
  @Attribute(name="rendered", type=Boolean.class)
})
// @formatter:on
public class IfAuthTag extends FlTagHandler {

  public IfAuthTag(TagConfig config) {
    super(config);
  }

  @Override
  public void apply(FaceletContext ctx, UIComponent parent) throws IOException {
    if (!isRendered(ctx)) {
      return;
    }

    var module = getString(ctx, "module");
    var modules = getAppConfig(ctx).getEnableModules();

    if (module == null && modules.size() == 1) {
      module = modules.iterator().next();
    }
    Arguments.isTrue(module != null && modules.contains(module), "The module is invalid or not supported.");

    final var request = getRequest(ctx);

    // Principal
    var principal = ServletUtils.getPrincipal(request);
    if ((principal == null) || !module.equals(principal.getModule())) {
      return;
    }

    var roles = getString(ctx, "roles");
    if (roles != null) {
      var userRoles = SecurityUtils.parseUserRoles(roles);
      if (!Arrays.stream(userRoles).anyMatch(role -> request.isUserInRole(role))) {
        return;
      }
    }
    nextHandler.apply(ctx, parent);
  }
}
