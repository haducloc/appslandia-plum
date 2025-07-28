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

package com.appslandia.plum.base;

import java.util.UUID;

import com.appslandia.common.base.TextGenerator;
import com.appslandia.common.utils.StringUtils;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.servlet.http.HttpServletRequest;

/**
 *
 * @author Loc Ha
 *
 */
public abstract class SimpleCaptchaManager implements CaptchaManager {

  public static final String PARAM_CAPTCHA_ID = "__captcha_id";
  public static final String PARAM_CAPTCHA_TEXT = "__captcha_text";

  protected abstract TextGenerator getWordsGenerator();

  protected abstract void saveCaptcha(HttpServletRequest request, String captchaId, String captchaText);

  protected String generateCaptchaId() {
    return UUID.randomUUID().toString();
  }

  @Override
  public String initCaptcha(HttpServletRequest request) {
    var captchaId = generateCaptchaId();
    var captchaText = getWordsGenerator().generate();

    saveCaptcha(request, captchaId, captchaText);
    request.setAttribute(PARAM_CAPTCHA_ID, captchaId);
    return captchaId;
  }

  public abstract String getCaptchaText(HttpServletRequest request, String captchaId);

  public String refreshCaptcha(HttpServletRequest request, String captchaId) {
    var captchaText = getWordsGenerator().generate();
    saveCaptcha(request, captchaId, captchaText);
    request.setAttribute(PARAM_CAPTCHA_ID, captchaId);
    return captchaId;
  }

  protected abstract boolean doVerifyCaptcha(HttpServletRequest request, String captchaId, String captchaText);

  @Override
  public boolean verifyCaptcha(HttpServletRequest request) {
    var captchaId = request.getParameter(PARAM_CAPTCHA_ID);
    if (captchaId == null) {
      ServletUtils.addError(request, PARAM_CAPTCHA_TEXT, Resources.ERROR_CAPTCHA_FAILED);
      return false;
    }
    var captchaText = StringUtils.trimToNull(request.getParameter(PARAM_CAPTCHA_TEXT));
    if (captchaText == null) {
      ServletUtils.addError(request, PARAM_CAPTCHA_TEXT, Resources.ERROR_CAPTCHA_FAILED);
      return false;
    }
    var valid = doVerifyCaptcha(request, captchaId, captchaText);
    if (!valid) {
      ServletUtils.addError(request, PARAM_CAPTCHA_TEXT, Resources.ERROR_CAPTCHA_FAILED);
    }
    return valid;
  }
}
