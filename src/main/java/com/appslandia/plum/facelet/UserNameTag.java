// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.facelet;

import java.io.IOException;

import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.XmlEscaper;
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
@Tag(name = "userName", dynamicAttributes = false, attributes = {
  @Attribute(name="module", type=String.class),
  @Attribute(name="rendered", type=Boolean.class),
})
// @formatter:on
public class UserNameTag extends FlTagHandler {

  public UserNameTag(TagConfig config) {
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

    // content
    String content = null;
    var principal = ServletUtils.getPrincipal(getRequest(ctx));

    if (principal == null) {
      content = getRequestContext(ctx).res("user.anonymous_name");

    } else if (!module.equals(principal.getModule())) {
      content = principal.getDisplayName() + " (!)";
    } else {
      content = principal.getDisplayName();
    }

    var rawValue = XmlEscaper.escapeXml(content);
    parent.getChildren().add(toHtmlOuputText(rawValue));
  }
}
