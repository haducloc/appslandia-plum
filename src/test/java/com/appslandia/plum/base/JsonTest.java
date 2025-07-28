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

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Loc Ha
 *
 */
public class JsonTest extends MockTestBase {

  @Override
  protected void initialize() {
    container.register(TestController.class, TestController.class);
  }

  @Test
  public void test_testInt() {
    try {
      executeCurrent("GET", "http://localhost/app/testController/testInt");

      var content = getCurrentResponse().getContent().toString(StandardCharsets.UTF_8);
      Assertions.assertEquals("100", content);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_testNull() {
    try {
      executeCurrent("GET", "http://localhost/app/testController/testNull");

      var content = getCurrentResponse().getContent().toString(StandardCharsets.UTF_8);
      Assertions.assertEquals("null", content);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_testModel() {
    try {
      executeCurrent("GET", "http://localhost/app/testController/testModel");

      var content = getCurrentResponse().getContent().toString(StandardCharsets.UTF_8);
      Assertions.assertTrue(content.contains("\"userId\":1"));
      Assertions.assertTrue(content.contains("\"name\":\"user1\""));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Controller("testController")
  public static class TestController {

    @HttpGet
    public int testInt() throws Exception {
      return 100;
    }

    @HttpGet
    public Integer testNull() throws Exception {
      return null;
    }

    @HttpGet
    public UserModel testModel() throws Exception {
      return new UserModel(1, "user1");
    }
  }

  public static class UserModel {

    public int userId;
    public String name;

    public UserModel(int userId, String name) {
      this.userId = userId;
      this.name = name;
    }
  }
}
