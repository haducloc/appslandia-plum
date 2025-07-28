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

import com.appslandia.plum.base.SimpleCaptchaManager;
import com.appslandia.plum.tags.TagUtils;
import com.appslandia.plum.utils.HtmlUtils;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

/**
 *
 * @author Loc Ha
 *
 */
@Tag(name = "captchaText")
public class CaptchaTextTag extends UITagBase {

  protected String maxlength;
  protected String form;
  protected boolean required;
  protected boolean autofocus;

  @Override
  protected String getTagName() {
    return "input";
  }

  @Override
  protected void initTag() throws JspException, IOException {
    var isValid = getModelState().isValid(SimpleCaptchaManager.PARAM_CAPTCHA_TEXT);

    // Class
    if (!isValid) {
      var errClass = TagUtils.CSS_FIELD_ERROR;
      this.clazz = (this.clazz == null) ? errClass : this.clazz + " " + errClass;
    }
  }

  @Override
  protected void writeAttributes(JspWriter out) throws JspException, IOException {
    HtmlUtils.escAttribute(out, "id", SimpleCaptchaManager.PARAM_CAPTCHA_TEXT);
    HtmlUtils.escAttribute(out, "type", "text");
    HtmlUtils.escAttribute(out, "name", SimpleCaptchaManager.PARAM_CAPTCHA_TEXT);

    if (this.required) {
      HtmlUtils.required(out);
    }

    if ((this.form != null) && !isInFormContext()) {
      HtmlUtils.escAttribute(out, "form", this.form);
    }

    if (this.maxlength != null) {
      HtmlUtils.escAttribute(out, "maxlength", this.maxlength);
    }

    HtmlUtils.escAttribute(out, "autocomplete", "off");
    HtmlUtils.escAttribute(out, "inputmode", "text");

    if (this.autofocus) {
      HtmlUtils.autofocus(out);
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
    return false;
  }

  @Override
  protected void writeBody(JspWriter out) throws JspException, IOException {
  }

  @Override
  public void setId(String id) {
    throw new UnsupportedOperationException();
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setMaxlength(String maxlength) {
    this.maxlength = maxlength;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setForm(String form) {
    this.form = form;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setRequired(boolean required) {
    this.required = required;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setAutofocus(boolean autofocus) {
    this.autofocus = autofocus;
  }
}
