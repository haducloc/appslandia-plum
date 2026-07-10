// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.appslandia.common.utils.ContentTypes;
import com.appslandia.plum.mocks.MockAppConfig;
import com.appslandia.plum.results.ContentResult;
import com.appslandia.plum.utils.TestUtils;

/**
 *
 * @author Loc Ha
 *
 */
public class EnableCompressTest extends MockTestBase {

  @Override
  protected void initialize() {
    container.register(TestController.class, TestController.class);

    var appConfig = container.getObject(MockAppConfig.class);
    appConfig.set(AppConfig.CONFIG_COMPRESS_THRESHOLD, 1);
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
  public static class TestController extends ControllerBase {

    @HttpGetPost
    @EnableCompress
    public ActionResult testGzip() throws Exception {
      return new ContentResult("data", ContentTypes.TEXT_PLAIN);
    }
  }
}
