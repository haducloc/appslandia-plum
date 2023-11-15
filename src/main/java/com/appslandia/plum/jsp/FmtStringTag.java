// The MIT License (MIT)
// Copyright © 2015 AppsLandia. All rights reserved.

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

import com.appslandia.common.base.StringFormat;
import com.appslandia.common.utils.XmlEscaper;
import com.appslandia.plum.base.StringFormatProvider;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.servlet.jsp.JspException;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@Tag(name = "fmtString", dynamicAttributes = false)
public class FmtStringTag extends TagBase {

  protected String format;
  protected String value;

  @Override
  public void doTag() throws JspException, IOException {
    if (this.value == null) {
      return;
    }
    StringFormatProvider groupFormatProvider = ServletUtils.getAppScoped(this.pageContext.getServletContext(),
        StringFormatProvider.class);
    StringFormat groupFormat = groupFormatProvider.getStringFormat(this.format);

    XmlEscaper.escapeXml(this.pageContext.getOut(), groupFormat.format(this.value));
  }

  @Attribute(required = true, rtexprvalue = false)
  public void setFormat(String format) {
    this.format = format;
  }

  @Attribute(required = true, rtexprvalue = true)
  public void setValue(String value) {
    this.value = value;
  }
}
