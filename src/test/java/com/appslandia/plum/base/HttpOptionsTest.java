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
public class HttpOptionsTest extends MockTestBase {

  @Override
  protected void initialize() {
    container.register(TestController.class, TestController.class);
  }

  @Test
  public void test_testAction() {
    try {
      executeCurrent("OPTIONS", "http://localhost/app/testController/testAction");

      Assertions.assertEquals(200, getCurrentResponse().getStatus());
      Assertions.assertNotNull(getCurrentResponse().getHeader("Allow"));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_testAuthorize() {
    try {
      executeCurrent("OPTIONS", "http://localhost/app/testController/testAuthorize");

      Assertions.assertEquals(200, getCurrentResponse().getStatus());
      Assertions.assertNotNull(getCurrentResponse().getHeader("Allow"));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_enableCorsAction() {
    try {
      executeCurrent("OPTIONS", "http://localhost/app/testController/enableActionOptions");

      Assertions.assertEquals(200, getCurrentResponse().getStatus());
      Assertions.assertEquals("GET, HEAD, OPTIONS", getCurrentResponse().getHeader("Allow"));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Controller("testController")
  public static class TestController extends ControllerBase {

    @HttpGet
    public void testAction() throws Exception {
    }

    @HttpGet
    @Authorize
    public void testAuthorize() throws Exception {
    }

    @HttpGet
    @HttpOptions
    public void enableActionOptions() throws Exception {
    }
  }
}
