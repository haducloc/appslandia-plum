// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.appslandia.common.base.MemoryStream;
import com.appslandia.plum.mocks.MockHttpServletResponse;
import com.appslandia.plum.mocks.MockServletContext;
import com.appslandia.plum.mocks.MockSessionCookieConfig;
import com.appslandia.plum.utils.TestUtils;

/**
 *
 * @author Loc Ha
 *
 */
public class DeflateResponseWrapperTest {

  MockServletContext servletContext;

  @BeforeEach
  public void initServletContext() {
    servletContext = new MockServletContext(new MockSessionCookieConfig());
  }

  @Test
  public void test() {
    var response = new MockHttpServletResponse(servletContext);
    response.setCharacterEncoding("UTF-8");

    var wrapper = new DeflateResponseWrapper(response, 10, new MemoryStream(32));

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
  public void test_deflate() {
    var response = new MockHttpServletResponse(servletContext);
    response.setCharacterEncoding("UTF-8");

    var wrapper = new DeflateResponseWrapper(response, 10, new MemoryStream(32));

    try {
      wrapper.getWriter().write("12345678901");
      wrapper.finishResponse();

      var ce = response.getHeader("Content-Encoding");
      Assertions.assertEquals("deflate", ce);

      var content = response.getContent().toString(StandardCharsets.UTF_8);
      Assertions.assertNotEquals("12345678901", content);

      var actualContent = TestUtils.undeflateToString(response.getContent());
      Assertions.assertEquals("12345678901", actualContent);

    } catch (IOException ex) {
      Assertions.fail(ex);
    }
  }
}
