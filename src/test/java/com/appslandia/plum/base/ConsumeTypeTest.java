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

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.appslandia.common.json.GsonProcessor;
import com.appslandia.common.utils.MimeTypes;

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
      getCurrentRequest().setContentType(MimeTypes.APP_JSON);

      executeCurrent("POST", "http://localhost/app/testController/postJson");

      Assertions.assertEquals(200, getCurrentResponse().getStatus());

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_postJson_415() {
    try {
      getCurrentRequest().setContentType(MimeTypes.APP_XML);

      executeCurrent("POST", "http://localhost/app/testController/postJson");

      Assertions.assertEquals(415, getCurrentResponse().getStatus());

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_loginAction() {
    try {
      getCurrentRequest().setContentType(MimeTypes.APP_JSON);
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
      getCurrentRequest().setContentType(MimeTypes.APP_XML);

      executeCurrent("POST", "http://localhost/app/testController/loginAction");

      Assertions.assertEquals(415, getCurrentResponse().getStatus());

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Controller("testController")
  public static class TestController {

    @HttpPost
    public void postAction() throws Exception {
    }

    @HttpPost
    @ConsumeType(MimeTypes.APP_JSON)
    public void postJson() throws Exception {
    }

    @HttpPost
    public void loginAction(@BindModel(ModelSource.JSON_BODY) LoginModel model) throws Exception {
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
