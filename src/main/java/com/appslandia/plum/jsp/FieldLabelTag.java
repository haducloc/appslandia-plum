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
import java.util.Objects;

import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.XmlEscaper;
import com.appslandia.plum.tags.TagUtils;
import com.appslandia.plum.utils.HtmlUtils;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

/**
 *
 * @author Loc Ha
 *
 */
@Tag(name = "label", bodyContent = "scriptless")
public class FieldLabelTag extends UITagBase {

  protected String path;
  protected String form;
  protected String labelKey;
  protected boolean skipFor = false;
  protected boolean required;
  protected String _for;

  @Override
  protected String getTagName() {
    return "label";
  }

  @Override
  protected void initTag() throws JspException, IOException {
    Arguments.notNull(path);

    // Form Context
    var formCtx = getFormContext();
    if (formCtx != null) {
      form = formCtx.getForm();
    }

    if (!skipFor) {
      _for = TagUtils.toTagId(path);
    }

    // Required Class
    var clazz = this.clazz;
    if (required) {
      var reqClass = TagUtils.CSS_LABEL_REQUIRED;
      clazz = (clazz == null) ? reqClass : clazz + " " + reqClass;
    }

    var isValid = !Objects.equals(form, getModelState().getForm()) || getModelState().isValid(path);

    // Error Class
    if (!isValid) {
      var errClass = TagUtils.CSS_LABEL_ERROR;
      clazz = (clazz == null) ? errClass : clazz + " " + errClass;
    }
    this.clazz = clazz;
  }

  @Override
  protected void writeAttributes(JspWriter out) throws JspException, IOException {
    if (id != null) {
      HtmlUtils.escAttribute(out, "id", id);
    }

    if (_for != null) {
      HtmlUtils.escAttribute(out, "for", _for);
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
    if (labelKey != null) {
      var label = getRequestContext().res(labelKey);
      XmlEscaper.escapeContent(out, label);
    } else if (body != null) {
      body.invoke(out);
    } else {
      throw new JspException("Couldn't determine the label content.");
    }
  }

  @Attribute(rtexprvalue = true, required = true)
  public void setPath(String path) {
    this.path = path;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setForm(String form) {
    this.form = form;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setLabelKey(String labelKey) {
    this.labelKey = labelKey;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setSkipFor(boolean skipFor) {
    this.skipFor = skipFor;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setRequired(boolean required) {
    this.required = required;
  }
}
