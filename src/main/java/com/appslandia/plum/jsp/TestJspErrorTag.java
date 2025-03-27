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

package com.appslandia.plum.jsp;

import java.io.IOException;

import com.appslandia.plum.base.AppConfig;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

/**
 *
 * @author Loc Ha
 *
 */
@Tag(name = "testJspError", dynamicAttributes = false)
public class TestJspErrorTag extends TagBase {

  @Override
  public void doTag() throws JspException, IOException {
    AppConfig appConfig = ServletUtils.getAppScoped(this.pageContext.getServletContext(), AppConfig.class);
    if (!appConfig.isEnableDebug()) {
      return;
    }

    String jspError = getRequest().getParameter("__test_jsp_error");
    JspWriter out = this.pageContext.getOut();
    out.println("<ul>");

    for (int i = 1; i <= 10; i++) {
      out.println("<li>This is a test line " + i + "</li>");

      if (i == 5) {
        if ("error".equals(jspError)) {
          throw new JspException("This is a test JSP error: __test_jsp_error=" + jspError);
        }

        if ("flush_error".equals(jspError)) {
          out.flush();
          throw new JspException("This is a test JSP error: __test_jsp_error=" + jspError);
        }
      }
    }
    out.println("</ul>");
  }
}
