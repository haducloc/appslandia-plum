// The MIT License (MIT)
// Copyright © 2015 Loc Ha

// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:

// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.

// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

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
  @Attribute(name = "formatter", type = String.class),
  @Attribute(name = "datatag", type = Object.class),
  @Attribute(name = "rendered", type = Boolean.class)
})
// @formatter:on
@FacesComponent(DataListTag.COMPONENT_TYPE)
public class DataListTag extends UITagBase {

  public static final String COMPONENT_TYPE = "appslandia.DataListTag";

  // @formatter:off
  private static final Set<String> TAGLIB_ATTRS = CollectionUtils.unmodifiableSet(
    "id",
    "items",
    "formatter",
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
    writeIdAttribute(ctx, out);
    writeAttribute(ctx, out, "datatag", "data-tag");
  }

  @Override
  protected boolean hasClosing() {
    return true;
  }

  @Override
  protected void writeBody(FacesContext ctx, ResponseWriter out) throws IOException {
    // items
    Iterable<?> items = getIterable(ctx, "items");
    if (items != null) {
      var formatter = getString(ctx, "formatter");
      for (Object item : items) {
        if (item == null) {
          continue;
        }
        out.write("<option");
        HtmlUtils.escAttribute(out, "value", formatInputValue(ctx, item, formatter));
        out.write("></option>");
      }
    }
  }
}
