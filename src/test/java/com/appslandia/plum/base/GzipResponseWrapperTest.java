// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.appslandia.plum.mocks.MockHttpServletResponse;
import com.appslandia.plum.mocks.MockServletContext;
import com.appslandia.plum.mocks.MockSessionCookieConfig;
import com.appslandia.plum.utils.TestUtils;

/**
 *
 * @author Loc Ha
 *
 */
public class GzipResponseWrapperTest {

  MockServletContext servletContext;

  @BeforeEach
  public void initServletContext() {
    servletContext = new MockServletContext(new MockSessionCookieConfig());
  }

  @Test
  public void test() {
    var response = new MockHttpServletResponse(servletContext);
    response.setCharacterEncoding("UTF-8");

    var wrapper = new GzipResponseWrapper(response, 10);

    try {
      wrapper.getWriter().write("123456789");
      wrapper.finishResponse();
      Assertions.assertNull(response.getHeader("Content-Encoding"));

      var content = response.getContent().toString(StandardCharsets.UTF_8);
      Assertions.assertEquals("123456789", content);

    } catch (IOException ex) {
      Assertions.fail(ex);
    }
  }

  @Test
  public void test_gzip() {
    var response = new MockHttpServletResponse(servletContext);
    response.setCharacterEncoding("UTF-8");

    var wrapper = new GzipResponseWrapper(response, 10);

    try {
      wrapper.getWriter().write("12345678901");
      wrapper.finishResponse();

      var ce = response.getHeader("Content-Encoding");
      Assertions.assertEquals("gzip", ce);

      var content = response.getContent().toString(StandardCharsets.UTF_8);
      Assertions.assertNotEquals("12345678901", content);

      var actualContent = TestUtils.ungzipToString(response.getContent());
      Assertions.assertEquals("12345678901", actualContent);

    } catch (IOException ex) {
      Assertions.fail(ex);
    }
  }
}
