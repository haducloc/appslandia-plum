// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Loc Ha
 *
 */
public class HttpStatus500Test extends MockTestBase {

  @Override
  protected void initialize() {
    container.register(TestController.class, TestController.class);
  }

  @Test
  public void test_testException() {
    try {
      executeCurrent("GET", "http://localhost/app/testController/testException");

      Assertions.assertEquals(500, getCurrentResponse().getStatus());

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_testRuntimeException() {
    try {
      executeCurrent("GET", "http://localhost/app/testController/testRuntimeException");

      Assertions.assertEquals(500, getCurrentResponse().getStatus());

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Controller("testController")
  public static class TestController extends ControllerBase {

    @HttpGet
    public void testException() throws Exception {
      throw new Exception();
    }

    @HttpGet
    public void testRuntimeException() throws Exception {
      throw new RuntimeException();
    }
  }
}
