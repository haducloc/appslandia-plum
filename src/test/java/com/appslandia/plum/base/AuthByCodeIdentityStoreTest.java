// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.appslandia.common.base.Out;
import com.appslandia.plum.mem.MemModules;
import com.appslandia.plum.mem.MemUserAuthByCodeIdentityStore;

import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;

/**
 *
 * @author Loc Ha
 *
 */
public class AuthByCodeIdentityStoreTest extends MockTestBase {

  protected AuthContext authContext;

  @Override
  protected void initialize() {
    container.register(TestController.class, TestController.class);
    authContext = container.getObject(AuthContext.class);
  }

  protected SeriesToken initSeriesToken() {
    var store = container.getObject(MemUserAuthByCodeIdentityStore.class);
    var seriesToken = store.saveToken(MemModules.MEM_FORM, "user1", "code1", 10);
    return seriesToken;
  }

  @Test
  public void test_loginAction() {
    try {
      var seriesToken = initSeriesToken();
      getCurrentRequest().addParameter("series", seriesToken.getSeries().toString());
      getCurrentRequest().addParameter("token", seriesToken.getToken());
      getCurrentRequest().addParameter("code", "code1");

      // Execute testLogin
      executeCurrent("POST", "http://localhost/app/testController/testLogin");

      Assertions.assertNotNull(getCurrentRequest().getUserPrincipal());
      Assertions.assertEquals(200, getCurrentResponse().getStatus());

      var principal = (AuthPrincipal) getCurrentRequest().getUserPrincipal();
      Assertions.assertEquals(MemModules.MEM_FORM, principal.getModule());

      Assertions.assertNotNull(principal.getStoreId());
      Assertions.assertTrue(principal.getStoreId().contains("MemUserAuthByCodeStore"));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_loginAction_failed() {
    try {
      var seriesToken = initSeriesToken();
      getCurrentRequest().addParameter("series", seriesToken.getSeries().toString());
      getCurrentRequest().addParameter("token", seriesToken.getToken());
      getCurrentRequest().addParameter("code", "invalid_code");

      // Execute testLogin
      executeCurrent("POST", "http://localhost/app/testController/testLogin");

      Assertions.assertNull(getCurrentRequest().getUserPrincipal());
      Assertions.assertNull(getCurrentRequest().getRemoteUser());
      Assertions.assertNull(getCurrentRequest().getAuthType());

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Controller("testController")
  @Module(MemModules.MEM_FORM)
  public static class TestController extends ControllerBase {

    @Inject
    protected AuthContext authContext;

    @HttpGetPost
    public void testLogin(HttpRequestFacade request, HttpServletResponse response, @NotNull UUID series,
        @NotNull String token, @NotNull String code) throws Exception {

      // Authenticate
      var invalidCode = new Out<String>();
      var credential = new AuthByCodeCredential(series, token, code);
      var parameters = new AuthParameters().credential(credential).module(MemModules.MEM_FORM);

      authContext.authenticate(request, response, parameters, invalidCode);
    }
  }
}
