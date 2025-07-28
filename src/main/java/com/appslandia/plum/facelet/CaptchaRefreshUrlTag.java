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

package com.appslandia.plum.facelet;

import java.io.IOException;

import com.appslandia.common.base.Params;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.XmlEscaper;
import com.appslandia.plum.base.SimpleCaptchaManager;

import jakarta.faces.component.FacesComponent;
import jakarta.faces.context.FacesContext;

/**
 *
 * @author Loc Ha
 *
 */
// @formatter:off
@Tag(name = "captchaRefreshUrl", dynamicAttributes = false, attributes = {
  @Attribute(name = "absUrl", type = Boolean.class),
  @Attribute(name = "esc", type = Boolean.class),
  @Attribute(name = "rendered", type = Boolean.class)
})
// @formatter:on
@FacesComponent(CaptchaRefreshUrlTag.COMPONENT_TYPE)
public class CaptchaRefreshUrlTag extends FlComponent {

  public static final String COMPONENT_TYPE = "appslandia.CaptchaRefreshUrlTag";

  @Override
  public void encodeAll(FacesContext ctx) throws IOException {
    if (!this.isRendered()) {
      return;
    }

    var captchaId = (String) getRequest(ctx).getAttribute(SimpleCaptchaManager.PARAM_CAPTCHA_ID);
    Asserts.notNull(captchaId);

    var params = new Params().set(SimpleCaptchaManager.PARAM_CAPTCHA_ID, captchaId);
    var absUrl = getBool(ctx, "absUrl", false);

    var url = getActionParser(ctx).toActionUrl(getRequest(ctx), "captcha", "refresh", params, absUrl);
    url = getResponse(ctx).encodeURL(url);

    var out = ctx.getResponseWriter();
    var esc = getBool(ctx, "esc", false);

    if (esc) {
      XmlEscaper.escapeAttribute(out, url);
    } else {
      out.write(url);
    }
  }
}
