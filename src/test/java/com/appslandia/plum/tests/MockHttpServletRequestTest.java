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

package com.appslandia.plum.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.appslandia.plum.mocks.MockHttpServletRequest;
import com.appslandia.plum.mocks.MockServletContext;
import com.appslandia.plum.mocks.MockServletUtils;
import com.appslandia.plum.mocks.MockSessionCookieConfig;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.servlet.DispatcherType;

/**
 *
 * @author Loc Ha
 *
 */
public class MockHttpServletRequestTest {

  MockServletContext servletContext;

  @BeforeEach
  public void beforeEachTest() {
    servletContext = new MockServletContext(new MockSessionCookieConfig());
  }

  @Test
  public void test() {
    var request = new MockHttpServletRequest(servletContext);
    request.setMethod("POST");
    Assertions.assertEquals("POST", request.getMethod());

    request.setCharacterEncoding("UTF-8");
    Assertions.assertEquals("UTF-8", request.getCharacterEncoding());

    request.setDispatcherType(DispatcherType.ERROR);
    Assertions.assertEquals(DispatcherType.ERROR, request.getDispatcherType());

    request.setHeader("Accept-Encoding", "gzip");
    Assertions.assertEquals("gzip", request.getHeader("Accept-Encoding"));
  }

  @Test
  public void test_setRequestURL() {
    var request = new MockHttpServletRequest(servletContext);
    try {
      request.setRequestURL("http://localhost/app/main?param1=value1");
    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
    Assertions.assertEquals("http", request.getScheme());
    Assertions.assertEquals("localhost", request.getServerName());
    Assertions.assertEquals(80, request.getServerPort());

    Assertions.assertEquals("/app/main", request.getRequestURI());
    Assertions.assertEquals("/main", request.getServletPath());
    Assertions.assertEquals("param1=value1", request.getQueryString());

    try {
      request.setRequestURL("http://localhost:8080/app/main");
    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
    Assertions.assertEquals(8080, request.getServerPort());
  }

  @Test
  public void test_attributes() {
    var request = new MockHttpServletRequest(servletContext);
    request.setAttribute("attr1", "value1");
    request.setAttribute("attr2", "value2");

    request.setAttribute("attr3", "value3");
    request.setAttribute("attr3", null);

    Assertions.assertEquals("value1", request.getAttribute("attr1"));
    Assertions.assertNull(request.getAttribute("attr3"));

    request.removeAttribute("attr2");
    Assertions.assertNull(request.getAttribute("attr2"));
  }

  @Test
  public void test_parameters() {
    var request = new MockHttpServletRequest(servletContext);
    request.addParameter("p1", "v1");
    request.addParameter("p2", "v21", "v22");

    request.addParameter("p3", "v31");
    request.addParameter("p3", "v32");

    Assertions.assertEquals("v1", request.getParameter("p1"));
    Assertions.assertEquals("v21", request.getParameter("p2"));
    Assertions.assertEquals("v31", request.getParameter("p3"));

    Assertions.assertArrayEquals(new String[] { "v21", "v22" }, request.getParameterValues("p2"));
    Assertions.assertArrayEquals(new String[] { "v31", "v32" }, request.getParameterValues("p3"));
  }

  @Test
  public void test_headers() {
    var request = new MockHttpServletRequest(servletContext);
    request.setHeaderValues("h1", "v1");
    request.setHeaderValues("h2", "v21", "v22");

    Assertions.assertEquals("v1", request.getHeader("h1"));
    Assertions.assertEquals("v21", request.getHeader("h2"));
    Assertions.assertEquals("v21", request.getHeaders("h2").nextElement());

    request.addSessionCookie("session-1");
    Assertions.assertEquals("session-1",
        request.getCookie(request.getServletContext().getSessionCookieConfig().getName()).getValue());
  }

  @Test
  public void test_cookies() {
    var request = new MockHttpServletRequest(servletContext);
    Assertions.assertNull(request.getCookies());

    request.addCookie(MockServletUtils.createCookie(servletContext, "cookie1", "v1", 1000));
    Assertions.assertNotNull(request.getCookies());

    var cookie = ServletUtils.getCookieValue(request, "cookie1");
    Assertions.assertEquals("v1", cookie);

    request.addSessionCookie("session-1");
    Assertions.assertEquals("session-1", request.getRequestedSessionId());
  }

  @Test
  public void test_invalidate() {
    var request = new MockHttpServletRequest(servletContext);
    request.getSession().invalidate();

    Assertions.assertNull(request.getSession(false));
    Assertions.assertNotNull(request.getSession());
  }

  @Test
  public void test_session() {
    var request = new MockHttpServletRequest(servletContext);
    Assertions.assertNotNull(request.getServletContext());

    Assertions.assertNull(request.getSession(false));
    Assertions.assertNotNull(request.getSession(true));
    Assertions.assertEquals(request.getSession(), request.getSession(true));
  }
}
