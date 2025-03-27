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

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.appslandia.plum.base.ActionResult;
import com.appslandia.plum.base.Controller;
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
  public void test_testTextResult() {
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
  public void test_testTextResult_ISO_8859_1() {
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
  public static class TestController {

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
