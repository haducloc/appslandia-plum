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

import com.appslandia.common.utils.XmlEscaper;

import jakarta.servlet.jsp.JspException;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@Tag(name = "out", dynamicAttributes = false, bodyContent = "empty")
public class OutTag extends TagBase {

  protected Object value;
  protected boolean render = true;

  @Override
  public void doTag() throws JspException, IOException {
    if (this.render && this.value != null) {

      XmlEscaper.escapeXml(this.pageContext.getOut(), this.value.toString());
    }
  }

  @Attribute(required = true, rtexprvalue = true)
  public void setValue(Object value) {
    this.value = value;
  }

  @Attribute(required = false, rtexprvalue = true)
  public void setRender(boolean render) {
    this.render = render;
  }
}
