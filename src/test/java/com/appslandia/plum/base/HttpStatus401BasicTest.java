// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.appslandia.plum.mem.MemModules;
import com.appslandia.plum.mocks.MockAppConfig;
import com.appslandia.plum.mocks.MockServletUtils;

/**
 *
 * @author Loc Ha
 *
 */
public class HttpStatus401BasicTest extends MockTestBase {

  @Override
  protected void initialize() {
    container.register(TestController.class, TestController.class);

    var appConfig = container.getObject(MockAppConfig.class);
    appConfig.set(AppConfig.CONFIG_DEFAULT_MODULE, MemModules.MEM_BASIC);
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
          "Basic " + MockServletUtils.createBasicCredential("user1", "pass1"));

      executeCurrent("GET", "http://localhost/app/testController/testAction");
      Assertions.assertEquals(200, getCurrentResponse().getStatus());

      var principal = (AuthPrincipal) getCurrentRequest().getUserPrincipal();
      Assertions.assertEquals("memBasic", principal.getModule());

      Assertions.assertNotNull(principal.getStoreId());
      Assertions.assertTrue(principal.getStoreId().contains("MemUserStore"));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Controller("testController")
  @Module(MemModules.MEM_BASIC)
  public static class TestController extends ControllerBase {

    @HttpGet
    @Authorize
    public void testAction() throws Exception {
    }
  }
}
