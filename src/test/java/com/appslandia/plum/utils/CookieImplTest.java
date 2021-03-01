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

package com.appslandia.plum.utils;

import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class CookieImplTest {

	@Test
	public void test() {
		CookieImpl cookie = new CookieImpl("c1", "v1");
		String respCookie = cookie.toString();

		Assert.assertEquals(respCookie, "c1=v1");
	}

	@Test
	public void test_setPath() {
		CookieImpl cookie = new CookieImpl("c1", "v1");
		cookie.setPath("");

		String respCookie = cookie.toString();
		Assert.assertEquals(respCookie, "c1=v1; Path=");
	}

	@Test
	public void test_setDomain() {
		CookieImpl cookie = new CookieImpl("c1", "v1");
		cookie.setDomain("domain1");

		String respCookie = cookie.toString();
		Assert.assertEquals(respCookie, "c1=v1; Domain=domain1");
	}

	@Test
	public void test_setDiscard() {
		CookieImpl cookie = new CookieImpl("c1", "v1");
		cookie.setDiscard(true);

		String respCookie = cookie.toString();
		Assert.assertEquals(respCookie, "c1=v1; Discard");
	}

	@Test
	public void test_setSecure() {
		CookieImpl cookie = new CookieImpl("c1", "v1");
		cookie.setSecure(true);

		String respCookie = cookie.toString();
		Assert.assertEquals(respCookie, "c1=v1; Secure");
	}

	@Test
	public void test_setHttpOnly() {
		CookieImpl cookie = new CookieImpl("c1", "v1");
		cookie.setHttpOnly(true);

		String respCookie = cookie.toString();
		Assert.assertEquals(respCookie, "c1=v1; HttpOnly");
	}

	@Test
	public void test_setSameSite() {
		CookieImpl cookie = new CookieImpl("c1", "v1");
		cookie.setSameSite(true);

		String respCookie = cookie.toString();
		Assert.assertEquals(respCookie, "c1=v1; SameSite");

		cookie = new CookieImpl("c1", "v1");
		cookie.setSameSite(true).setSameSiteMode("lax");

		respCookie = cookie.toString();
		Assert.assertEquals(respCookie, "c1=v1; SameSite=Lax");
	}

	@Test
	public void test_setMaxAge() {
		CookieImpl cookie = new CookieImpl("c1", "v1");
		String respCookie = cookie.toString();
		Assert.assertEquals(respCookie, "c1=v1");

		cookie = new CookieImpl("c1", "v1");
		cookie.setMaxAge(0, TimeUnit.SECONDS);
		respCookie = cookie.toString();
		Assert.assertEquals(respCookie, "c1=v1; Max-Age=0; Expires=Thu, 01 Jan 1970 00:00:00 GMT");

		cookie = new CookieImpl("c1", "v1");
		cookie.setMaxAge(1000, TimeUnit.SECONDS);
		respCookie = cookie.toString();
		Assert.assertTrue(respCookie.contains("c1=v1; Max-Age=1000; Expires="));
	}
}
