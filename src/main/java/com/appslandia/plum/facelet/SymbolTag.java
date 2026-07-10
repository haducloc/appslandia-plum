// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.facelet;

import java.io.IOException;
import java.util.Set;

import com.appslandia.common.utils.CollectionUtils;
import com.appslandia.plum.utils.HtmlUtils;

import jakarta.faces.component.FacesComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;

/**
 *
 * @author Loc Ha
 *
 */
// @formatter:off
@Tag(name = "sym", dynamicAttributes = false, attributes = {
  @Attribute(name = "name", type = String.class, required = true),
  @Attribute(name = "times", type = Integer.class, required = false),

  @Attribute(name = "id", type = String.class),
  @Attribute(name = "clazz", type = String.class),
  @Attribute(name = "style", type = String.class),
  @Attribute(name = "title", type = String.class),
  @Attribute(name = "hidden", type = Boolean.class),
  @Attribute(name = "datatag", type = Object.class),
  @Attribute(name = "rendered", type = Boolean.class)
})
// @formatter:on
@FacesComponent(SymbolTag.COMPONENT_TYPE)
public class SymbolTag extends UITagBase {

  public static final String COMPONENT_TYPE = "appslandia.SymbolTag";

  // @formatter:off
  private static final Set<String> TAGLIB_ATTRS = CollectionUtils.toUnmodifiableSet(
    "name",
    "times",

    "id",
    "clazz",
    "style",
    "title",
    "hidden",
    "datatag",
    "rendered"
  );
  // @formatter:on

  @Override
  public Set<String> getTaglibAttributes() {
    return TAGLIB_ATTRS;
  }

  @Override
  protected String getTagName() {
    return "span";
  }

  @Override
  protected void initTag(FacesContext ctx) throws IOException {
  }

  @Override
  protected void writeAttributes(FacesContext ctx, ResponseWriter out) throws IOException {
    var id = getId();
    if (!isGeneratedId(id)) {
      HtmlUtils.escAttribute(out, "id", id);
    }

    if (getBool(ctx, "hidden", false)) {
      HtmlUtils.hidden(out);
    }

    writeAttribute(ctx, out, "datatag", "data-tag");
    writeAttribute(ctx, out, "clazz", "class");
    writeAttribute(ctx, out, "style");
    writeAttribute(ctx, out, "title");
  }

  @Override
  protected boolean hasClosing() {
    return true;
  }

  @Override
  protected void writeBody(FacesContext ctx, ResponseWriter out) throws IOException {
    var name = getStringReq(ctx, "name");
    var times = getInt(ctx, "times", 1);

    var provider = getHtmlSymbolProvider(ctx);
    var symbol = provider.getHtmlSymbol(name);

    for (var i = 0; i < times; i++) {
      out.write(symbol.getCode());
    }
  }
}
