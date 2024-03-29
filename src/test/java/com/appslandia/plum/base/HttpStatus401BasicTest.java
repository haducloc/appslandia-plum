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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.appslandia.plum.mocks.MemUserDatabase;
import com.appslandia.plum.mocks.MockServletUtils;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class HttpStatus401BasicTest extends MockTestBase {

  @Override
  protected void initialize() {
    container.register(TestController.class, TestController.class);

    MemUserDatabase memUserDatabase = container.getObject(MemUserDatabase.class);
    memUserDatabase.addUser("user1", "password");
  }

  @Test
  public void test_testAction_unAuthenticated() {
    try {
      executeCurrent("GET", "http://localhost/app/testController/testAction");

      Assertions.assertEquals(401, getCurrentResponse().getStatus());

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_testAction_authenticated() {
    try {
      getCurrentRequest().setHeader("Authorization",
          "Basic " + MockServletUtils.createBasicCredential("user1", "password"));

      executeCurrent("GET", "http://localhost/app/testController/testAction");

      Assertions.assertEquals(200, getCurrentResponse().getStatus());

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Controller("testController")
  public static class TestController {

    @HttpGet
    @Authorize
    public void testAction() throws Exception {
    }
  }
}
