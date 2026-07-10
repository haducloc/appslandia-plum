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
public class HttpStatus404Test extends MockTestBase {

  @Override
  protected void initialize() {
    container.register(TestController.class, TestController.class);
  }

  @Test
  public void test_notFound() {
    try {
      executeCurrent("GET", "http://localhost/app/testController/notAction");

      Assertions.assertEquals(404, getCurrentResponse().getStatus());

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_actionNoPathParams_invalidPathParamsAdded() {
    try {
      executeCurrent("GET", "http://localhost/app/testController/actionNoPathParams/v1");

      Assertions.assertEquals(404, getCurrentResponse().getStatus());

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_actionWithPathParams_pathParamsMissed() {
    try {
      executeCurrent("GET", "http://localhost/app/testController/actionWithPathParams/v1");

      Assertions.assertEquals(404, getCurrentResponse().getStatus());

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_actionWithPathParams_invalidPathParamsFormat() {
    try {
      executeCurrent("GET", "http://localhost/app/testController/actionWithPathParams/v1/v2");

      Assertions.assertEquals(404, getCurrentResponse().getStatus());

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_actionNotFoundException() {
    try {
      executeCurrent("GET", "http://localhost/app/testController/actionNotFoundException");

      Assertions.assertEquals(404, getCurrentResponse().getStatus());

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Controller("testController")
  public static class TestController extends ControllerBase {

    @HttpGet
    public void actionNoPathParams() throws Exception {
    }

    @HttpGet
    @PathParams("/{p1}/{p2}-{p3}")
    public void actionWithPathParams() throws Exception {
    }

    @HttpGet
    public void actionNotFoundException() throws Exception {
      throw new NotFoundException("actionNotFoundException");
    }
  }
}
