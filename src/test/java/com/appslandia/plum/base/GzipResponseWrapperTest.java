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
      wrapper.finishWrapper();
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
      wrapper.finishWrapper();

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
