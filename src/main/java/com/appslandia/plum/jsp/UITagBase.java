// Licensed under the MIT License.
// See LICENSE file in the project root for details.

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
    if (dynamicAttributes == null) {
      dynamicAttributes = new LinkedHashMap<>();
    }
    dynamicAttributes.put(name, value);
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
    out.write(getTagName());
    writeAttributes(out);

    if (dynamicAttributes != null) {
      HtmlUtils.escAttributes(out, dynamicAttributes, null);
    }

    if (hasClosing()) {
      out.write('>');
      writeBody(out);

      out.write("</");
      out.write(getTagName());
      out.write('>');

    } else {
      out.write(" />");
    }
  }

  @Override
  public void doTag() throws JspException, IOException {
    if (!rendered) {
      return;
    }
    try {
      initTag();
      writeTag(pageContext.getOut());

    } finally {
      cleanUpTag();
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
