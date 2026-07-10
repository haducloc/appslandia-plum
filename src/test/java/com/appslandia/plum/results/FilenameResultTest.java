// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.results;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.appslandia.common.utils.MathUtils;
import com.appslandia.plum.base.ActionResult;
import com.appslandia.plum.base.Controller;
import com.appslandia.plum.base.ControllerBase;
import com.appslandia.plum.base.HttpGet;
import com.appslandia.plum.base.MockTestBase;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
public class FilenameResultTest extends MockTestBase {

  @Override
  protected void initialize() {
    container.register(TestController.class, TestController.class);
  }

  @Test
  public void test() {
    try {
      executeCurrent("GET", "http://localhost/app/testController/testDownload");

      Assertions.assertEquals("application/pdf", getCurrentResponse().getContentType());
      Assertions.assertEquals("attachment; filename=\"testDownload.dat\"; filename*=UTF-8''testDownload.dat",
          getCurrentResponse().getHeader("Content-Disposition"));

      Assertions.assertArrayEquals(MathUtils.toByteArray(1, 10), getCurrentResponse().getContent().toByteArray());

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_inline() {
    try {
      executeCurrent("GET", "http://localhost/app/testController/testDownloadInline");

      Assertions.assertEquals("application/pdf", getCurrentResponse().getContentType());
      Assertions.assertEquals("inline; filename=\"testDownloadInline.dat\"; filename*=UTF-8''testDownloadInline.dat",
          getCurrentResponse().getHeader("Content-Disposition"));

      Assertions.assertArrayEquals(MathUtils.toByteArray(1, 10), getCurrentResponse().getContent().toByteArray());

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Controller("testController")
  public static class TestController extends ControllerBase {

    @HttpGet
    public ActionResult testDownload() throws Exception {
      return new FilenameResult("testDownload.dat", "application/pdf") {

        @Override
        protected void writeContent(HttpServletRequest request, HttpServletResponse response) throws IOException {
          response.getOutputStream().write(MathUtils.toByteArray(1, 10));
        }
      };
    }

    @HttpGet
    public ActionResult testDownloadInline() throws Exception {
      return new FilenameResult("testDownloadInline.dat", "application/pdf", true) {

        @Override
        protected void writeContent(HttpServletRequest request, HttpServletResponse response) throws IOException {
          response.getOutputStream().write(MathUtils.toByteArray(1, 10));
        }
      };
    }
  }
}
