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
public class HttpStatus405Test extends MockTestBase {

  @Override
  protected void initialize() {
    container.register(TestController.class, TestController.class);
  }

  @Test
  public void test_testAction_methodNotAllowed() {
    try {
      executeCurrent("POST", "http://localhost/app/testController/testAction");

      Assertions.assertEquals(405, getCurrentResponse().getStatus());

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_testAction_OPTIONS() {
    try {
      executeCurrent("OPTIONS", "http://localhost/app/testController/testAction");

      Assertions.assertEquals(405, getCurrentResponse().getStatus());

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_testAction_TRACE() {
    try {
      executeCurrent("TRACE", "http://localhost/app/testController/testAction");

      Assertions.assertEquals(405, getCurrentResponse().getStatus());

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_testPostAction_HEAD() {
    try {
      executeCurrent("HEAD", "http://localhost/app/testController/testPostAction");

      Assertions.assertEquals(405, getCurrentResponse().getStatus());

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Controller("testController")
  public static class TestController {

    @HttpGet
    public void testAction() throws Exception {
    }

    @HttpPost
    public void testPostAction() throws Exception {
    }
  }
}
