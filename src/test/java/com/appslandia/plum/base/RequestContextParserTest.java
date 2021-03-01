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

import javax.servlet.http.Cookie;

import org.junit.Assert;
import org.junit.Test;

import com.appslandia.plum.mocks.MockHttpServletResponse;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class RequestContextParserTest extends MockTestBase {

	RequestContextParser requestContextParser;

	@Override
	protected void initialize() {
		container.register(TestController.class, TestController.class);
		requestContextParser = container.getObject(RequestContextParser.class);
	}

	private Cookie initPrefCookie(String language) {
		MockHttpServletResponse response = container.createResponse();
		PrefCookieHandler prefCookieHandler = container.getObject(PrefCookieHandler.class);
		prefCookieHandler.savePrefCookie(response, new PrefCookie().setLanguage(language));
		return response.getCookie(prefCookieHandler.getCookieName());
	}

	@Test
	public void test_pathLanguage() {
		getCurrentRequest().setRequestURL("http://localhost/app/vi/testController/index");
		requestContextParser.parse(getCurrentRequest(), getCurrentResponse());

		RequestContext requestContext = (RequestContext) getCurrentRequest().getAttribute(RequestContext.REQUEST_ATTRIBUTE_ID);
		Assert.assertNotNull(requestContext.getActionDesc());

		Assert.assertTrue(requestContext.isPathLanguage());
		Assert.assertEquals(requestContext.getLanguage().getId(), "vi");
	}

	@Test
	public void test_defaultLanguage() {
		getCurrentRequest().setRequestURL("http://localhost/app/testController/index");
		requestContextParser.parse(getCurrentRequest(), getCurrentResponse());

		RequestContext requestContext = (RequestContext) getCurrentRequest().getAttribute(RequestContext.REQUEST_ATTRIBUTE_ID);
		Assert.assertNotNull(requestContext.getActionDesc());

		Assert.assertFalse(requestContext.isPathLanguage());
		Assert.assertEquals(requestContext.getLanguage().getId(), "en");
	}

	@Test
	public void test_prefLanguage() {
		getCurrentRequest().setRequestURL("http://localhost/app/testController/index");
		getCurrentRequest().addCookie(initPrefCookie("vi"));
		requestContextParser.parse(getCurrentRequest(), getCurrentResponse());

		RequestContext requestContext = (RequestContext) getCurrentRequest().getAttribute(RequestContext.REQUEST_ATTRIBUTE_ID);
		Assert.assertNotNull(requestContext.getActionDesc());

		Assert.assertFalse(requestContext.isPathLanguage());
		Assert.assertEquals(requestContext.getLanguage().getId(), "vi");
	}

	@Test
	public void test_noActionDesc() {
		getCurrentRequest().setRequestURL("http://localhost/app/testController/noAction");
		requestContextParser.parse(getCurrentRequest(), getCurrentResponse());

		RequestContext requestContext = (RequestContext) getCurrentRequest().getAttribute(RequestContext.REQUEST_ATTRIBUTE_ID);
		Assert.assertNull(requestContext.getActionDesc());

		Assert.assertFalse(requestContext.isPathLanguage());
		Assert.assertEquals(requestContext.getLanguage().getId(), "en");
	}

	@Controller("testController")
	public static class TestController {

		@HttpGet
		public void index() {
		}
	}
}
