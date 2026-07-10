// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.jsp;

import java.io.IOException;

import com.appslandia.plum.utils.HtmlUtils;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

/**
 *
 * @author Loc Ha
 *
 */
@Tag(name = "testViewError", dynamicAttributes = false)
public class TestViewErrorTag extends UITagBase {

  @Override
  protected String getTagName() {
    return "ul";
  }

  @Override
  protected void initTag() throws JspException, IOException {
  }

  @Override
  protected void writeAttributes(JspWriter out) throws JspException, IOException {
    if (id != null) {
      HtmlUtils.escAttribute(out, "id", id);
    }

    if (hidden) {
      HtmlUtils.hidden(out);
    }

    if (datatag != null) {
      HtmlUtils.escAttribute(out, "data-tag", datatag.toString());
    }
    if (clazz != null) {
      HtmlUtils.escAttribute(out, "class", clazz);
    }
    if (style != null) {
      HtmlUtils.escAttribute(out, "style", style);
    }
    if (title != null) {
      HtmlUtils.escAttribute(out, "title", title);
    }
  }

  @Override
  protected boolean hasClosing() {
    return true;
  }

  @Override
  protected void writeBody(JspWriter out) throws JspException, IOException {
    var viewError = getRequest().getParameter("__test_view_error");

    for (var i = 1; i <= 10; i++) {
      out.println("<li>This is a test line " + i + "</li>");

      if (i == 5) {
        if ("error".equals(viewError)) {
          throw new JspException("This is a test view error: __test_view_error=" + viewError);
        }

        if ("flush_error".equals(viewError)) {
          out.flush();
          throw new JspException("This is a test view error: __test_view_error=" + viewError);
        }
      }
    }
  }
}
