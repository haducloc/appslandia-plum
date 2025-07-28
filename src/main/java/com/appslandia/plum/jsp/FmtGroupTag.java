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

import com.appslandia.common.utils.XmlEscaper;
import com.appslandia.plum.base.GroupFormatProvider;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.servlet.jsp.JspException;

/**
 *
 * @author Loc Ha
 *
 */
@Tag(name = "fmtGroup", dynamicAttributes = false)
public class FmtGroupTag extends TagBase {

  protected String name;
  protected String value;

  @Override
  public void doTag() throws JspException, IOException {
    if (!this.rendered || this.value == null) {
      return;
    }
    var groupFormatProvider = ServletUtils.getAppScoped(this.pageContext.getServletContext(),
        GroupFormatProvider.class);
    var groupFormat = groupFormatProvider.getGroupFormat(this.name);

    var formattedValue = groupFormat.format(this.value);
    XmlEscaper.escapeAttribute(this.pageContext.getOut(), formattedValue);
  }

  @Attribute(rtexprvalue = true, required = true)
  public void setName(String name) {
    this.name = name;
  }

  @Attribute(rtexprvalue = true, required = true)
  public void setValue(String value) {
    this.value = value;
  }
}
