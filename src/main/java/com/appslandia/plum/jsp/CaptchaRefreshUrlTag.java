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
import com.appslandia.common.utils.XmlEscaper;
import com.appslandia.plum.base.SimpleCaptchaManager;

import jakarta.servlet.jsp.JspException;

/**
 *
 * @author Loc Ha
 *
 */
@Tag(name = "captchaRefreshUrl", dynamicAttributes = false)
public class CaptchaRefreshUrlTag extends TagBase {

  protected boolean absUrl;
  protected boolean esc = false;

  @Override
  public void doTag() throws JspException, IOException {
    if (!this.rendered) {
      return;
    }

    var captchaId = (String) getRequest().getAttribute(SimpleCaptchaManager.PARAM_CAPTCHA_ID);
    Asserts.notNull(captchaId);

    var params = new Params().set(SimpleCaptchaManager.PARAM_CAPTCHA_ID, captchaId);
    var url = getActionParser().toActionUrl(getRequest(), "captcha", "refresh", params, this.absUrl);
    url = getResponse().encodeURL(url);

    if (this.esc) {
      XmlEscaper.escapeAttribute(this.pageContext.getOut(), url);
    } else {
      this.pageContext.getOut().write(url);
    }
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setAbsUrl(boolean absUrl) {
    this.absUrl = absUrl;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setEsc(boolean esc) {
    this.esc = esc;
  }
}
