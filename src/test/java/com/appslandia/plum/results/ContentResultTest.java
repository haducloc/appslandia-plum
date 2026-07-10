// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.results;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.appslandia.plum.base.ActionResult;
import com.appslandia.plum.base.Controller;
import com.appslandia.plum.base.ControllerBase;
import com.appslandia.plum.base.HttpGet;
import com.appslandia.plum.base.MockTestBase;

/**
 *
 * @author Loc Ha
 *
 */
public class ContentResultTest extends MockTestBase {

  @Override
  protected void initialize() {
    container.register(TestController.class, TestController.class);
  }

  @Test
  public void test_testValue() {
    try {
      executeCurrent("GET", "http://localhost/app/testController/testValue");

      Assertions.assertEquals("100", getCurrentResponse().getContent().toString(StandardCharsets.UTF_8.name()));
      Assertions.assertEquals("application/json", getCurrentResponse().getContentType());

      Assertions.assertEquals(StandardCharsets.UTF_8.name(), getCurrentResponse().getCharacterEncoding());

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_testContentResult() {
    try {
      executeCurrent("GET", "http://localhost/app/testController/testContentResult");

      Assertions.assertEquals("Some text", getCurrentResponse().getContent().toString(StandardCharsets.UTF_8.name()));
      Assertions.assertEquals("text/plain", getCurrentResponse().getContentType());

      Assertions.assertEquals(StandardCharsets.UTF_8.name(), getCurrentResponse().getCharacterEncoding());

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_testContentResult_ISO_8859_1() {
    try {
      executeCurrent("GET", "http://localhost/app/testController/testContentResult_ISO_8859_1");

      Assertions.assertEquals("Some text", getCurrentResponse().getContent().toString(StandardCharsets.ISO_8859_1));
      Assertions.assertEquals("text/plain", getCurrentResponse().getContentType());

      Assertions.assertEquals(StandardCharsets.ISO_8859_1.name(), getCurrentResponse().getCharacterEncoding());

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Controller("testController")
  public static class TestController extends ControllerBase {

    @HttpGet
    public int testValue() throws Exception {
      return 100;
    }

    @HttpGet
    public ActionResult testContentResult() throws Exception {
      return new ContentResult("Some text", "text/plain");
    }

    @HttpGet
    public ActionResult testContentResult_ISO_8859_1() throws Exception {
      return new ContentResult("Some text", "text/plain", StandardCharsets.ISO_8859_1.name());
    }
  }
}
