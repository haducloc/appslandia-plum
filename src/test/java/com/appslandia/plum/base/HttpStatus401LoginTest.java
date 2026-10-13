// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.appslandia.common.base.Out;
import com.appslandia.plum.mem.MemModules;
import com.appslandia.plum.mocks.MockAppConfig;

import jakarta.inject.Inject;
import jakarta.security.auth.message.AuthException;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
public class HttpStatus401LoginTest extends MockTestBase {

  @Override
  protected void initialize() {
    container.register(TestController.class, TestController.class);

    var appConfig = container.getObject(MockAppConfig.class);
    appConfig.set("memForm.login_uri", "/test/loginAction");
  }

  @Test
  public void test_testAction_unAuthenticated() {
    try {
      executeCurrent("GET", "http://localhost/app/test/test-action");

      Assertions.assertEquals(302, getCurrentResponse().getStatus());
      var location = getCurrentResponse().getHeader("Location");
      Assertions.assertNotNull(location);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_loginAction() {
    try {
      executeCurrent("POST", "http://localhost/app/test/login-action");

      Assertions.assertNotNull(getCurrentRequest().getUserPrincipal());
      Assertions.assertEquals(200, getCurrentResponse().getStatus());

      var principal = (AuthPrincipal) getCurrentRequest().getUserPrincipal();
      Assertions.assertEquals("memForm", principal.getModule());

      Assertions.assertNotNull(principal.getStoreId());
      Assertions.assertTrue(principal.getStoreId().contains("MemUserStore"));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Controller("test")
  @Module(MemModules.MEM_FORM)
  public static class TestController extends ControllerBase {

    @Inject
    protected AuthContext authContext;

    @HttpGet
    @Authorize
    public void testAction() throws Exception {
    }

    @HttpGetPost
    public void loginAction(HttpActionRequest request, HttpServletResponse response) throws Exception {
      if (request.isGetOrHead()) {
        return;
      }
      var invalidCode = new Out<String>();
      var credential = new UsernamePasswordCredential("user1", "pass1");

      var isValid = authContext.authenticate(request, response,
          new AuthParameters().credential(credential).module(request.getRequestContext().getModule()), invalidCode);

      if (!isValid) {
        throw new AuthException(invalidCode.get());
      }
    }
  }
}
