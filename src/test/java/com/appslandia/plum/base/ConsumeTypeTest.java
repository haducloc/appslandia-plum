// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.appslandia.common.json.GsonProcessor;
import com.appslandia.common.utils.ContentTypes;

/**
 *
 * @author Loc Ha
 *
 */
public class ConsumeTypeTest extends MockTestBase {

  @Override
  protected void initialize() {
    container.register(TestController.class, TestController.class);
  }

  @Test
  public void test_postAction() {
    try {
      executeCurrent("POST", "http://localhost/app/testController/postAction");

      Assertions.assertEquals(200, getCurrentResponse().getStatus());

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_postJson() {
    try {
      getCurrentRequest().setContentType(ContentTypes.APP_JSON);

      executeCurrent("POST", "http://localhost/app/testController/postJson");

      Assertions.assertEquals(200, getCurrentResponse().getStatus());

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_postJson_415() {
    try {
      getCurrentRequest().setContentType(ContentTypes.APP_XML);

      executeCurrent("POST", "http://localhost/app/testController/postJson");

      Assertions.assertEquals(415, getCurrentResponse().getStatus());

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_loginAction() {
    try {
      getCurrentRequest().setContentType(ContentTypes.APP_JSON);
      getCurrentRequest().setContent(
          new GsonProcessor().toString(new LoginModel("user1", "password1")).getBytes(StandardCharsets.UTF_8));

      executeCurrent("POST", "http://localhost/app/testController/loginAction");

      Assertions.assertEquals(200, getCurrentResponse().getStatus());

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_loginAction_415() {
    try {
      getCurrentRequest().setContentType(ContentTypes.APP_XML);

      executeCurrent("POST", "http://localhost/app/testController/loginAction");

      Assertions.assertEquals(415, getCurrentResponse().getStatus());

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Controller("testController")
  public static class TestController extends ControllerBase {

    @HttpPost
    public void postAction() throws Exception {
    }

    @HttpPost
    @ConsumeType(ContentTypes.APP_JSON)
    public void postJson() throws Exception {
    }

    @HttpPost
    public void loginAction(@BindModel(BindSource.JSON_BODY) LoginModel model) throws Exception {
    }
  }

  public static class LoginModel {

    public String userName;
    public String password;

    public LoginModel() {
    }

    public LoginModel(String userName, String password) {
      this.userName = userName;
      this.password = password;
    }
  }
}
