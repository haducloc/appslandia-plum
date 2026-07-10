// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.results;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.appslandia.plum.base.ActionResult;
import com.appslandia.plum.base.AppConfig;
import com.appslandia.plum.base.Controller;
import com.appslandia.plum.base.ControllerBase;
import com.appslandia.plum.base.HttpGet;
import com.appslandia.plum.base.MockTestBase;

/**
 *
 * @author Loc Ha
 *
 */
public class RedirectResultNoSessionTest extends MockTestBase {

  @Override
  protected void initialize() {
    container.register(TestController.class, TestController.class);

    container.getAppConfig().set(AppConfig.CONFIG_ENABLE_SESSION, false);
  }

  @Test
  public void test_testAction() {
    try {
      executeCurrent("GET", "http://localhost/app/testController/testAction");

      Assertions.assertEquals(302, getCurrentResponse().getStatus());

      var location = getCurrentResponse().getHeader("Location");
      Assertions.assertNotNull(location);
      Assertions.assertEquals("/app/testController", location);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_testAction_params() {
    try {
      executeCurrent("GET", "http://localhost/app/testController/testAction_params");

      Assertions.assertEquals(302, getCurrentResponse().getStatus());

      var location = getCurrentResponse().getHeader("Location");
      Assertions.assertNotNull(location);
      Assertions.assertEquals("/app/testController?p1=v1", location);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_redirectToIndex() {
    try {
      executeCurrent("GET", "http://localhost/app/testController/redirectToIndex");

      Assertions.assertEquals(302, getCurrentResponse().getStatus());

      var location = getCurrentResponse().getHeader("Location");
      Assertions.assertNotNull(location);
      Assertions.assertEquals("/app/testController", location);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_redirectToRoot() {
    try {
      executeCurrent("GET", "http://localhost/app/testController/redirectToRoot");

      Assertions.assertEquals(302, getCurrentResponse().getStatus());

      var location = getCurrentResponse().getHeader("Location");
      Assertions.assertNotNull(location);
      Assertions.assertEquals("/app", location);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Controller("testController")
  public static class TestController extends ControllerBase {

    @HttpGet
    public void index() throws Exception {
    }

    @HttpGet
    public ActionResult testAction() throws Exception {
      return new RedirectResult("index", null);
    }

    @HttpGet
    public ActionResult testAction_params() throws Exception {
      return new RedirectResult("index", null).query("p1", "v1");
    }

    @HttpGet
    public ActionResult redirectToIndex() throws Exception {
      return RedirectResult.INDEX;
    }

    @HttpGet
    public ActionResult redirectToRoot() throws Exception {
      return RedirectResult.ROOT;
    }
  }
}
