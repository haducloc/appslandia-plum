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
@Tag(name = "datalist", dynamicAttributes = false, attributes = {
  @Attribute(name = "id", type = String.class, required = true),
  @Attribute(name = "items", type = Iterable.class, required = true),
  @Attribute(name = "fmt", type = String.class, description = "Formatter ID"),
  @Attribute(name = "datatag", type = Object.class),
  @Attribute(name = "rendered", type = Boolean.class)
})
// @formatter:on
@FacesComponent(DataListTag.COMPONENT_TYPE)
public class DataListTag extends UITagBase {

  public static final String COMPONENT_TYPE = "appslandia.DataListTag";

  // @formatter:off
  private static final Set<String> TAGLIB_ATTRS = CollectionUtils.toUnmodifiableSet(
    "id",
    "items",
    "fmt",
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
    return "datalist";
  }

  @Override
  protected void initTag(FacesContext ctx) throws IOException {
    assertIdSet(ctx);
  }

  @Override
  protected void writeAttributes(FacesContext ctx, ResponseWriter out) throws IOException {
    HtmlUtils.escAttribute(out, "id", getId());
    writeAttribute(ctx, out, "datatag", "data-tag");
  }

  @Override
  protected boolean hasClosing() {
    return true;
  }

  @Override
  protected void writeTag(FacesContext ctx, ResponseWriter out) throws IOException {
    newLine(out);
    super.writeTag(ctx, out);
  }

  @Override
  protected void writeBody(FacesContext ctx, ResponseWriter out) throws IOException {
    // items
    Iterable<?> items = getIterable(ctx, "items");
    if (items != null) {
      var fmt = getString(ctx, "fmt");

      for (Object item : items) {
        if (item == null) {
          continue;
        }
        out.write("<option");
        HtmlUtils.escAttribute(out, "value", formatValue(ctx, item, fmt));
        out.write("></option>");
      }
    }
  }
}
