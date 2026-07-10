// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.facelet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.appslandia.plum.base.Controller;
import com.appslandia.plum.base.ControllerBase;
import com.appslandia.plum.base.HttpGet;
import com.appslandia.plum.base.MockTestBase;

/**
 *
 * @author Loc Ha
 *
 */
public class FunctionsTest extends MockTestBase {

  @Override
  protected void initialize() {
    container.register(TestController.class, TestController.class);
  }

  @BeforeEach
  public void initJspContext() throws Exception {
    executeCurrent("GET", "http://localhost/app/testController/index");
  }

  @Test
  public void test_nowMs() {
    try {
      var ms = Functions.nowMs();

      Assertions.assertTrue(ms > 0);
    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_id() {
    try {
      var id = Functions.id("user.firstName");
      Assertions.assertEquals("user_firstName", id);

      id = Functions.id("user[1].firstName");
      Assertions.assertEquals("user_1__firstName", id);
    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_fmtInt() {
    try {
      var ctx = getCurrentRequestContext();
      var strVal = Functions.fmtInt(ctx, 12345);

      Assertions.assertEquals("12,345", strVal);
    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_fmtInt_null() {
    try {
      var ctx = getCurrentRequestContext();
      var strVal = Functions.fmtInt(ctx, null);

      Assertions.assertNull(strVal);
    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_fmtNum() {
    try {
      var ctx = getCurrentRequestContext();
      var strVal = Functions.fmtNum(ctx, 12345.678, 2);

      Assertions.assertEquals("12,345.68", strVal);
    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_fmtNum_null() {
    try {
      var ctx = getCurrentRequestContext();
      var strVal = Functions.fmtNum(ctx, null, 2);

      Assertions.assertNull(strVal);
    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_fmtPer() {
    try {
      var ctx = getCurrentRequestContext();
      var strVal = Functions.fmtPer(ctx, 0.1234, 2);

      Assertions.assertEquals("12.34%", strVal);
    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_fmtPer_null() {
    try {
      var ctx = getCurrentRequestContext();
      var strVal = Functions.fmtPer(ctx, null, 2);

      Assertions.assertNull(strVal);
    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_fmtCur() {
    try {
      var ctx = getCurrentRequestContext();
      var strVal = Functions.fmtCur(ctx, 12345.678, 2);

      Assertions.assertEquals("$12,345.68", strVal);
    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_fmtCur_null() {
    try {
      var ctx = getCurrentRequestContext();
      var strVal = Functions.fmtCur(ctx, null, 2);

      Assertions.assertNull(strVal);
    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_if() {
    try {
      var strVal = Functions.if_(true, "<test>");
      Assertions.assertEquals("<test>", strVal);

      strVal = Functions.if_(false, "<test>");
      Assertions.assertNull(strVal);
    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_if_null() {
    try {
      var strVal = Functions.if_(true, null);

      Assertions.assertNull(strVal);
    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_iif() {
    try {
      var strVal = Functions.iif(true, "<yes>", "<no>");
      Assertions.assertEquals("<yes>", strVal);

      strVal = Functions.iif(false, "<yes>", "<no>");
      Assertions.assertEquals("<no>", strVal);
    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_iif_null() {
    try {
      var strVal = Functions.iif(true, null, "<no>");
      Assertions.assertNull(strVal);

      strVal = Functions.iif(false, "<yes>", null);
      Assertions.assertNull(strVal);
    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_trunc() {
    try {
      var strVal = Functions.trunc("HelloWorld", 5);

      Assertions.assertEquals("Hello…", strVal);
    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_trunc_shorterThanLen() {
    try {
      var strVal = Functions.trunc("Hello", 10);

      Assertions.assertEquals("Hello", strVal);
    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_trunc_null() {
    try {
      var strVal = Functions.trunc(null, 5);

      Assertions.assertNull(strVal);
    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_upper() {
    try {
      var strVal = Functions.upper("hello");

      Assertions.assertEquals("HELLO", strVal);
    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_upper_null() {
    try {
      var strVal = Functions.upper(null);

      Assertions.assertNull(strVal);
    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_lower() {
    try {
      var strVal = Functions.lower("HELLO");

      Assertions.assertEquals("hello", strVal);
    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_lower_null() {
    try {
      var strVal = Functions.lower(null);

      Assertions.assertNull(strVal);
    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_mask() {
    try {
      var strVal = Functions.mask("123456789", 3);

      Assertions.assertEquals("***456789", strVal);
    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_mask_null() {
    try {
      var strVal = Functions.mask(null, 3);

      Assertions.assertNull(strVal);
    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_maskEnd() {
    try {
      var strVal = Functions.maskEnd("\"123456789", 3);

      Assertions.assertEquals("\"123456***", strVal);
    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_maskEnd_null() {
    try {
      var strVal = Functions.maskEnd(null, 3);

      Assertions.assertNull(strVal);
    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Controller("testController")
  public static class TestController extends ControllerBase {

    @HttpGet
    public void index() {
    }
  }
}
