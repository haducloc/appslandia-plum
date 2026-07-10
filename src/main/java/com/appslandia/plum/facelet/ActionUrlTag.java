// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.facelet;

import java.io.IOException;

import com.appslandia.common.utils.XmlEscaper;

import jakarta.faces.component.FacesComponent;
import jakarta.faces.context.FacesContext;

/**
 *
 * @author Loc Ha
 *
 */
// @formatter:off
@Tag(name = "actionUrl", attributes = {
  @Attribute(name = "controller", type = String.class),
  @Attribute(name = "action", type = String.class, required = true),
  @Attribute(name = "absUrl", type = Boolean.class),
  @Attribute(name = "esc", type = Boolean.class),
  @Attribute(name = "rendered", type = Boolean.class)
})
// @formatter:on
@FacesComponent(ActionUrlTag.COMPONENT_TYPE)
public class ActionUrlTag extends FlComponent implements DynamicAttributes {

  public static final String COMPONENT_TYPE = "appslandia.ActionUrlTag";

  @Override
  public void encodeAll(FacesContext ctx) throws IOException {
    if (!isRendered()) {
      return;
    }

    // action/controller
    var action = getStringReq(ctx, "action");
    var controller = getString(ctx, "controller");
    if (controller == null) {
      controller = getRequestContext(ctx).getActionDesc().getController();
    }

    // URL
    var absUrl = getBool(ctx, "absUrl", false);
    var parameters = getDynamicParameters(ctx);
    var url = getActionParser(ctx).toActionUrl(getRequest(ctx), controller, action, parameters, absUrl);
    url = getResponse(ctx).encodeURL(url);

    var out = ctx.getResponseWriter();
    var esc = getBool(ctx, "esc", true);

    if (esc) {
      XmlEscaper.escapeXml(out, url);
    } else {
      out.write(url);
    }
  }
}
