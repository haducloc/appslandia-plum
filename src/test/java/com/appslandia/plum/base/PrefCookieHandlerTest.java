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
public class PrefCookieHandlerTest extends MockTestBase {

	PrefCookieHandler prefCookieHandler;

	@Override
	protected void initialize() {
		prefCookieHandler = container.getObject(PrefCookieHandler.class);
	}

	private Cookie initPrefCookie() {
		MockHttpServletResponse response = container.createResponse();
		prefCookieHandler.savePrefCookie(response, new PrefCookie().setLanguage("en"));
		return response.getCookie(prefCookieHandler.getCookieName());
	}

	@Test
	public void test_savePrefCookie() {
		try {
			prefCookieHandler.savePrefCookie(getCurrentResponse(), new PrefCookie().setLanguage("en"));

			Cookie savedCookie = getCurrentResponse().getCookie(prefCookieHandler.getCookieName());
			Assert.assertNotNull(savedCookie);

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_loadPrefCookie() {
		try {
			getCurrentRequest().addCookie(initPrefCookie());

			PrefCookie prefCookie = prefCookieHandler.loadPrefCookie(getCurrentRequest(), getCurrentResponse());
			Assert.assertNotNull(prefCookie);

			Assert.assertNotNull(getCurrentRequest().getAttribute(PrefCookie.REQUEST_ATTRIBUTE_ID));
			Assert.assertEquals(prefCookie.getLanguage(), "en");

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_loadPrefCookie_noCookie() {
		try {
			PrefCookie prefCookie = prefCookieHandler.loadPrefCookie(getCurrentRequest(), getCurrentResponse());
			Assert.assertNull(prefCookie);

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_loadPrefCookie_invalidCookie() {
		try {
			getCurrentRequest().addCookie(new Cookie(prefCookieHandler.getCookieName(), "invalid"));

			executeCurrent("GET", "http://localhost/app/testController/testAction");

			PrefCookie prefCookie = (PrefCookie) getCurrentRequest().getAttribute(PrefCookie.REQUEST_ATTRIBUTE_ID);
			Assert.assertNull(prefCookie);
		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_removePrefCookie() {
		try {
			prefCookieHandler.removePrefCookie(getCurrentResponse());
			Cookie deletedCookie = getCurrentResponse().getCookie(prefCookieHandler.getCookieName());

			Assert.assertNotNull(deletedCookie);
			Assert.assertTrue(deletedCookie.getMaxAge() == 0);
			Assert.assertTrue("".equals(deletedCookie.getValue()));

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}
}
