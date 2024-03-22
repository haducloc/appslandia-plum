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

import com.appslandia.plum.base.SimpleCaptchaManager;
import com.appslandia.plum.utils.HtmlUtils;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@Tag(name = "captchaWords")
public class CaptchaWordsTag extends UITagBase {

  protected String form;
  protected String maxlength;
  protected String placeholder;
  protected boolean required;
  protected String errorClass;

  @Override
  protected String getTagName() {
    return "input";
  }

  @Override
  protected void initTag() throws JspException, IOException {
    boolean isValid = !Objects.equals(this.form, getModelState().getForm())
        || getModelState().isValid(SimpleCaptchaManager.PARAM_CAPTCHA_WORDS);

    if (!isValid) {
      String errClass = (this.errorClass != null) ? this.errorClass : "l-error-field";
      this.clazz = (this.clazz == null) ? errClass : this.clazz + " " + errClass;
    }
  }

  @Override
  protected void writeAttributes(JspWriter out) throws JspException, IOException {
    HtmlUtils.escAttribute(out, "id", SimpleCaptchaManager.PARAM_CAPTCHA_WORDS);
    HtmlUtils.escAttribute(out, "type", "text");
    HtmlUtils.escAttribute(out, "name", SimpleCaptchaManager.PARAM_CAPTCHA_WORDS);

    if (this.maxlength != null)
      HtmlUtils.escAttribute(out, "maxlength", this.maxlength);
    if (this.placeholder != null)
      HtmlUtils.escAttribute(out, "placeholder", this.placeholder);

    if (this.required)
      HtmlUtils.required(out);
    HtmlUtils.escAttribute(out, "autocomplete", "off");

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

  @Attribute(required = false, rtexprvalue = false)
  public void setForm(String form) {
    this.form = form;
  }

  @Attribute(required = false, rtexprvalue = false)
  public void setMaxlength(String maxlength) {
    this.maxlength = maxlength;
  }

  @Attribute(required = false, rtexprvalue = true)
  public void setPlaceholder(String placeholder) {
    this.placeholder = placeholder;
  }

  @Attribute(required = false, rtexprvalue = false)
  public void setRequired(boolean required) {
    this.required = required;
  }

  @Attribute(required = false, rtexprvalue = false)
  public void setErrorClass(String errorClass) {
    this.errorClass = errorClass;
  }
}
