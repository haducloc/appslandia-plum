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
import com.appslandia.plum.utils.HtmlUtils;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

/**
 *
 * @author Loc Ha
 *
 */
@Tag(name = "captchaId", dynamicAttributes = false)
public class CaptchaIdTag extends UITagBase {

  protected String form;
  protected Object datatag;
  protected String _captchaId;

  @Override
  protected String getTagName() {
    return "input";
  }

  @Override
  protected void initTag() throws JspException, IOException {
    var captchaManager = ServletUtils.getAppScoped(getRequest(), SimpleCaptchaManager.class);
    this._captchaId = captchaManager.initCaptcha(getRequest());
  }

  @Override
  protected void writeAttributes(JspWriter out) throws JspException, IOException {
    HtmlUtils.escAttribute(out, "id", SimpleCaptchaManager.PARAM_CAPTCHA_ID);
    HtmlUtils.escAttribute(out, "type", "hidden");
    HtmlUtils.escAttribute(out, "name", SimpleCaptchaManager.PARAM_CAPTCHA_ID);

    HtmlUtils.escAttribute(out, "value", this._captchaId);
    HtmlUtils.required(out);

    if ((this.form != null) && !isInFormContext()) {
      HtmlUtils.escAttribute(out, "form", this.form);
    }

    if (this.datatag != null) {
      HtmlUtils.escAttribute(out, "data-tag", this.datatag.toString());
    }
  }

  @Override
  protected boolean hasClosing() {
    return false;
  }

  @Override
  protected void writeBody(JspWriter out) throws JspException, IOException {
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setForm(String form) {
    this.form = form;
  }

  @Override
  @Attribute(rtexprvalue = true, required = false)
  public void setDatatag(Object datatag) {
    this.datatag = datatag;
  }

  @Override
  public void setId(String id) {
    throw new UnsupportedOperationException();
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
