// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.facelet;

import java.io.IOException;

import com.appslandia.plum.utils.HtmlUtils;

import jakarta.faces.FacesException;
import jakarta.faces.component.FacesComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;

/**
 *
 * @author Loc Ha
 *
 */
// @formatter:off
@Tag(name = "testViewError", dynamicAttributes = false, attributes = {
  @Attribute(name = "id", type = String.class),
  @Attribute(name = "clazz", type = String.class),
  @Attribute(name = "style", type = String.class),
  @Attribute(name = "title", type = String.class),
  @Attribute(name = "hidden", type = Boolean.class),
  @Attribute(name = "datatag", type = Object.class),
  @Attribute(name = "rendered", type = Boolean.class)
})
// @formatter:on
@FacesComponent(TestViewErrorTag.COMPONENT_TYPE)
public class TestViewErrorTag extends UITagBase {

  public static final String COMPONENT_TYPE = "appslandia.TestViewErrorTag";

  @Override
  protected String getTagName() {
    return "ul";
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
    var viewError = getRequest(ctx).getParameter("__test_view_error");
    for (var i = 1; i <= 10; i++) {
      out.write("<li>This is a test line " + i + "</li>");

      if (i == 5) {
        if ("error".equals(viewError)) {
          throw new FacesException("This is a test view error: __test_view_error=" + viewError);
        }

        if ("flush_error".equals(viewError)) {
          out.flush();
          throw new FacesException("This is a test view error: __test_view_error=" + viewError);
        }
      }
    }
  }
}
