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

import com.appslandia.common.utils.Arguments;
import com.appslandia.plum.utils.HtmlUtils;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

/**
 *
 * @author Loc Ha
 *
 */
@Tag(name = "datalist", dynamicAttributes = false)
public class DataListTag extends UITagBase {

  protected Iterable<Object> items;
  protected String formatter;

  @Override
  protected String getTagName() {
    return "datalist";
  }

  @Override
  protected void initTag() throws JspException, IOException {
    Arguments.notNull(this.id);
  }

  @Override
  protected void writeAttributes(JspWriter out) throws JspException, IOException {
    HtmlUtils.escAttribute(out, "id", this.id);
    if (this.datatag != null) {
      HtmlUtils.escAttribute(out, "data-tag", this.datatag.toString());
    }
  }

  @Override
  protected boolean hasClosing() {
    return true;
  }

  @Override
  protected void writeBody(JspWriter out) throws JspException, IOException {
    if (this.items != null) {
      for (Object item : this.items) {
        if (item == null) {
          continue;
        }
        out.write("<option");
        HtmlUtils.escAttribute(out, "value", formatInputValue(item, this.formatter));
        out.write("></option>");
      }
    }
  }

  @Override
  @Attribute(rtexprvalue = true, required = true)
  public void setId(String id) {
    this.id = id;
  }

  @Attribute(rtexprvalue = true, required = true)
  public void setItems(Iterable<Object> items) {
    this.items = items;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setFormatter(String formatter) {
    this.formatter = formatter;
  }

  @Override
  public void setHidden(boolean hidden) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setClazz(String clazz) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setStyle(String style) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setTitle(String title) {
    throw new UnsupportedOperationException();
  }
}
