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

/**
 *
 * @author Loc Ha
 *
 */
public class CaptchaManagerTest extends MockTestBase {

  SimpleCaptchaManager captchaManager;
  RequestContextParser requestContextParser;

  @Override
  protected void initialize() {
    container.register(TestController.class, TestController.class);
    captchaManager = container.getObject(CaptchaManager.class);
    requestContextParser = container.getObject(RequestContextParser.class);
  }

  @Test
  public void test_initCaptcha() {
    try {
      getCurrentRequest().setRequestURL("http://localhost/app/testController/testCaptcha");
      requestContextParser.initRequestContext(getCurrentRequest(), getCurrentResponse());

      captchaManager.initCaptcha(getCurrentRequest());
      var captchaId = (String) getCurrentRequest().getAttribute(SimpleCaptchaManager.PARAM_CAPTCHA_ID);
      Assertions.assertNotNull(captchaId);

      captchaId = (String) getCurrentRequest().getAttribute(SimpleCaptchaManager.PARAM_CAPTCHA_ID);
      Assertions.assertNotNull(captchaId);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_getCaptchaWords() {
    try {
      var request = container.createRequest("GET", "http://localhost/app/testController/testCaptcha");
      requestContextParser.initRequestContext(request, container.createResponse());
      captchaManager.initCaptcha(request);
      var captchaId = (String) request.getAttribute(SimpleCaptchaManager.PARAM_CAPTCHA_ID);

      getCurrentRequest().setMethod("POST").setRequestURL("http://localhost/app/testController/testCaptcha");
      getCurrentRequest().setSession(request.getSession());
      getCurrentRequest().addParameter(SimpleCaptchaManager.PARAM_CAPTCHA_ID, captchaId);
      requestContextParser.initRequestContext(getCurrentRequest(), getCurrentResponse());

      var captchaText = captchaManager.getCaptchaText(getCurrentRequest(), captchaId);
      Assertions.assertNotNull(captchaText);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_verifyCaptcha() {
    try {
      var request = container.createRequest("GET", "http://localhost/app/testController/testCaptcha");
      requestContextParser.initRequestContext(request, container.createResponse());
      captchaManager.initCaptcha(request);
      var captchaId = (String) request.getAttribute(SimpleCaptchaManager.PARAM_CAPTCHA_ID);

      getCurrentRequest().setMethod("POST").setRequestURL("http://localhost/app/testController/testCaptcha");
      getCurrentRequest().setSession(request.getSession());
      getCurrentRequest().addParameter(SimpleCaptchaManager.PARAM_CAPTCHA_ID, captchaId);
      getCurrentRequest().addParameter(SimpleCaptchaManager.PARAM_CAPTCHA_TEXT, MockCaptchaManager.MOCK_CAPTCHA_WORDS);
      requestContextParser.initRequestContext(getCurrentRequest(), getCurrentResponse());

      var valid = captchaManager.verifyCaptcha(getCurrentRequest());
      Assertions.assertTrue(valid);

      // Removed
      valid = captchaManager.verifyCaptcha(getCurrentRequest());
      Assertions.assertFalse(valid);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_verifyCaptcha_invalidWords() {
    try {
      var request = container.createRequest("GET", "http://localhost/app/testController/testCaptcha");
      requestContextParser.initRequestContext(request, container.createResponse());
      captchaManager.initCaptcha(request);
      var captchaId = (String) request.getAttribute(SimpleCaptchaManager.PARAM_CAPTCHA_ID);

      getCurrentRequest().setMethod("POST").setRequestURL("http://localhost/app/testController/testCaptcha");
      getCurrentRequest().setSession(request.getSession());
      getCurrentRequest().addParameter(SimpleCaptchaManager.PARAM_CAPTCHA_ID, captchaId);
      getCurrentRequest().addParameter(SimpleCaptchaManager.PARAM_CAPTCHA_TEXT, "invalid");
      requestContextParser.initRequestContext(getCurrentRequest(), getCurrentResponse());

      var valid = captchaManager.verifyCaptcha(getCurrentRequest());
      Assertions.assertFalse(valid);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_verifyCaptcha_noSession() {
    try {
      var request = container.createRequest("GET", "http://localhost/app/testController/testCaptcha");
      requestContextParser.initRequestContext(request, container.createResponse());
      captchaManager.initCaptcha(request);
      var captchaId = (String) request.getAttribute(SimpleCaptchaManager.PARAM_CAPTCHA_ID);

      getCurrentRequest().setMethod("POST").setRequestURL("http://localhost/app/testController/testCaptcha");
      // getCurrentRequest().setSession(request.getSession());
      getCurrentRequest().addParameter(SimpleCaptchaManager.PARAM_CAPTCHA_ID, captchaId);
      getCurrentRequest().addParameter(SimpleCaptchaManager.PARAM_CAPTCHA_TEXT, MockCaptchaManager.MOCK_CAPTCHA_WORDS);
      requestContextParser.initRequestContext(getCurrentRequest(), getCurrentResponse());

      var valid = captchaManager.verifyCaptcha(getCurrentRequest());
      Assertions.assertFalse(valid);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Controller("testController")
  public static class TestController {

    @HttpGetPost
    @EnableCaptcha
    public void testCaptcha() throws Exception {
    }
  }
}
