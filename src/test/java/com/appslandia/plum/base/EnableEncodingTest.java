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

package com.appslandia.plum.base;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.appslandia.common.utils.MimeTypes;
import com.appslandia.plum.results.ContentResult;
import com.appslandia.plum.utils.TestUtils;

/**
 *
 * @author Loc Ha
 *
 */
public class EnableEncodingTest extends MockTestBase {

  @Override
  protected void initialize() {
    container.register(TestController.class, TestController.class);
    container.getAppConfig().set(AppConfig.CONFIG_DISABLE_ENCODING, false);
  }

  @Test
  public void test_testGzip() {
    try {
      getCurrentRequest().setHeader("Accept-Encoding", "gzip");

      executeCurrent("GET", "http://localhost/app/testController/testGzip");

      Assertions.assertEquals(200, getCurrentResponse().getStatus());
      Assertions.assertNull(getCurrentResponse().getHeader("Content-Length"));
      Assertions.assertEquals("gzip", getCurrentResponse().getHeader("Content-Encoding"));

      var data = TestUtils.ungzipToString(getCurrentResponse().getContent());
      Assertions.assertEquals("data", data);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_testGzip_HEAD() {
    try {
      getCurrentRequest().setHeader("Accept-Encoding", "gzip");

      executeCurrent("HEAD", "http://localhost/app/testController/testGzip");

      Assertions.assertEquals(200, getCurrentResponse().getStatus());
      Assertions.assertEquals("gzip", getCurrentResponse().getHeader("Content-Encoding"));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_testGzip_POST() {
    try {
      getCurrentRequest().setHeader("Accept-Encoding", "gzip");

      executeCurrent("POST", "http://localhost/app/testController/testGzip");

      Assertions.assertEquals(200, getCurrentResponse().getStatus());
      Assertions.assertNull(getCurrentResponse().getHeader("Content-Length"));
      Assertions.assertEquals("gzip", getCurrentResponse().getHeader("Content-Encoding"));

      var data = TestUtils.ungzipToString(getCurrentResponse().getContent());
      Assertions.assertEquals("data", data);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Controller("testController")
  public static class TestController {

    @HttpGetPost
    @EnableEncoding
    public ActionResult testGzip() throws Exception {
      return new ContentResult("data", MimeTypes.TEXT_PLAIN);
    }
  }
}
