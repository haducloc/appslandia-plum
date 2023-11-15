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
import java.util.Objects;

import com.appslandia.plum.utils.HtmlUtils;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@Tag(name = "group", bodyContent = "scriptless")
public class FieldGroupTag extends UITagBase {

  protected String form;
  protected String fieldName;

  @Override
  protected String getTagName() {
    return "div";
  }

  @Override
  protected void initTag() throws JspException, IOException {
    boolean isValid = !Objects.equals(this.form, this.getModelState().getForm())
        || this.getModelState().isValid(this.fieldName);

    if (!isValid) {
      this.clazz = (this.clazz == null) ? "l-error-group" : this.clazz + " l-error-group";
    }
  }

  @Override
  protected void writeAttributes(JspWriter out) throws JspException, IOException {
    if (this.id != null)
      HtmlUtils.escAttribute(out, "id", this.id);
    if (this.hidden)
      HtmlUtils.hidden(out);

    if (this.datatag != null)
      HtmlUtils.escAttribute(out, "data-tag", this.datatag);
    if (this.clazz != null)
      HtmlUtils.escAttribute(out, "class", this.clazz);
    if (this.style != null)
      HtmlUtils.escAttribute(out, "style", this.style);
    if (this.title != null)
      HtmlUtils.escAttribute(out, "title", this.title);
  }

  @Override
  protected boolean hasBody() {
    return this.body != null;
  }

  @Override
  protected void writeBody(JspWriter out) throws JspException, IOException {
    this.body.invoke(out);
  }

  @Attribute(required = false, rtexprvalue = false)
  public void setForm(String form) {
    this.form = form;
  }

  @Attribute(required = true, rtexprvalue = true)
  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }
}
