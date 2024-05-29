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

package com.appslandia.plum.base;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.appslandia.plum.mocks.MockHttpServletResponse;
import com.appslandia.plum.mocks.MockServletContext;
import com.appslandia.plum.mocks.MockSessionCookieConfig;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class ContentResponseWrapperTest {

  MockServletContext servletContext;

  @BeforeEach
  public void beforeEachTest() {
    servletContext = new MockServletContext(new MockSessionCookieConfig());
  }

  @Test
  public void test() {
    try {
      MockHttpServletResponse response = new MockHttpServletResponse(servletContext);
      ContentResponseWrapper wrapper = new ContentResponseWrapper(response, true);

      wrapper.getOutputStream().write("data".getBytes(StandardCharsets.UTF_8));
      wrapper.finishWrapper();

      Assertions.assertEquals("data", wrapper.getContent().toString(StandardCharsets.UTF_8.name()));

    } catch (IOException ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @SuppressWarnings("resource")
  @Test
  public void test_reset() {
    try {
      MockHttpServletResponse response = new MockHttpServletResponse(servletContext);
      ContentResponseWrapper wrapper = new ContentResponseWrapper(response, true);

      wrapper.getOutputStream().write("data".getBytes(StandardCharsets.UTF_8));
      wrapper.setHeader("testHeader", "testValue");
      wrapper.reset();

      Assertions.assertNull(wrapper.getHeader("testHeader"));
      Assertions.assertEquals(0, wrapper.getContent().size());
      Assertions.assertEquals(0, wrapper.getContent().toByteArray().length);

    } catch (IOException ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @SuppressWarnings("resource")
  @Test
  public void test_resetBuffer() {
    try {
      MockHttpServletResponse response = new MockHttpServletResponse(servletContext);
      ContentResponseWrapper wrapper = new ContentResponseWrapper(response, true);

      wrapper.getOutputStream().write("data".getBytes(StandardCharsets.UTF_8));
      wrapper.resetBuffer();

      Assertions.assertEquals(0, wrapper.getContent().size());
      Assertions.assertEquals(0, wrapper.getContent().toByteArray().length);

    } catch (IOException ex) {
      Assertions.fail(ex.getMessage());
    }
  }
}
