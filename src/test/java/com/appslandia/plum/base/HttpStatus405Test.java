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

      Assertions.assertEquals(200, getCurrentResponse().getStatus());

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
  public static class TestController extends ControllerBase {

    @HttpGet
    public void testAction() throws Exception {
    }

    @HttpPost
    public void testPostAction() throws Exception {
    }
  }
}
