// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.facelet;

import java.io.IOException;

import com.appslandia.common.utils.XmlEscaper;
import com.appslandia.plum.base.GroupFormatProvider;
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
@Tag(name = "fmtString", dynamicAttributes = false, attributes = {
  @Attribute(name = "fmt", type = String.class, required = true, description = "GroupFormat ID"),
  @Attribute(name = "value", type = String.class, required = true),
  @Attribute(name = "rendered", type = Boolean.class)
})
// @formatter:on
public class FmtStringTag extends FlTagHandler {

  public FmtStringTag(TagConfig config) {
    super(config);
  }

  @Override
  public void apply(FaceletContext ctx, UIComponent parent) throws IOException {
    if (!isRendered(ctx)) {
      return;
    }
    var value = getString(ctx, "value");
    if (value == null) {
      return;
    }

    var fmtName = getStringReq(ctx, "fmt");
    var groupFormatProvider = ServletUtils.getAppScoped(getRequest(ctx), GroupFormatProvider.class);

    var format = groupFormatProvider.getGroupFormat(fmtName);
    var formattedValue = format.format(value);

    var esc = XmlEscaper.escapeXml(formattedValue);
    parent.getChildren().add(toHtmlOuputText(esc));
  }
}
