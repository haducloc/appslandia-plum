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

import javax.servlet.DispatcherType;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.appslandia.plum.mocks.MockHttpServletRequest;
import com.appslandia.plum.mocks.MockServletContext;
import com.appslandia.plum.mocks.MockServletUtils;
import com.appslandia.plum.mocks.MockSessionCookieConfig;
import com.appslandia.plum.utils.ServletUtils;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class MockHttpServletRequestTest {

	MockServletContext servletContext;

	@Before
	public void onInvokingTest() {
		servletContext = new MockServletContext(new MockSessionCookieConfig());
	}

	@Test
	public void test() {
		MockHttpServletRequest request = new MockHttpServletRequest(servletContext);
		request.setMethod("POST");
		Assert.assertEquals(request.getMethod(), "POST");

		request.setCharacterEncoding("UTF-8");
		Assert.assertEquals(request.getCharacterEncoding(), "UTF-8");

		request.setDispatcherType(DispatcherType.ERROR);
		Assert.assertEquals(request.getDispatcherType(), DispatcherType.ERROR);

		request.setHeader("Accept-Encoding", "gzip");
		Assert.assertEquals(request.getHeader("Accept-Encoding"), "gzip");
	}

	@Test
	public void test_setRequestURL() {
		MockHttpServletRequest request = new MockHttpServletRequest(servletContext);
		try {
			request.setRequestURL("http://localhost/app/main?param1=value1");
		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
		Assert.assertEquals(request.getScheme(), "http");
		Assert.assertEquals(request.getServerName(), "localhost");
		Assert.assertEquals(request.getServerPort(), 80);

		Assert.assertEquals(request.getRequestURI(), "/app/main");
		Assert.assertEquals(request.getServletPath(), "/main");
		Assert.assertEquals(request.getQueryString(), "param1=value1");

		try {
			request.setRequestURL("http://localhost:8080/app/main");
		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
		Assert.assertEquals(request.getServerPort(), 8080);
	}

	@Test
	public void test_attributes() {
		MockHttpServletRequest request = new MockHttpServletRequest(servletContext);
		request.setAttribute("attr1", "value1");
		request.setAttribute("attr2", "value2");

		request.setAttribute("attr3", "value3");
		request.setAttribute("attr3", null);

		Assert.assertEquals(request.getAttribute("attr1"), "value1");
		Assert.assertNull(request.getAttribute("attr3"));

		request.removeAttribute("attr2");
		Assert.assertNull(request.getAttribute("attr2"));
	}

	@Test
	public void test_parameters() {
		MockHttpServletRequest request = new MockHttpServletRequest(servletContext);
		request.addParameter("p1", "v1");
		request.addParameter("p2", "v21", "v22");

		request.addParameter("p3", "v31");
		request.addParameter("p3", "v32");

		Assert.assertEquals(request.getParameter("p1"), "v1");
		Assert.assertEquals(request.getParameter("p2"), "v21");
		Assert.assertEquals(request.getParameter("p3"), "v31");

		Assert.assertArrayEquals(request.getParameterValues("p2"), new String[] { "v21", "v22" });
		Assert.assertArrayEquals(request.getParameterValues("p3"), new String[] { "v31", "v32" });
	}

	@Test
	public void test_headers() {
		MockHttpServletRequest request = new MockHttpServletRequest(servletContext);
		request.setHeaderValues("h1", "v1");
		request.setHeaderValues("h2", "v21", "v22");

		Assert.assertEquals(request.getHeader("h1"), "v1");
		Assert.assertEquals(request.getHeader("h2"), "v21");
		Assert.assertEquals(request.getHeaders("h2").nextElement(), "v21");

		request.addSessionCookie("session-1");
		Assert.assertEquals(request.getCookie(request.getServletContext().getSessionCookieConfig().getName()).getValue(), "session-1");
	}

	@Test
	public void test_cookies() {
		MockHttpServletRequest request = new MockHttpServletRequest(servletContext);
		Assert.assertNull(request.getCookies());

		request.addCookie(MockServletUtils.createCookie(servletContext, "cookie1", "v1", 1000));
		Assert.assertNotNull(request.getCookies());

		String cookie = ServletUtils.getCookieValue(request, "cookie1");
		Assert.assertEquals(cookie, "v1");

		request.addSessionCookie("session-1");
		Assert.assertEquals(request.getRequestedSessionId(), "session-1");
	}

	@Test
	public void test_invalidate() {
		MockHttpServletRequest request = new MockHttpServletRequest(servletContext);
		request.getSession().invalidate();

		Assert.assertNull(request.getSession(false));
		Assert.assertNotNull(request.getSession());
	}

	@Test
	public void test_session() {
		MockHttpServletRequest request = new MockHttpServletRequest(servletContext);
		Assert.assertNotNull(request.getServletContext());

		Assert.assertNull(request.getSession(false));
		Assert.assertNotNull(request.getSession(true));
		Assert.assertEquals(request.getSession(true), request.getSession());
	}
}
