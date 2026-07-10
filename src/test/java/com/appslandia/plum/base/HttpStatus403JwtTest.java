// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.appslandia.common.jose.JwtPayload;
import com.appslandia.common.jose.JwtSigner;
import com.appslandia.common.jose.JwtToken;
import com.appslandia.plum.mem.MemModules;
import com.appslandia.plum.mem.MemVersionLiteral;
import com.appslandia.plum.mocks.MockAppConfig;
import com.appslandia.plum.mocks.MockHttpServletRequest;

import jakarta.servlet.http.HttpServletRequest;

/**
 *
 * @author Loc Ha
 *
 */
public class HttpStatus403JwtTest extends MockTestBase {

  @Override
  protected void initialize() {
    container.register(TestController.class, TestController.class);

    var appConfig = container.getObject(MockAppConfig.class);
    appConfig.set(AppConfig.CONFIG_DEFAULT_MODULE, MemModules.MEM_JWT);
  }

  void initAccessToken(MockHttpServletRequest request, JwtPayload payload) {
    var jwtSigner = container.getObject(JwtSigner.class, MemVersionLiteral.IMPL);
    var header = jwtSigner.newHeader();
    request.setHeader("Authorization", "Bearer " + jwtSigner.sign(new JwtToken(header, payload)));
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
      initAccessToken(getCurrentRequest(),
          new JwtPayload().set(AuthPrincipal.ATTRIBUTE_SUB, "user1").set(AuthPrincipal.ATTRIBUTE_ROLES, null));

      executeCurrent("GET", "http://localhost/app/testController/testManager");
      Assertions.assertEquals(403, getCurrentResponse().getStatus());

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_testManager_authorized() {
    try {
      initAccessToken(getCurrentRequest(),
          new JwtPayload().set(AuthPrincipal.ATTRIBUTE_SUB, "manager1").set(AuthPrincipal.ATTRIBUTE_ROLES, "manager"));

      executeCurrent("GET", "http://localhost/app/testController/testManager");
      Assertions.assertEquals(200, getCurrentResponse().getStatus());

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_testUser1_authorized() {
    try {
      initAccessToken(getCurrentRequest(),
          new JwtPayload().set(AuthPrincipal.ATTRIBUTE_SUB, "user1").set(AuthPrincipal.ATTRIBUTE_ROLES, null));

      executeCurrent("GET", "http://localhost/app/testController/testUser1");
      Assertions.assertEquals(200, getCurrentResponse().getStatus());

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_testForbiddenException_unAuthorized() {
    try {
      initAccessToken(getCurrentRequest(),
          new JwtPayload().set(AuthPrincipal.ATTRIBUTE_SUB, "user1").set(AuthPrincipal.ATTRIBUTE_ROLES, null));

      executeCurrent("GET", "http://localhost/app/testController/testForbiddenException");
      Assertions.assertEquals(403, getCurrentResponse().getStatus());

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Controller("testController")
  @Module(MemModules.MEM_JWT)
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
