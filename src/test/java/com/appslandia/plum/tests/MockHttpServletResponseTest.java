// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.tests;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.appslandia.plum.mocks.MockHttpServletResponse;
import com.appslandia.plum.mocks.MockServletContext;
import com.appslandia.plum.mocks.MockServletUtils;
import com.appslandia.plum.mocks.MockSessionCookieConfig;

/**
 *
 * @author Loc Ha
 *
 */
public class MockHttpServletResponseTest {

  MockServletContext servletContext;

  @BeforeEach
  public void initServletContext() {
    servletContext = new MockServletContext(new MockSessionCookieConfig());
  }

  @Test
  public void test() {
    var response = new MockHttpServletResponse(servletContext);
    response.setCharacterEncoding("UTF-8");
    Assertions.assertEquals("UTF-8", response.getCharacterEncoding());

    try {
      response.flushBuffer();
    } catch (IOException ex) {
    }
    Assertions.assertTrue(response.isCommitted());

    response.setContentType("text/html");
    Assertions.assertEquals("text/html", response.getContentType());
  }

  @Test
  public void test_headers() {
    var response = new MockHttpServletResponse(servletContext);
    response.addHeaderValues("h1", "v1");
    response.addHeaderValues("h2", "v21", "v22");

    Assertions.assertEquals("v1", response.getHeader("h1"));
    response.addHeaderValues("h1", "v11");
    Assertions.assertTrue(response.getHeaders("h1").size() == 2);

    Assertions.assertEquals("v21", response.getHeader("h2"));
    Assertions.assertEquals("v21", response.getHeaders("h2").iterator().next());
  }

  @Test
  public void test_sendError() {
    var response = new MockHttpServletResponse(servletContext);
    try {
      response.sendError(500);
    } catch (IOException ex) {
      Assertions.fail(ex.getMessage());
    }
    Assertions.assertEquals(500, response.getStatus());
  }

  @Test
  public void test_cookies() {
    var response = new MockHttpServletResponse(servletContext);
    response.addCookie(MockServletUtils.createCookie(servletContext, "cookie1", "v1", 1000));

    Assertions.assertNotNull(response.getCookie("cookie1"));
    Assertions.assertEquals("v1", response.getCookie("cookie1").getValue());
  }

  @Test
  public void test_writeContent() {
    var response = new MockHttpServletResponse(servletContext);
    try {
      response.getWriter().write("data");
      response.flushBuffer();

      Assertions.assertEquals("data", response.getContent().toString(StandardCharsets.UTF_8));

    } catch (IOException ex) {
      Assertions.fail(ex.getMessage());
    }
  }
}
