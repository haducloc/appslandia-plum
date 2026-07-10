// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.appslandia.plum.utils.ServletUtils;

/**
 *
 * @author Loc Ha
 *
 */
public class DefaultValueBindingTest extends MockTestBase {

  @Override
  protected void initialize() {
    container.register(TestController.class, TestController.class);
  }

  @Test
  public void test_testAction() {
    try {
      executeCurrent("GET", "http://localhost/app/testController/testAction");

      var userLanguage = (String) getCurrentRequest().getAttribute("language");
      Assertions.assertEquals("en", userLanguage);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_testAction_language() {
    try {
      getCurrentRequest().addParameter("language", "vi");
      executeCurrent("GET", "http://localhost/app/testController/testAction");

      var userLanguage = (String) getCurrentRequest().getAttribute("language");
      Assertions.assertEquals("vi", userLanguage);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_testUserModel() {
    try {
      executeCurrent("POST", "http://localhost/app/testController/testUserModel");

      var userModel = (UserModel) getCurrentRequest().getAttribute(ServletUtils.REQUEST_ATTRIBUTE_MODEL);
      Assertions.assertNotNull(userModel);
      Assertions.assertEquals("en", userModel.getLanguage());

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_testUserModel_language() {
    try {
      getCurrentRequest().addParameter("language", "vi");
      executeCurrent("POST", "http://localhost/app/testController/testUserModel");

      var userModel = (UserModel) getCurrentRequest().getAttribute(ServletUtils.REQUEST_ATTRIBUTE_MODEL);
      Assertions.assertNotNull(userModel);
      Assertions.assertEquals("vi", userModel.getLanguage());

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Controller("testController")
  public static class TestController extends ControllerBase {

    @HttpGet
    public void testAction(@BindField(defaultValue = "en") String language, HttpRequestFacade request)
        throws Exception {
      request.store("language", language);
    }

    @HttpPost
    public void testUserModel(@BindModel UserModel model, HttpRequestFacade request) throws Exception {
      request.storeModel(model);
    }
  }

  public static class UserModel {

    @BindField(defaultValue = "en")
    protected String language;

    public String getLanguage() {
      return language;
    }

    public void setLanguage(String language) {
      this.language = language;
    }
  }
}
