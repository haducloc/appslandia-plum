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

import org.junit.Assert;
import org.junit.Test;

import com.appslandia.plum.mocks.MockCaptchaManager;
import com.appslandia.plum.mocks.MockHttpServletRequest;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
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
			requestContextParser.parse(getCurrentRequest(), getCurrentResponse());

			captchaManager.initCaptcha(getCurrentRequest());
			String captchaId = (String) getCurrentRequest().getAttribute(SimpleCaptchaManager.REQUEST_ATTRIBUTE_CAPTCHA_DATA);
			Assert.assertNotNull(captchaId);

			captchaId = (String) getCurrentRequest().getAttribute(SimpleCaptchaManager.REQUEST_ATTRIBUTE_CAPTCHA_DATA);
			Assert.assertNotNull(captchaId);

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_getCaptchaWords() {
		try {
			MockHttpServletRequest request = container.createRequest("GET", "http://localhost/app/testController/testCaptcha");
			requestContextParser.parse(request, container.createResponse());
			captchaManager.initCaptcha(request);
			String captchaId = (String) request.getAttribute(SimpleCaptchaManager.REQUEST_ATTRIBUTE_CAPTCHA_DATA);

			getCurrentRequest().setMethod("POST").setRequestURL("http://localhost/app/testController/testCaptcha");
			getCurrentRequest().setSession(request.getSession());
			getCurrentRequest().addParameter(SimpleCaptchaManager.PARAM_CAPTCHA_ID, captchaId);
			requestContextParser.parse(getCurrentRequest(), getCurrentResponse());

			String captchaWords = captchaManager.getCaptchaWords(getCurrentRequest());
			Assert.assertNotNull(captchaWords);

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_verifyCaptcha() {
		try {
			MockHttpServletRequest request = container.createRequest("GET", "http://localhost/app/testController/testCaptcha");
			requestContextParser.parse(request, container.createResponse());
			captchaManager.initCaptcha(request);
			String captchaId = (String) request.getAttribute(SimpleCaptchaManager.REQUEST_ATTRIBUTE_CAPTCHA_DATA);

			getCurrentRequest().setMethod("POST").setRequestURL("http://localhost/app/testController/testCaptcha");
			getCurrentRequest().setSession(request.getSession());
			getCurrentRequest().addParameter(SimpleCaptchaManager.PARAM_CAPTCHA_ID, captchaId);
			getCurrentRequest().addParameter(SimpleCaptchaManager.PARAM_CAPTCHA_WORDS, MockCaptchaManager.MOCK_CAPTCHA_WORDS);
			requestContextParser.parse(getCurrentRequest(), getCurrentResponse());

			boolean valid = captchaManager.verifyCaptcha(getCurrentRequest());
			Assert.assertTrue(valid);

			// Removed
			valid = captchaManager.verifyCaptcha(getCurrentRequest());
			Assert.assertFalse(valid);

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_verifyCaptcha_invalidWords() {
		try {
			MockHttpServletRequest request = container.createRequest("GET", "http://localhost/app/testController/testCaptcha");
			requestContextParser.parse(request, container.createResponse());
			captchaManager.initCaptcha(request);
			String captchaId = (String) request.getAttribute(SimpleCaptchaManager.REQUEST_ATTRIBUTE_CAPTCHA_DATA);

			getCurrentRequest().setMethod("POST").setRequestURL("http://localhost/app/testController/testCaptcha");
			getCurrentRequest().setSession(request.getSession());
			getCurrentRequest().addParameter(SimpleCaptchaManager.PARAM_CAPTCHA_ID, captchaId);
			getCurrentRequest().addParameter(SimpleCaptchaManager.PARAM_CAPTCHA_WORDS, "invalid");
			requestContextParser.parse(getCurrentRequest(), getCurrentResponse());

			boolean valid = captchaManager.verifyCaptcha(getCurrentRequest());
			Assert.assertFalse(valid);

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_verifyCaptcha_noSession() {
		try {
			MockHttpServletRequest request = container.createRequest("GET", "http://localhost/app/testController/testCaptcha");
			requestContextParser.parse(request, container.createResponse());
			captchaManager.initCaptcha(request);
			String captchaId = (String) request.getAttribute(SimpleCaptchaManager.REQUEST_ATTRIBUTE_CAPTCHA_DATA);

			getCurrentRequest().setMethod("POST").setRequestURL("http://localhost/app/testController/testCaptcha");
			// getCurrentRequest().setSession(request.getSession());
			getCurrentRequest().addParameter(SimpleCaptchaManager.PARAM_CAPTCHA_ID, captchaId);
			getCurrentRequest().addParameter(SimpleCaptchaManager.PARAM_CAPTCHA_WORDS, MockCaptchaManager.MOCK_CAPTCHA_WORDS);
			requestContextParser.parse(getCurrentRequest(), getCurrentResponse());

			boolean valid = captchaManager.verifyCaptcha(getCurrentRequest());
			Assert.assertFalse(valid);

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
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
