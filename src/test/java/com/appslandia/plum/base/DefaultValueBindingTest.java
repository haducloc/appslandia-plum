// The MIT License (MIT)
// Copyright © 2015 Loc Ha

// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:

// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.

// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

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
  public static class TestController {

    @HttpGet
    public void testAction(@BindValue(defval = "en") String language, RequestWrapper request) throws Exception {
      request.store("language", language);
    }

    @HttpPost
    public void testUserModel(@BindModel UserModel model, RequestWrapper request) throws Exception {
      request.storeModel(model);
    }
  }

  public static class UserModel {

    @BindValue(defval = "en")
    protected String language;

    public String getLanguage() {
      return language;
    }

    public void setLanguage(String language) {
      this.language = language;
    }
  }
}
