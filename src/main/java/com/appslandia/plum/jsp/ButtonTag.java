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
import com.appslandia.plum.utils.HtmlUtils;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

/**
 *
 * @author Loc Ha
 *
 */
@Tag(name = "button", bodyContent = "scriptless")
public class ButtonTag extends UITagBase {

  protected String labelKey;
  protected String type;
  protected String name;
  protected String value;

  protected boolean disabled;
  protected boolean autofocus;
  protected String form;

  protected String _label;

  @Override
  protected String getTagName() {
    return "button";
  }

  @Override
  protected void initTag() throws JspException, IOException {
    // Form Context
    var formCtx = getFormContext();
    if (formCtx != null) {
      form = formCtx.getForm();
    }

    // Label
    if (labelKey != null) {
      _label = getRequestContext().res(labelKey);
    } else if (body != null) {
      _label = evalBody();
    } else {
      throw new JspException("Couldn't determine the button label.");
    }
  }

  @Override
  protected void writeAttributes(JspWriter out) throws JspException, IOException {
    if (id != null) {
      HtmlUtils.escAttribute(out, "id", id);
    }

    HtmlUtils.escAttribute(out, "type", (type != null) ? type : "button");

    if (name != null) {
      HtmlUtils.escAttribute(out, "name", name);
    }

    if (value != null) {
      HtmlUtils.escAttribute(out, "value", value);
    }

    HtmlUtils.escAttribute(out, "data-label", _label);

    if (disabled) {
      HtmlUtils.disabled(out);
    }

    if ((form != null) && !isInFormContext()) {
      HtmlUtils.escAttribute(out, "form", form);
    }

    if (autofocus) {
      HtmlUtils.autofocus(out);
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
    XmlEscaper.escapeContent(out, _label);
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setLabelKey(String labelKey) {
    this.labelKey = labelKey;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setType(String type) {
    this.type = type;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setName(String name) {
    this.name = name;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setValue(String value) {
    this.value = value;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setDisabled(boolean disabled) {
    this.disabled = disabled;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setAutofocus(boolean autofocus) {
    this.autofocus = autofocus;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setForm(String form) {
    this.form = form;
  }
}
