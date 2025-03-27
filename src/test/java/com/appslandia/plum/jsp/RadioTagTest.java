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

package com.appslandia.plum.jsp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.appslandia.common.utils.NormalizeUtils;
import com.appslandia.plum.base.Controller;
import com.appslandia.plum.base.HttpGet;
import com.appslandia.plum.base.MockTestBase;
import com.appslandia.plum.mocks.MockJspContext;
import com.appslandia.plum.utils.ServletUtils;
import com.appslandia.plum.utils.TestUtils;

import jakarta.validation.constraints.NotNull;

/**
 *
 * @author Loc Ha
 *
 */
public class RadioTagTest extends MockTestBase {

  RadioTag tag = new RadioTag();
  TestModel model = new TestModel();

  @BeforeAll
  public static void beforeAllTests() {
    TestUtils.initExpressionEvaluator();
  }

  @Override
  protected void initialize() {
    container.register(TestController.class, TestController.class);
  }

  @BeforeEach
  public void beforeEachTest() {
    tag.setJspContext(new MockJspContext(getCurrentRequest(), getCurrentResponse()));
    getCurrentRequest().setAttribute(ServletUtils.REQUEST_ATTRIBUTE_MODEL, model);

    executeCurrent("GET", "http://localhost/app/testController/index");
  }

  @Test
  public void test() {
    try {
      model.setUserType(1);

      tag.setPath("model.userType");
      tag.setCodeValue(1);

      tag.doTag();
      String html = tag.getPageContext().getOut().toString().stripTrailing();

      Assertions.assertEquals(
          "<input id=\"userType\" type=\"radio\" name=\"userType\" value=\"1\" checked=\"checked\" />",
          NormalizeUtils.toSingleLine(html));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_unchecked() {
    try {
      model.setUserType(2);

      tag.setPath("model.userType");
      tag.setCodeValue("1");

      tag.doTag();
      String html = tag.getPageContext().getOut().toString().stripTrailing();

      Assertions.assertEquals("<input id=\"userType\" type=\"radio\" name=\"userType\" value=\"1\" />",
          NormalizeUtils.toSingleLine(html));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_checked_readonly() {
    try {
      model.setUserType(1);

      tag.setPath("model.userType");
      tag.setCodeValue("1");
      tag.setReadonly(true);

      tag.doTag();
      String html = tag.getPageContext().getOut().toString().stripTrailing();

      Assertions.assertEquals(
          "<input id=\"userType\" type=\"radio\" name=\"userType\" value=\"1\" checked=\"checked\" disabled=\"disabled\" /> <input name=\"userType\" value=\"1\" type=\"hidden\" />",
          NormalizeUtils.toSingleLine(html));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_unchecked_readonly() {
    try {
      model.setUserType(2);

      tag.setPath("model.userType");
      tag.setCodeValue("1");
      tag.setReadonly(true);

      tag.doTag();
      String html = tag.getPageContext().getOut().toString().stripTrailing();

      Assertions.assertEquals(
          "<input id=\"userType\" type=\"radio\" name=\"userType\" value=\"1\" disabled=\"disabled\" />",
          NormalizeUtils.toSingleLine(html));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  public static class TestModel {
    @NotNull
    private int userType;

    public int getUserType() {
      return userType;
    }

    public void setUserType(int userType) {
      this.userType = userType;
    }
  }

  @Controller("testController")
  public static class TestController {

    @HttpGet
    public void index() {
    }
  }
}
