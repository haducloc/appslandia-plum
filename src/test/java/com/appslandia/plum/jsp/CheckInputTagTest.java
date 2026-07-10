// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.jsp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.appslandia.common.utils.NormalizeUtils;
import com.appslandia.plum.base.Controller;
import com.appslandia.plum.base.ControllerBase;
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
  public void initJspContext() throws Exception {
    var choiceGroup = new ChoiceGroupTag();
    choiceGroup.setPath("active");
    choiceGroup.setModel("model");

    tag.setParent(choiceGroup);

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
  public static class TestController extends ControllerBase {

    @HttpGet
    public void index() {
    }
  }
}
