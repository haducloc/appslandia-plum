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

import com.appslandia.common.base.Params;
import com.appslandia.common.utils.Asserts;
import com.appslandia.plum.base.SimpleCaptchaManager;
import com.appslandia.plum.utils.HtmlUtils;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

/**
 *
 * @author Loc Ha
 *
 */
@Tag(name = "captchaImg")
public class CaptchaImgTag extends UITagBase {

  protected String width;
  protected String height;
  protected String alt;

  protected String loading;
  protected String decoding;
  protected String fetchpriority;

  protected String _url;

  @Override
  protected String getTagName() {
    return "img";
  }

  @Override
  protected void initTag() throws JspException, IOException {
    var captchaId = (String) getRequest().getAttribute(SimpleCaptchaManager.PARAM_CAPTCHA_ID);
    Asserts.notNull(captchaId);

    var params = new Params().set(SimpleCaptchaManager.PARAM_CAPTCHA_ID, captchaId);
    var url = getActionParser().toActionUrl(getRequest(), "captcha", "img", params, false);
    this._url = getResponse().encodeURL(url);
  }

  @Override
  protected void writeAttributes(JspWriter out) throws JspException, IOException {
    if (this.id != null) {
      HtmlUtils.escAttribute(out, "id", this.id);
    }
    HtmlUtils.escAttribute(out, "src", this._url);

    if (this.width != null) {
      HtmlUtils.escAttribute(out, "width", this.width);
    }

    if (this.height != null) {
      HtmlUtils.escAttribute(out, "height", this.height);
    }

    HtmlUtils.escAttribute(out, "alt", (this.alt != null) ? this.alt : "CAPTCHA");

    if (this.loading != null) {
      HtmlUtils.escAttribute(out, "loading", this.loading);
    }

    if (this.decoding != null) {
      HtmlUtils.escAttribute(out, "decoding", this.decoding);
    }

    if (this.fetchpriority != null) {
      HtmlUtils.escAttribute(out, "fetchpriority", this.fetchpriority);
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

  @Attribute(rtexprvalue = true, required = false)
  public void setWidth(String width) {
    this.width = width;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setHeight(String height) {
    this.height = height;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setAlt(String alt) {
    this.alt = alt;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setLoading(String loading) {
    this.loading = loading;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setDecoding(String decoding) {
    this.decoding = decoding;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setFetchpriority(String fetchpriority) {
    this.fetchpriority = fetchpriority;
  }
}
