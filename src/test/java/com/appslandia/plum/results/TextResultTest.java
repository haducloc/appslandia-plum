// The MIT License (MIT)
// Copyright © 2015 AppsLandia. All rights reserved.

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

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.appslandia.common.base.BOMInputStream;
import com.appslandia.common.utils.IOUtils;
import com.appslandia.plum.base.ActionResult;
import com.appslandia.plum.base.Controller;
import com.appslandia.plum.base.HttpGet;
import com.appslandia.plum.base.MockTestBase;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class TextResultTest extends MockTestBase {

  @Override
  protected void initialize() {
    container.register(TestController.class, TestController.class);
  }

  @Test
  public void test_testTextFileResult() {
    try {
      executeCurrent("GET", "http://localhost/app/testController/testTextFileResult");

      Assertions.assertEquals("application/csv", getCurrentResponse().getContentType());
      Assertions.assertEquals(StandardCharsets.UTF_8.name(), getCurrentResponse().getCharacterEncoding());

      try (BOMInputStream bis = new BOMInputStream(
          new ByteArrayInputStream(getCurrentResponse().getContent().toByteArray()))) {
        String content = IOUtils.toString(bis, StandardCharsets.UTF_8.name());
        Assertions.assertEquals("item1, item2, item3", content);
      }
    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_testTextFileResult_ISO_8859_1() {
    try {
      executeCurrent("GET", "http://localhost/app/testController/testTextFileResult_ISO_8859_1");

      Assertions.assertEquals("application/csv", getCurrentResponse().getContentType());
      Assertions.assertEquals(StandardCharsets.ISO_8859_1.name(), getCurrentResponse().getCharacterEncoding());

      try (BOMInputStream bis = new BOMInputStream(
          new ByteArrayInputStream(getCurrentResponse().getContent().toByteArray()))) {
        String content = IOUtils.toString(bis, StandardCharsets.ISO_8859_1.name());
        Assertions.assertEquals("item1, item2, item3", content);
      }
    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Controller("testController")
  public static class TestController {

    @HttpGet
    public ActionResult testTextFileResult() throws Exception {
      String content = "item1, item2, item3";
      return new TextFileResult(content, "test.csv", "application/csv");
    }

    @HttpGet
    public ActionResult testTextFileResult_ISO_8859_1() throws Exception {
      String content = "item1, item2, item3";
      return new TextFileResult(content, "test.csv", "application/csv", StandardCharsets.ISO_8859_1.name());
    }
  }
}
