// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.results;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.appslandia.common.base.Params;
import com.appslandia.plum.base.ActionResult;
import com.appslandia.plum.base.Controller;
import com.appslandia.plum.base.ControllerBase;
import com.appslandia.plum.base.HttpGet;
import com.appslandia.plum.base.MockTestBase;

/**
 *
 * @author Loc Ha
 *
 */
public class RedirectResultTest extends MockTestBase {

  @Override
  protected void initialize() {
    container.register(TestController.class, TestController.class);
  }

  @Test
  public void test_testAction() {
    try {
      executeCurrent("GET", "http://localhost/app/test/test-action");

      Assertions.assertEquals(302, getCurrentResponse().getStatus());

      var location = getCurrentResponse().getHeader("Location");
      Assertions.assertNotNull(location);
      Assertions.assertEquals("/app/test?encodeRedirectURL=true", location);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_testAction_params() {
    try {
      executeCurrent("GET", "http://localhost/app/test/test-action_params");

      Assertions.assertEquals(302, getCurrentResponse().getStatus());

      var location = getCurrentResponse().getHeader("Location");
      Assertions.assertNotNull(location);
      Assertions.assertEquals("/app/test?p1=v1&encodeRedirectURL=true", location);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Controller("test")
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
      return new RedirectResult("index", null, new Params().set("p1", "v1"));
    }
  }
}
