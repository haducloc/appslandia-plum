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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.appslandia.common.utils.NormalizeUtils;
import com.appslandia.plum.base.Controller;
import com.appslandia.plum.base.HttpGet;
import com.appslandia.plum.base.MockTestBase;
import com.appslandia.plum.mocks.MockJspContext;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.validation.constraints.NotNull;

/**
 *
 * @author Loc Ha
 *
 */
public class CheckInputTagTest extends MockTestBase {

  ChoiceInputTag tag = new ChoiceInputTag();
  TestModel model = new TestModel();

  @Override
  protected void initialize() {
    container.register(TestController.class, TestController.class);
  }

  @BeforeEach
  public void beforeEachTest() {
    var choiceBox = new ChoiceBoxTag();
    choiceBox.setPath("active");
    tag.setParent(choiceBox);

    tag.setJspContext(new MockJspContext(getCurrentRequest(), getCurrentResponse()));
    getCurrentRequest().setAttribute(ServletUtils.REQUEST_ATTRIBUTE_MODEL, model);

    executeCurrent("GET", "http://localhost/app/testController/index");
  }

  @Test
  public void test() {
    try {
      model.setActive(true);
      tag.setValue("true");
      tag.doTag();

      var html = tag.getPageContext().getOut().toString().strip();

      Assertions.assertEquals(
          "<input id=\"active\" type=\"checkbox\" name=\"active\" value=\"true\" checked=\"checked\" />",
          NormalizeUtils.normalizeHtml(html));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_unchecked() {
    try {
      model.setActive(false);
      tag.setValue("true");
      tag.doTag();

      var html = tag.getPageContext().getOut().toString().strip();

      Assertions.assertEquals("<input id=\"active\" type=\"checkbox\" name=\"active\" value=\"true\" />",
          NormalizeUtils.normalizeHtml(html));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_checked_disabled() {
    try {
      model.setActive(true);
      tag.setValue("true");
      tag.setDisabled(true);

      tag.doTag();
      var html = tag.getPageContext().getOut().toString().strip();

      Assertions.assertEquals(
          "<input id=\"active\" type=\"checkbox\" name=\"active\" value=\"true\" checked=\"checked\" disabled=\"disabled\" />",
          NormalizeUtils.normalizeHtml(html));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_unchecked_disabled() {
    try {
      model.setActive(false);
      tag.setValue("true");
      tag.setDisabled(true);

      tag.doTag();
      var html = tag.getPageContext().getOut().toString().strip();

      Assertions.assertEquals(
          "<input id=\"active\" type=\"checkbox\" name=\"active\" value=\"true\" disabled=\"disabled\" />",
          NormalizeUtils.normalizeHtml(html));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  public static class TestModel {

    @NotNull
    private Boolean active;

    public Boolean getActive() {
      return active;
    }

    public void setActive(Boolean active) {
      this.active = active;
    }
  }

  @Controller("testController")
  public static class TestController {

    @HttpGet
    public void index() {
    }
  }
}
