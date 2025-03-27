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

  protected String form;
  protected String fieldName;
  protected String labelKey;
  protected boolean required;

  protected String requiredClass;
  protected String errorClass;

  protected String _for;

  @Override
  protected String getTagName() {
    return "label";
  }

  @Override
  protected void initTag() throws JspException, IOException {
    if (this._for == null) {
      this._for = HtmlUtils.toValueTagId(this.fieldName);
    }

    if (this.required) {
      String reqClass = (this.requiredClass != null) ? this.requiredClass : "l-required-label";
      this.clazz = (this.clazz == null) ? reqClass : this.clazz + " " + reqClass;
    }

    boolean isValid = !Objects.equals(this.form, this.getModelState().getForm())
        || this.getModelState().isValid(this.fieldName);

    if (!isValid) {
      String errClass = (this.errorClass != null) ? this.errorClass : "l-error-label";
      this.clazz = (this.clazz == null) ? errClass : this.clazz + " " + errClass;
    }
  }

  @Override
  protected void writeAttributes(JspWriter out) throws JspException, IOException {
    if (this.id != null)
      HtmlUtils.escAttribute(out, "id", this.id);
    HtmlUtils.escAttribute(out, "for", this._for);
    if (this.hidden)
      HtmlUtils.hidden(out);

    if (this.form != null)
      HtmlUtils.escAttribute(out, "form", this.form);

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
    return true;
  }

  @Override
  protected void writeBody(JspWriter out) throws JspException, IOException {
    if (this.labelKey != null) {
      out.write(this.getRequestContext().escXml(this.labelKey));
    } else {
      this.body.invoke(out);
    }
  }

  @Attribute(required = false, rtexprvalue = true)
  public void setFor(String _for) {
    this._for = _for;
  }

  @Attribute(required = false, rtexprvalue = false)
  public void setForm(String form) {
    this.form = form;
  }

  @Attribute(required = true, rtexprvalue = true)
  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }

  @Attribute(required = false, rtexprvalue = false)
  public void setLabelKey(String labelKey) {
    this.labelKey = labelKey;
  }

  @Attribute(required = false, rtexprvalue = true)
  public void setRequired(boolean required) {
    this.required = required;
  }

  @Attribute(required = false, rtexprvalue = false)
  public void setRequiredClass(String requiredClass) {
    this.requiredClass = requiredClass;
  }

  @Attribute(required = false, rtexprvalue = false)
  public void setErrorClass(String errorClass) {
    this.errorClass = errorClass;
  }
}
