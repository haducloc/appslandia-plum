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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.appslandia.plum.mocks.MockCaptchaManager;
import com.appslandia.plum.mocks.MockHttpServletRequest;
import com.appslandia.plum.results.RedirectResult;

/**
 *
 * @author Loc Ha
 *
 */
public class EnableCaptchaTest extends MockTestBase {

  SimpleCaptchaManager captchaManager;

  @Override
  protected void initialize() {
    container.register(TestController.class, TestController.class);
    captchaManager = container.getObject(CaptchaManager.class);
  }

  @Test
  public void test_testCaptcha() {
    try {
      executeCurrent("GET", "http://localhost/app/testController/testCaptcha");

      String captchaId = (String) getCurrentRequest().getAttribute(SimpleCaptchaManager.REQUEST_ATTRIBUTE_CAPTCHA_DATA);
      Assertions.assertNull(captchaId);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_testCaptcha_POST() {
    try {
      MockHttpServletRequest request = container.createRequest("GET");
      captchaManager.initCaptcha(request);
      String captchaId = (String) request.getAttribute(SimpleCaptchaManager.REQUEST_ATTRIBUTE_CAPTCHA_DATA);

      getCurrentRequest().setSession(request.getSession());
      getCurrentRequest().setParameter(SimpleCaptchaManager.PARAM_CAPTCHA_ID, captchaId);
      getCurrentRequest().setParameter(SimpleCaptchaManager.PARAM_CAPTCHA_WORDS, MockCaptchaManager.MOCK_CAPTCHA_WORDS);

      executeCurrent("POST", "http://localhost/app/testController/testCaptcha");

      Assertions.assertTrue(getCurrentModelState().isValid(SimpleCaptchaManager.PARAM_CAPTCHA_WORDS));

      // Removed
      boolean valid = captchaManager.verifyCaptcha(getCurrentRequest());
      Assertions.assertTrue(!valid);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_testCaptcha_POST_invalid() {
    try {
      MockHttpServletRequest request = container.createRequest();
      captchaManager.initCaptcha(request);
      String captchaId = (String) request.getAttribute(SimpleCaptchaManager.REQUEST_ATTRIBUTE_CAPTCHA_DATA);

      getCurrentRequest().setSession(request.getSession());
      getCurrentRequest().setParameter(SimpleCaptchaManager.PARAM_CAPTCHA_ID, captchaId);
      getCurrentRequest().setParameter(SimpleCaptchaManager.PARAM_CAPTCHA_WORDS, "invalid");

      executeCurrent("POST", "http://localhost/app/testController/testCaptcha");

      Assertions.assertFalse(getCurrentModelState().isValid(SimpleCaptchaManager.PARAM_CAPTCHA_WORDS));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Controller("testController")
  public static class TestController {

    @HttpGetPost
    @EnableCaptcha
    public ActionResult testCaptcha(RequestWrapper request) throws Exception {
      if (request.isGetOrHead()) {
        return ActionResult.EMPTY;
      }
      return RedirectResult.ROOT;
    }
  }
}
