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

package com.appslandia.plum.pebble.functions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.appslandia.common.base.StringWriter;
import com.appslandia.plum.base.ActionResult;
import com.appslandia.plum.base.Controller;
import com.appslandia.plum.base.HttpGet;
import com.appslandia.plum.base.MockTestBase;
import com.appslandia.plum.mocks.MemPebbleTemplateProvider;
import com.appslandia.plum.pebble.PebbleUtils;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class ActionUrlFunctionTest extends MockTestBase {

  protected MemPebbleTemplateProvider pebbleTemplateProvider;

  @Override
  protected void initialize() {
    container.register(TestController.class, TestController.class);

    pebbleTemplateProvider = container.getObject(MemPebbleTemplateProvider.class);
  }

  @Test
  public void test() {
    String templateContent = """
        {{ actionUrl(action='index', __par1='value1', __par2='value 2') }}
        """;
    pebbleTemplateProvider.addTemplate("/WEB-INF/pebble/index.peb", templateContent.strip());

    try {
      executeCurrent("GET", "http://localhost/app/testController/index");

      StringWriter out = new StringWriter();
      PebbleUtils.executePebble(getCurrentRequest(), getCurrentResponse(), out, "/WEB-INF/pebble/index.peb", null,
          getCurrentRequestContext().getLanguage().getLocale());

      String content = out.toString();
      Assertions.assertEquals("/app/testController/?par1=value1&amp;par2=value+2&amp;encodeURL=true", content);

    } catch (Exception ex) {
      Assertions.fail(ex);
    }
  }

  @Test
  public void test_not_escXml() {
    String templateContent = """
        {{ actionUrl(action='index', __par1='value1', __par2='value 2', escXml=false) }}
        """;
    pebbleTemplateProvider.addTemplate("/WEB-INF/pebble/index.peb", templateContent.strip());

    try {
      executeCurrent("GET", "http://localhost/app/testController/index");

      StringWriter out = new StringWriter();
      PebbleUtils.executePebble(getCurrentRequest(), getCurrentResponse(), out, "/WEB-INF/pebble/index.peb", null,
          getCurrentRequestContext().getLanguage().getLocale());

      String content = out.toString();
      Assertions.assertEquals("/app/testController/?par1=value1&par2=value+2&encodeURL=true", content);

    } catch (Exception ex) {
      Assertions.fail(ex);
    }
  }

  @Test
  public void test_absUrl() {
    String templateContent = """
        {{ actionUrl(action='index', __par1='value1', __par2='value 2', absUrl=true) }}
        """;
    pebbleTemplateProvider.addTemplate("/WEB-INF/pebble/index.peb", templateContent.strip());

    try {
      executeCurrent("GET", "http://localhost/app/testController/index");

      StringWriter out = new StringWriter();
      PebbleUtils.executePebble(getCurrentRequest(), getCurrentResponse(), out, "/WEB-INF/pebble/index.peb", null,
          getCurrentRequestContext().getLanguage().getLocale());

      String content = out.toString();
      Assertions.assertEquals("http://localhost/app/testController/?par1=value1&amp;par2=value+2&amp;encodeURL=true",
          content);

    } catch (Exception ex) {
      Assertions.fail(ex);
    }
  }

  @Controller("testController")
  public static class TestController {

    @HttpGet
    public ActionResult index() throws Exception {
      return ActionResult.EMPTY;
    }
  }
}
