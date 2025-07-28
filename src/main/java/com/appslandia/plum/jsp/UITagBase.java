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
import java.util.LinkedHashMap;
import java.util.Map;

import com.appslandia.plum.utils.HtmlUtils;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.DynamicAttributes;

/**
 *
 * @author Loc Ha
 *
 */
public abstract class UITagBase extends TagBase implements DynamicAttributes {

  // Global attributes
  protected String id;
  protected boolean hidden;
  protected Object datatag;

  protected String clazz;
  protected String style;
  protected String title;

  protected Map<String, Object> dynamicAttributes;

  @Override
  public void setDynamicAttribute(String uri, String name, Object value) throws JspException {
    if (this.dynamicAttributes == null) {
      this.dynamicAttributes = new LinkedHashMap<>();
    }
    this.dynamicAttributes.put(name, value);
  }

  protected abstract void initTag() throws JspException, IOException;

  protected void cleanUpTag() throws JspException, IOException {
  }

  protected abstract String getTagName();

  protected abstract void writeAttributes(JspWriter out) throws JspException, IOException;

  protected abstract boolean hasClosing();

  protected abstract void writeBody(JspWriter out) throws JspException, IOException;

  protected void writeTag(JspWriter out) throws JspException, IOException {
    out.write('<');
    out.write(this.getTagName());
    this.writeAttributes(out);

    if (this.dynamicAttributes != null) {
      HtmlUtils.escAttributes(out, this.dynamicAttributes, null);
    }

    if (this.hasClosing()) {
      out.write('>');
      this.writeBody(out);

      out.write("</");
      out.write(this.getTagName());
      out.write('>');

    } else {
      out.write(" />");
    }
  }

  @Override
  public void doTag() throws JspException, IOException {
    if (!this.rendered) {
      return;
    }
    try {
      this.initTag();
      this.writeTag(this.pageContext.getOut());

    } finally {
      this.cleanUpTag();
    }
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setId(String id) {
    this.id = id;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setHidden(boolean hidden) {
    this.hidden = hidden;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setDatatag(Object datatag) {
    this.datatag = datatag;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setClazz(String clazz) {
    this.clazz = clazz;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setStyle(String style) {
    this.style = style;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setTitle(String title) {
    this.title = title;
  }
}
