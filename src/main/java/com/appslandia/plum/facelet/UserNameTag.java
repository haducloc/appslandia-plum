// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.facelet;

import java.io.IOException;

import com.appslandia.common.utils.XmlEscaper;
import com.appslandia.plum.base.AppConfig;
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
  @Attribute(name="guestKey", type=String.class),
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
    if (module == null) {
      module = getAppConfig(ctx).getStringReq(AppConfig.CONFIG_DEFAULT_MODULE);
    }
    String content = null;

    // Principal
    var principal = ServletUtils.getPrincipal(getRequest(ctx));
    if (principal == null) {

      var guestKey = getString(ctx, "guestKey");
      content = (guestKey != null) ? getRequestContext(ctx).res(guestKey) : "Guest";

    } else if (!module.equals(principal.getModule())) {
      content = principal.getDisplayName() + " (!)";
    } else {
      content = principal.getDisplayName();
    }

    var rawValue = XmlEscaper.escapeXml(content);
    parent.getChildren().add(toHtmlOuputText(rawValue));
  }
}
