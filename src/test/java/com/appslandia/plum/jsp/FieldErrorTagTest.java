// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.jsp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.appslandia.common.utils.NormalizeUtils;
import com.appslandia.plum.base.Controller;
import com.appslandia.plum.base.ControllerBase;
import com.appslandia.plum.base.HttpPost;
import com.appslandia.plum.base.MockTestBase;
import com.appslandia.plum.mocks.MockJspContext;

/**
 *
 * @author Loc Ha
 *
 */
public class FieldErrorTagTest extends MockTestBase {

  FieldErrorTag tag = new FieldErrorTag();

  @Override
  protected void initialize() {
    container.register(TestController.class, TestController.class);
  }

  @BeforeEach
  public void initJspContext() throws Exception {
    tag.setJspContext(new MockJspContext(getCurrentRequest(), getCurrentResponse()));
    executeCurrent("POST", "http://localhost/app/testController/index");
  }

  @Test
  public void test() {
    try {
      tag.setPath("userName");
      tag.setRole("alert");
      tag.doTag();

      var html = tag.getPageContext().getOut().toString().strip();
      Assertions.assertEquals("<div id=\"err_userName\" role=\"alert\" class=\"l-error-msg d-none\"></div>", html);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_error() {
    try {
      getCurrentModelState().addError("userName", "The userName field is required.");

      tag.setPath("userName");
      tag.setRole("alert");
      tag.setClazz("l-error-msg");
      tag.doTag();

      var html = tag.getPageContext().getOut().toString().strip();
      Assertions.assertEquals(
          "<div id=\"err_userName\" role=\"alert\" class=\"l-error-msg\">The userName field is required.</div>",
          NormalizeUtils.normalizeHtml(html));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Controller("testController")
  public static class TestController extends ControllerBase {

    @HttpPost
    public void index() {
    }
  }
}
