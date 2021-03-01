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

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.appslandia.plum.mocks.MockHttpServletResponse;
import com.appslandia.plum.mocks.MockServletContext;
import com.appslandia.plum.mocks.MockServletUtils;
import com.appslandia.plum.mocks.MockSessionCookieConfig;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class MockHttpServletResponseTest {

	MockServletContext servletContext;

	@Before
	public void onInvokingTest() {
		servletContext = new MockServletContext(new MockSessionCookieConfig());
	}

	@Test
	public void test() {
		MockHttpServletResponse response = new MockHttpServletResponse(servletContext);
		response.setCharacterEncoding("UTF-8");
		Assert.assertEquals(response.getCharacterEncoding(), "UTF-8");

		try {
			response.flushBuffer();
		} catch (IOException ex) {
		}
		Assert.assertTrue(response.isCommitted());

		response.setContentType("text/html");
		Assert.assertEquals(response.getContentType(), "text/html");
	}

	@Test
	public void test_headers() {
		MockHttpServletResponse response = new MockHttpServletResponse(servletContext);
		response.addHeaderValues("h1", "v1");
		response.addHeaderValues("h2", "v21", "v22");

		Assert.assertEquals(response.getHeader("h1"), "v1");
		response.addHeaderValues("h1", "v11");
		Assert.assertTrue(response.getHeaders("h1").size() == 2);

		Assert.assertEquals(response.getHeader("h2"), "v21");
		Assert.assertEquals(response.getHeaders("h2").iterator().next(), "v21");
	}

	@Test
	public void test_sendError() {
		MockHttpServletResponse response = new MockHttpServletResponse(servletContext);
		try {
			response.sendError(500);
		} catch (IOException ex) {
			Assert.fail(ex.getMessage());
		}
		Assert.assertEquals(response.getStatus(), 500);
	}

	@Test
	public void test_cookies() {
		MockHttpServletResponse response = new MockHttpServletResponse(servletContext);
		response.addCookie(MockServletUtils.createCookie(servletContext, "cookie1", "v1", 1000));

		Assert.assertNotNull(response.getCookie("cookie1"));
		Assert.assertEquals(response.getCookie("cookie1").getValue(), "v1");
	}

	@Test
	public void test_writeContent() {
		MockHttpServletResponse response = new MockHttpServletResponse(servletContext);
		try {
			response.getWriter().write("data");
			response.flushBuffer();

			Assert.assertEquals(response.getContent().toString(StandardCharsets.UTF_8), "data");

		} catch (IOException ex) {
			Assert.fail(ex.getMessage());
		}
	}
}
