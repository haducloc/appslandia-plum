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

package com.appslandia.plum.results;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.appslandia.common.utils.MathUtils;
import com.appslandia.plum.base.ActionResult;
import com.appslandia.plum.base.Controller;
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
      Assertions.assertEquals("attachment; filename=\"testDownload.dat\"",
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
      Assertions.assertEquals("inline; filename=\"testDownloadInline.dat\"",
          getCurrentResponse().getHeader("Content-Disposition"));

      Assertions.assertArrayEquals(MathUtils.toByteArray(1, 10), getCurrentResponse().getContent().toByteArray());

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Controller("testController")
  public static class TestController {

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
