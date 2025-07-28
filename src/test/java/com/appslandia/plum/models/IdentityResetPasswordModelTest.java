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

package com.appslandia.plum.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.appslandia.plum.base.BindModel;
import com.appslandia.plum.base.Controller;
import com.appslandia.plum.base.HttpPost;
import com.appslandia.plum.base.MockTestBase;
import com.appslandia.plum.base.RequestWrapper;
import com.appslandia.plum.utils.ServletUtils;

/**
 *
 * @author Loc Ha
 *
 */
public class IdentityResetPasswordModelTest extends MockTestBase {

  @Override
  protected void initialize() {
    container.register(TestController.class, TestController.class);
  }

  @Test
  public void test_resetpwd() {
    try {
      getCurrentRequest().addParameter("newPassword", "123@Test");
      getCurrentRequest().addParameter("confirmPassword", "124@Test");

      executeCurrent("POST", "http://localhost/app/testController/resetpwd");

      var model = (IdentityResetPasswordModel) getCurrentRequest().getAttribute(ServletUtils.REQUEST_ATTRIBUTE_MODEL);
      Assertions.assertNotNull(model);
      Assertions.assertFalse(getCurrentModelState().isValid("confirmPassword"));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Controller("testController")
  public static class TestController {

    @HttpPost
    public void resetpwd(@BindModel IdentityResetPasswordModel model, RequestWrapper request) throws Exception {
      request.storeModel(model);
    }
  }
}
