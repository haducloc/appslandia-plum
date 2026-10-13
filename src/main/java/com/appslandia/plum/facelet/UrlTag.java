// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.facelet;

import java.io.IOException;

import com.appslandia.common.utils.URLUtils;
import com.appslandia.common.utils.XmlEscaper;

import jakarta.faces.component.FacesComponent;
import jakarta.faces.context.FacesContext;

/**
 *
 * @author Loc Ha
 *
 */
// @formatter:off
@Tag(name = "url", attributes = {
  @Attribute(name = "base", type = String.class, required = true),
  @Attribute(name = "esc", type = Boolean.class),
  @Attribute(name = "rendered", type = Boolean.class)
})
// @formatter:on
@FacesComponent(UrlTag.COMPONENT_TYPE)
public class UrlTag extends FlComponent implements DynamicAttributes {

  public static final String COMPONENT_TYPE = "appslandia.UrlTag";

  @Override
  public void encodeAll(FacesContext ctx) throws IOException {
    if (!isRendered()) {
      return;
    }

    var base = getStringReq(ctx, "base");
    var parameters = getDynamicParameters(ctx);
    var url = URLUtils.toUrl(base, parameters);

    var out = ctx.getResponseWriter();
    var esc = getBool(ctx, "esc", true);

    if (esc) {
      XmlEscaper.escapeXml(out, url);
    } else {
      out.write(url);
    }
  }
}
