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

package com.appslandia.plum.mocks.tests;

import org.junit.Assert;
import org.junit.Test;

import com.appslandia.plum.mocks.MockServletContext;
import com.appslandia.plum.mocks.MockSessionCookieConfig;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class MockServletContextTest {

	@Test
	public void test() {
		MockServletContext sc = new MockServletContext(new MockSessionCookieConfig());

		Assert.assertNotNull(sc.getSessionCookieConfig());
		Assert.assertEquals(sc.getContextPath(), "/app");

		sc.setAppDir("C:/webapps");
		Assert.assertEquals(sc.getAppDir(), "C:/webapps");
	}

	@Test
	public void test_attributes() {
		MockServletContext sc = new MockServletContext(new MockSessionCookieConfig());

		sc.setAttribute("location", "location1");
		Assert.assertEquals(sc.getAttribute("location"), "location1");

		sc.removeAttribute("location");
		Assert.assertNull(sc.getAttribute("location"));
	}

	@Test
	public void test_initParameters() {
		MockServletContext sc = new MockServletContext(new MockSessionCookieConfig());
		sc.setInitParameter("p1", "v1");
		sc.setInitParameter("p2", "v2");

		Assert.assertEquals(sc.getInitParameter("p1"), "v1");
	}
}
