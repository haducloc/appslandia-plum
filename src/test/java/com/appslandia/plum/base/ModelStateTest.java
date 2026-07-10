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
public class ModelStateTest extends MockTestBase {

  @Override
  protected void initialize() {
    container.register(TestController.class, TestController.class);
  }

  @Test
  public void test_testQueryParam_valid() {
    try {
      getCurrentRequest().addParameter("userId", "100");
      executeCurrent("GET", "http://localhost/app/testController/testQueryParam");

      Assertions.assertTrue(getCurrentModelState().isValid());
      Assertions.assertEquals(200, getCurrentResponse().getStatus());

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_testQueryParam_invalid() {
    try {
      getCurrentRequest().addParameter("userId", "100x");
      executeCurrent("GET", "http://localhost/app/testController/testQueryParam");

      Assertions.assertFalse(getCurrentModelState().isValid());
      Assertions.assertEquals(200, getCurrentResponse().getStatus());

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_testPathParam_valid() {
    try {
      executeCurrent("GET", "http://localhost/app/testController/testPathParam/100");

      Assertions.assertTrue(getCurrentModelState().isValid());
      Assertions.assertEquals(200, getCurrentResponse().getStatus());

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_testPathParam_invalid() {
    try {
      executeCurrent("GET", "http://localhost/app/testController/testPathParam/100x");

      Assertions.assertFalse(getCurrentModelState().isValid());

      // NotFoundException
      Assertions.assertEquals(404, getCurrentResponse().getStatus());

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Controller("testController")
  public static class TestController extends ControllerBase {

    @HttpGet
    @PathParams("/{userId}")
    public void testPathParam(int userId) throws Exception {
    }

    @HttpGet
    public void testQueryParam(int userId) throws Exception {
    }
  }
}
