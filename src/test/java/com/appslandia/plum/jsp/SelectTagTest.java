// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.jsp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.appslandia.common.utils.CollectionUtils;
import com.appslandia.common.utils.NormalizeUtils;
import com.appslandia.plum.base.Controller;
import com.appslandia.plum.base.ControllerBase;
import com.appslandia.plum.base.HttpGet;
import com.appslandia.plum.base.MockTestBase;
import com.appslandia.plum.base.SelectItem;
import com.appslandia.plum.mocks.MockJspContext;
import com.appslandia.plum.utils.ServletUtils;

/**
 *
 * @author Loc Ha
 *
 */
public class SelectTagTest extends MockTestBase {

  SelectTag tag = new SelectTag();
  TestModel model = new TestModel();

  @Override
  protected void initialize() {
    container.register(TestController.class, TestController.class);
  }

  @BeforeEach
  public void initJspContext() throws Exception {
    tag.setJspContext(new MockJspContext(getCurrentRequest(), getCurrentResponse()));
    getCurrentRequest().setAttribute(ServletUtils.REQUEST_ATTRIBUTE_MODEL, model);
    executeCurrent("GET", "http://localhost/app/testController/index");
  }

  @Test
  public void test() {
    try {
      model.setUserType(2);

      tag.setPath("userType");
      tag.setItems(CollectionUtils.toList(new SelectItem(1, "type1"), new SelectItem(2, "type2")));

      tag.doTag();
      var html = tag.getPageContext().getOut().toString().strip();

      Assertions.assertEquals(
          "<select id=\"userType\" name=\"userType\"><option value=\"1\">type1</option><option value=\"2\" selected=\"selected\">type2</option></select>",
          NormalizeUtils.normalizeHtml(html));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_disabled() {
    try {
      model.setUserType(2);

      tag.setPath("userType");
      tag.setItems(CollectionUtils.toList(new SelectItem(1, "type1"), new SelectItem(2, "type2")));
      tag.setDisabled(true);

      tag.doTag();
      var html = tag.getPageContext().getOut().toString().strip();

      Assertions.assertEquals(
          "<select id=\"userType\" name=\"userType\" disabled=\"disabled\"><option value=\"1\">type1</option><option value=\"2\" selected=\"selected\">type2</option></select>",
          NormalizeUtils.normalizeHtml(html));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_unselected() {
    try {
      tag.setPath("userType");
      tag.setItems(CollectionUtils.toList(new SelectItem(1, "type1"), new SelectItem(2, "type2")));

      tag.doTag();
      var html = tag.getPageContext().getOut().toString().strip();

      Assertions.assertEquals(
          "<select id=\"userType\" name=\"userType\"><option value=\"1\">type1</option><option value=\"2\">type2</option></select>",
          NormalizeUtils.normalizeHtml(html));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_optBlank() {
    try {
      tag.setPath("userType");
      tag.setOptBlank("");
      tag.setItems(CollectionUtils.toList(new SelectItem(1, "type1"), new SelectItem(2, "type2")));

      tag.doTag();
      var html = tag.getPageContext().getOut().toString().strip();

      Assertions.assertEquals(
          "<select id=\"userType\" name=\"userType\"><option value=\"\" selected=\"selected\"></option><option value=\"1\">type1</option><option value=\"2\">type2</option></select>",
          NormalizeUtils.normalizeHtml(html));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_selected_optDisabled() {
    try {
      model.setUserType(2);
      tag.setPath("userType");
      tag.setItems(CollectionUtils.toList(new SelectItem(1, "type1"), new SelectItem(2, "type2", true)));

      tag.doTag();
      var html = tag.getPageContext().getOut().toString().strip();

      Assertions.assertEquals(
          "<select id=\"userType\" name=\"userType\"><option value=\"1\">type1</option><option value=\"2\" selected=\"selected\" disabled=\"disabled\">type2</option></select>",
          NormalizeUtils.normalizeHtml(html));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_unselected_optDisabled() {
    try {
      tag.setPath("userType");
      tag.setItems(CollectionUtils.toList(new SelectItem(1, "type1"), new SelectItem(2, "type2", true)));

      tag.doTag();
      var html = tag.getPageContext().getOut().toString().strip();

      Assertions.assertEquals(
          "<select id=\"userType\" name=\"userType\"><option value=\"1\">type1</option><option value=\"2\" disabled=\"disabled\">type2</option></select>",
          NormalizeUtils.normalizeHtml(html));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  public static class TestModel {

    private Integer userType;

    public Integer getUserType() {
      return userType;
    }

    public void setUserType(Integer userType) {
      this.userType = userType;
    }
  }

  @Controller("testController")
  public static class TestController extends ControllerBase {

    @HttpGet
    public void index() {
    }
  }
}
