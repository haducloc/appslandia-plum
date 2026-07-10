// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.appslandia.plum.mem.MemModules;
import com.appslandia.plum.mocks.MockAppConfig;
import com.appslandia.plum.mocks.MockServletUtils;

import jakarta.servlet.http.HttpServletRequest;

/**
 *
 * @author Loc Ha
 *
 */
public class HttpStatus403BasicTest extends MockTestBase {

  @Override
  protected void initialize() {
    container.register(TestController.class, TestController.class);

    var appConfig = container.getObject(MockAppConfig.class);
    appConfig.set(AppConfig.CONFIG_DEFAULT_MODULE, MemModules.MEM_BASIC);
  }

  @Test
  public void test_testManager_unAuthenticated() {
    try {
      executeCurrent("GET", "http://localhost/app/testController/testManager");
      Assertions.assertEquals(401, getCurrentResponse().getStatus());

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_testManager_unAuthorized() {
    try {
      getCurrentRequest().setHeader("Authorization",
          "Basic " + MockServletUtils.createBasicCredential("user1", "pass1"));

      executeCurrent("GET", "http://localhost/app/testController/testManager");
      Assertions.assertEquals(403, getCurrentResponse().getStatus());

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_testManager_authorized() {
    try {
      getCurrentRequest().setHeader("Authorization",
          "Basic " + MockServletUtils.createBasicCredential("manager1", "pass1"));

      executeCurrent("GET", "http://localhost/app/testController/testManager");
      Assertions.assertEquals(200, getCurrentResponse().getStatus());

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_testUser1_authorized() {
    try {
      getCurrentRequest().setHeader("Authorization",
          "Basic " + MockServletUtils.createBasicCredential("user1", "pass1"));

      executeCurrent("GET", "http://localhost/app/testController/testUser1");
      Assertions.assertEquals(200, getCurrentResponse().getStatus());

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_testForbiddenException_unAuthorized() {
    try {
      getCurrentRequest().setHeader("Authorization",
          "Basic " + MockServletUtils.createBasicCredential("user1", "pass1"));

      executeCurrent("GET", "http://localhost/app/testController/testForbiddenException");
      Assertions.assertEquals(403, getCurrentResponse().getStatus());

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Controller("testController")
  @Module(MemModules.MEM_BASIC)
  public static class TestController extends ControllerBase {

    @HttpGet
    @Authorize(roles = { "manager" })
    public void testManager() throws Exception {
    }

    @HttpGet
    @Authorize
    public void testUser1() throws Exception {
    }

    @HttpGet
    @Authorize
    public void testForbiddenException(HttpServletRequest request) throws Exception {
      if (!request.isUserInRole("manager")) {
        throw new ForbiddenException("manager is required");
      }
    }
  }
}
