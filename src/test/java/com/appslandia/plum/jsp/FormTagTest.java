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
import com.appslandia.plum.base.PathParams;
import com.appslandia.plum.mocks.MockJspContext;
import com.appslandia.plum.mocks.MockJspFragment;

/**
 *
 * @author Loc Ha
 *
 */
public class FormTagTest extends MockTestBase {

  FormTag tag = new FormTag();

  @Override
  protected void initialize() {
    container.register(TestController.class, TestController.class);
  }

  @BeforeEach
  public void initJspContext() throws Exception {
    tag.setJspContext(new MockJspContext(getCurrentRequest(), getCurrentResponse()));
    tag.setJspBody(new MockJspFragment(tag.getPageContext()));
    executeCurrent("GET", "http://localhost/app/testController/index");
  }

  @Test
  public void test() {
    try {
      tag.setId("form1");
      tag.setAction("index");
      tag.setController("testController");

      tag.doTag();
      var html = tag.getPageContext().getOut().toString().strip();

      Assertions.assertEquals(
          "<form id=\"form1\" action=\"/app/testController?encodeURL=true\" autocomplete=\"off\"><input id=\"__form\" type=\"hidden\" name=\"__form\" value=\"form1\" /></form>",
          NormalizeUtils.normalizeHtml(html));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_actionPathParams() {
    try {
      tag.setAction("actionPathParams");
      tag.setController("testController");

      tag.setDynamicAttribute(null, "__p1", "param1");
      tag.setDynamicAttribute(null, "__p2", "param2");

      tag.doTag();
      var html = tag.getPageContext().getOut().toString().strip();

      Assertions.assertEquals(
          "<form action=\"/app/testController/actionPathParams/param1?p2=param2&amp;encodeURL=true\" autocomplete=\"off\"></form>",
          NormalizeUtils.normalizeHtml(html));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Controller("testController")
  public static class TestController extends ControllerBase {

    @HttpGet
    public void index() {
    }

    @HttpGet
    @PathParams("/{p1}")
    public void actionPathParams() {
    }
  }
}
