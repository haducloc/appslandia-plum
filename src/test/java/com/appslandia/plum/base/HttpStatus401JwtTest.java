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

/**
 *
 * @author Loc Ha
 *
 */
public class HttpStatus401JwtTest extends MockTestBase {

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
      initAccessToken(getCurrentRequest(), new JwtPayload().set(AuthPrincipal.ATTRIBUTE_SUB, "sub1"));

      executeCurrent("GET", "http://localhost/app/testController/testAction");
      Assertions.assertEquals(200, getCurrentResponse().getStatus());

      var principal = (AuthPrincipal) getCurrentRequest().getUserPrincipal();
      Assertions.assertEquals("memJwt", principal.getModule());

      Assertions.assertNotNull(principal.getStoreId());
      Assertions.assertTrue(principal.getStoreId().contains("MemJwtStore"));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Controller("testController")
  @Module(MemModules.MEM_JWT)
  public static class TestController extends ControllerBase {

    @HttpGet
    @Authorize
    public void testAction() throws Exception {
    }
  }
}
