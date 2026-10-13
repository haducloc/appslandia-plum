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

/**
 *
 * @author Loc Ha
 *
 */
public class MemoryResponseWrapperTest {

  MockServletContext servletContext;

  @BeforeEach
  public void initServletContext() {
    servletContext = new MockServletContext(new MockSessionCookieConfig());
  }

  @Test
  public void test() {
    try {
      var response = new MockHttpServletResponse(servletContext);
      var wrapper = new MemoryResponseWrapper(response, true, new MemoryStream(32));

      wrapper.getOutputStream().write("data".getBytes(StandardCharsets.UTF_8));
      wrapper.finishResponse();

      Assertions.assertEquals("data", wrapper.getContent().toString(StandardCharsets.UTF_8.name()));

    } catch (IOException ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_reset() {
    try {
      var response = new MockHttpServletResponse(servletContext);
      var wrapper = new MemoryResponseWrapper(response, true, new MemoryStream(32));

      wrapper.getOutputStream().write("data".getBytes(StandardCharsets.UTF_8));
      wrapper.setHeader("testHeader", "testValue");
      wrapper.reset();

      Assertions.assertNull(wrapper.getHeader("testHeader"));
      Assertions.assertEquals(0, wrapper.getContent().length());
      Assertions.assertEquals(0, wrapper.getContent().toByteArray().length);

    } catch (IOException ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_resetBuffer() {
    try {
      var response = new MockHttpServletResponse(servletContext);
      var wrapper = new MemoryResponseWrapper(response, true, new MemoryStream(32));

      wrapper.getOutputStream().write("data".getBytes(StandardCharsets.UTF_8));
      wrapper.resetBuffer();

      Assertions.assertEquals(0, wrapper.getContent().length());
      Assertions.assertEquals(0, wrapper.getContent().toByteArray().length);

    } catch (IOException ex) {
      Assertions.fail(ex.getMessage());
    }
  }
}
