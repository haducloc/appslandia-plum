// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.jsp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.appslandia.plum.base.ConstGroup;
import com.appslandia.plum.base.ConstGroupProvider;
import com.appslandia.plum.base.Controller;
import com.appslandia.plum.base.ControllerBase;
import com.appslandia.plum.base.HttpGet;
import com.appslandia.plum.base.MockTestBase;
import com.appslandia.plum.mocks.MockJspContext;

/**
 *
 * @author Loc Ha
 *
 */
public class FmtConstTagTest extends MockTestBase {

  FmtConstTag tag = new FmtConstTag();
  ConstGroupProvider provider;

  @Override
  protected void initialize() {
    container.register(TestController.class, TestController.class);
    provider = container.getObject(ConstGroupProvider.class);
  }

  @BeforeEach
  public void initJspContext() throws Exception {
    tag.setJspContext(new MockJspContext(getCurrentRequest(), getCurrentResponse()));
    executeCurrent("GET", "http://localhost/app/testController/index");
  }

  @Test
  public void test_ACTIVE() {
    try {
      provider.addConstClass(ActiveStatuses.class);

      tag.setFmt("user_actives");
      tag.setValue(ActiveStatuses.ACTIVE);

      tag.doTag();
      var desc = tag.getPageContext().getOut().toString().strip();

      Assertions.assertEquals("en:user_actives.active", desc);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_INACTIVE() {
    try {
      provider.addConstClass(ActiveStatuses.class);

      tag.setFmt("user_actives");
      tag.setValue(ActiveStatuses.INACTIVE);

      tag.doTag();
      var desc = tag.getPageContext().getOut().toString().strip();

      Assertions.assertEquals("en:user_actives.inactive", desc);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_fallback() {
    try {
      // provider.addConstClass(Actives.class);

      tag.setFmt("user_actives");
      tag.setValue(ActiveStatuses.ACTIVE);

      tag.doTag();
      var desc = tag.getPageContext().getOut().toString().strip();

      Assertions.assertEquals(Integer.toString(ActiveStatuses.ACTIVE), desc);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  public static final class ActiveStatuses {

    @ConstGroup("user_actives")
    public static final int ACTIVE = 1;

    @ConstGroup("user_actives")
    public static final int INACTIVE = 0;
  }

  @Controller("testController")
  public static class TestController extends ControllerBase {

    @HttpGet
    public void index() {
    }
  }
}
