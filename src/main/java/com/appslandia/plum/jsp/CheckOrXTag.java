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

import com.appslandia.plum.base.HtmlSymbol;
import com.appslandia.plum.base.HtmlSymbolProvider;
import com.appslandia.plum.utils.HtmlUtils;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

/**
 *
 * @author Loc Ha
 *
 */
@Tag(name = "checkOrX", dynamicAttributes = false)
public class CheckOrXTag extends UITagBase {

  protected Boolean value;
  protected boolean heavy;

  @Override
  protected String getTagName() {
    return "span";
  }

  @Override
  protected void initTag() throws JspException, IOException {
  }

  @Override
  protected void writeTag(JspWriter out) throws JspException, IOException {
    if (this.value == null) {
      return;
    }
    super.writeTag(out);
  }

  @Override
  protected void writeAttributes(JspWriter out) throws JspException, IOException {
    if (this.id != null) {
      HtmlUtils.escAttribute(out, "id", this.id);
    }

    if (this.hidden) {
      HtmlUtils.hidden(out);
    }

    if (this.datatag != null) {
      HtmlUtils.escAttribute(out, "data-tag", this.datatag.toString());
    }
    if (this.clazz != null) {
      HtmlUtils.escAttribute(out, "class", this.clazz);
    }
    if (this.style != null) {
      HtmlUtils.escAttribute(out, "style", this.style);
    }
    if (this.title != null) {
      HtmlUtils.escAttribute(out, "title", this.title);
    }
  }

  @Override
  protected boolean hasClosing() {
    return true;
  }

  @Override
  protected void writeBody(JspWriter out) throws JspException, IOException {
    var provider = ServletUtils.getAppScoped(this.pageContext.getServletContext(), HtmlSymbolProvider.class);

    HtmlSymbol symbol = null;
    if (this.value) {
      symbol = provider.getHtmlSymbol(this.heavy ? "check-heavy" : "check");
    } else {
      symbol = provider.getHtmlSymbol(this.heavy ? "xmark-heavy" : "xmark");
    }
    out.write(symbol.getCode());
  }

  @Attribute(rtexprvalue = true, required = true)
  public void setValue(Boolean value) {
    this.value = value;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setHeavy(boolean heavy) {
    this.heavy = heavy;
  }
}
