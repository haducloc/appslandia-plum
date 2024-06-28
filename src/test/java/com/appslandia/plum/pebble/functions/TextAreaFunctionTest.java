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

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.appslandia.common.base.Params;
import com.appslandia.common.base.StringWriter;
import com.appslandia.plum.base.ActionResult;
import com.appslandia.plum.base.Controller;
import com.appslandia.plum.base.HttpGet;
import com.appslandia.plum.base.MockTestBase;
import com.appslandia.plum.base.Model;
import com.appslandia.plum.base.RequestAccessor;
import com.appslandia.plum.mocks.MemPebbleTemplateProvider;
import com.appslandia.plum.pebble.PebbleUtils;

import jakarta.validation.constraints.NotNull;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class TextAreaFunctionTest extends MockTestBase {

  protected MemPebbleTemplateProvider pebbleTemplateProvider;

  @Override
  protected void initialize() {
    container.register(TestController.class, TestController.class);

    pebbleTemplateProvider = container.getObject(MemPebbleTemplateProvider.class);
  }

  @Test
  public void test() {
    String templateContent = """
        {{ textarea(path='model.notes') }}
        """;
    pebbleTemplateProvider.addTemplate("/WEB-INF/pebble/index.peb", templateContent.strip());

    try {
      executeCurrent("GET", "http://localhost/app/testController/index");

      Map<String, Object> model = new Params().set("model", getCurrentRequest().getAttribute("model"));

      StringWriter out = new StringWriter();
      PebbleUtils.executePebble(getCurrentRequest(), getCurrentResponse(), out, "/WEB-INF/pebble/index.peb", model,
          getCurrentRequestContext().getLanguage().getLocale());

      String content = out.toString();
      Assertions.assertEquals("id=\"notes\" name=\"notes\"", content);

    } catch (Exception ex) {
      Assertions.fail(ex);
    }
  }

  @Controller("testController")
  public static class TestController {

    @HttpGet
    public ActionResult index(RequestAccessor request, @Model UserModel model) throws Exception {
      request.storeModel(model);

      return ActionResult.EMPTY;
    }
  }

  public static class UserModel {

    @NotNull
    private String notes;

    public String getNotes() {
      return notes;
    }

    public void setNotes(String notes) {
      this.notes = notes;
    }
  }
}
