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

package com.appslandia.plum.base;

import com.appslandia.common.base.TextGenerator;
import com.appslandia.common.utils.StringUtils;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.servlet.http.HttpServletRequest;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public abstract class SimpleCaptchaManager implements CaptchaManager {

  public static final String PARAM_CAPTCHA_ID = "captchaId";
  public static final String PARAM_CAPTCHA_WORDS = "captchaWords";
  public static final String REQUEST_ATTRIBUTE_CAPTCHA_DATA = "captchaData";

  protected abstract TextGenerator getCaptchaIdGenerator();

  protected abstract TextGenerator getWordsGenerator();

  protected abstract Object saveCaptcha(HttpServletRequest request, String captchaId, String captchaWords);

  protected abstract Object parseCaptchaData(HttpServletRequest request, String captchaId);

  @Override
  public void initCaptcha(HttpServletRequest request) {
    String captchaId = getCaptchaIdGenerator().generate();
    String captchaWords = getWordsGenerator().generate();

    Object captchaData = saveCaptcha(request, captchaId, captchaWords);
    request.setAttribute(REQUEST_ATTRIBUTE_CAPTCHA_DATA, captchaData);
  }

  protected abstract String doGetCaptchaWords(HttpServletRequest request, String captchaId);

  public String getCaptchaWords(HttpServletRequest request) {
    String captchaId = request.getParameter(PARAM_CAPTCHA_ID);
    if (captchaId == null) {
      return null;
    }
    return doGetCaptchaWords(request, captchaId);
  }

  protected abstract boolean doVerifyCaptcha(HttpServletRequest request, String captchaId, String captchaWords);

  @Override
  public boolean verifyCaptcha(HttpServletRequest request) {
    String captchaId = request.getParameter(PARAM_CAPTCHA_ID);
    if (captchaId == null) {
      ServletUtils.addError(request, PARAM_CAPTCHA_WORDS, Resources.ERROR_CAPTCHA_FAILED);
      return false;
    }
    String captchaWords = StringUtils.trimToNull(request.getParameter(PARAM_CAPTCHA_WORDS));
    if (captchaWords == null) {
      ServletUtils.addError(request, PARAM_CAPTCHA_WORDS, Resources.ERROR_CAPTCHA_FAILED);
      return false;
    }
    boolean valid = doVerifyCaptcha(request, captchaId, captchaWords);
    if (!valid) {
      ServletUtils.addError(request, PARAM_CAPTCHA_WORDS, Resources.ERROR_CAPTCHA_FAILED);
    }
    return valid;
  }
}
