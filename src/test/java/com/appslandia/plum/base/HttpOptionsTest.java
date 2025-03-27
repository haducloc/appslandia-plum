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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Loc Ha
 *
 */
public class HttpOptionsTest extends MockTestBase {

  @Override
  protected void initialize() {
    container.register(TestController.class, TestController.class);
  }

  @Test
  public void test_testAction() {
    try {
      executeCurrent("OPTIONS", "http://localhost/app/testController/testAction");

      Assertions.assertEquals(405, getCurrentResponse().getStatus());
      Assertions.assertNull(getCurrentResponse().getHeader("Allow"));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_testAuthorize() {
    try {
      executeCurrent("OPTIONS", "http://localhost/app/testController/testAuthorize");

      Assertions.assertEquals(405, getCurrentResponse().getStatus());
      Assertions.assertNull(getCurrentResponse().getHeader("Allow"));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_enableCorsAction() {
    try {
      executeCurrent("OPTIONS", "http://localhost/app/testController/enableCorsAction");

      Assertions.assertEquals(403, getCurrentResponse().getStatus());
      Assertions.assertNull(getCurrentResponse().getHeader("Allow"));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Controller("testController")
  public static class TestController {

    @HttpGet
    public void testAction() throws Exception {
    }

    @HttpGet
    @Authorize
    public void testAuthorize() throws Exception {
    }

    @HttpGet
    @EnableCors("testCors")
    public void enableCorsAction() throws Exception {
    }
  }
}
