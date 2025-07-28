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

package com.appslandia.plum.defaults;

import java.io.OutputStream;

import javax.imageio.ImageIO;

import com.appslandia.common.base.Params;
import com.appslandia.common.utils.Asserts;
import com.appslandia.plum.base.ActionParser;
import com.appslandia.plum.base.BadRequestException;
import com.appslandia.plum.base.BindValue;
import com.appslandia.plum.base.CacheControl;
import com.appslandia.plum.base.Controller;
import com.appslandia.plum.base.HttpGet;
import com.appslandia.plum.base.RequestWrapper;
import com.appslandia.plum.base.Resources;
import com.appslandia.plum.base.Result;
import com.appslandia.plum.base.SimpleCaptchaManager;
import com.appslandia.plum.captcha.CaptchaProducer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
@Controller("captcha")
@CacheControl(nocache = true)
public class DefaultCaptchaController {

  @Inject
  protected SimpleCaptchaManager captchaManager;

  @Inject
  protected CaptchaProducer captchaProducer;

  @Inject
  protected ActionParser actionParser;

  @HttpGet
  public void img(RequestWrapper request, HttpServletResponse response,
      @BindValue(name = SimpleCaptchaManager.PARAM_CAPTCHA_ID) String captchaId) throws Exception {
    if (captchaId == null) {
      throw new BadRequestException(request.res(Resources.ERROR_BAD_REQUEST)).setTitleKey(Resources.ERROR_BAD_REQUEST);
    }

    var captchaText = this.captchaManager.getCaptchaText(request, captchaId);
    var img = this.captchaProducer.produce(captchaText);

    response.setContentType("image/png");
    try (OutputStream os = response.getOutputStream()) {
      ImageIO.write(img, "png", os);
    }
  }

  @HttpGet
  public Result refresh(RequestWrapper request, HttpServletResponse response,
      @BindValue(name = SimpleCaptchaManager.PARAM_CAPTCHA_ID) String captchaId) throws Exception {
    if (captchaId == null) {
      throw new BadRequestException(request.res(Resources.ERROR_BAD_REQUEST)).setTitleKey(Resources.ERROR_BAD_REQUEST);
    }

    var captchaText = this.captchaManager.refreshCaptcha(request, captchaId);
    Asserts.notNull(captchaText);

    var params = new Params().set(SimpleCaptchaManager.PARAM_CAPTCHA_ID, captchaId).set("t",
        System.currentTimeMillis());
    var url = this.actionParser.toActionUrl(request, "captcha", "img", params, false);
    return new Result().setData(url);
  }
}
