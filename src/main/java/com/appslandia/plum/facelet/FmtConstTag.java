// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.facelet;

import java.io.IOException;

import com.appslandia.common.utils.XmlEscaper;
import com.appslandia.plum.base.ConstGroupProvider;
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
@Tag(name = "fmtConst", dynamicAttributes = false, attributes = {
  @Attribute(name="fmt", type=String.class, required=true, description = "Constant Group ID"),
  @Attribute(name="value", type=Object.class, required=true),
  @Attribute(name="rendered", type=Boolean.class),
})
// @formatter:on
public class FmtConstTag extends FlTagHandler {

  public FmtConstTag(TagConfig config) {
    super(config);
  }

  @Override
  public void apply(FaceletContext ctx, UIComponent parent) throws IOException {
    if (!isRendered(ctx)) {
      return;
    }
    var value = getValue(ctx, "value");
    if (value == null) {
      return;
    }

    var constGroup = getString(ctx, "fmt");
    var provider = ServletUtils.getAppScoped(getRequest(ctx), ConstGroupProvider.class);

    var descKey = provider.getDescKey(constGroup, value);
    var desc = (descKey != null) ? getRequestContext(ctx).res(descKey) : value.toString();

    var esc = XmlEscaper.escapeXml(desc);
    parent.getChildren().add(toHtmlOuputText(esc));
  }
}
