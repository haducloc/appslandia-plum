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

package com.appslandia.plum.pebble.functions;

import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.appslandia.common.base.Params;
import com.appslandia.common.base.StringWriter;
import com.appslandia.common.models.SelectItemImpl;
import com.appslandia.plum.base.ActionResult;
import com.appslandia.plum.base.Controller;
import com.appslandia.plum.base.HttpGet;
import com.appslandia.plum.base.MockTestBase;
import com.appslandia.plum.base.Model;
import com.appslandia.plum.base.RequestWrapper;
import com.appslandia.plum.mocks.MemPebbleTemplateProvider;
import com.appslandia.plum.pebble.PebbleUtils;

import jakarta.validation.constraints.NotNull;

/**
 *
 * @author Loc Ha
 *
 */
public class HiddenSelectFunctionTest extends MockTestBase {

  protected MemPebbleTemplateProvider pebbleTemplateProvider;

  @Override
  protected void initialize() {
    container.register(TestController.class, TestController.class);

    pebbleTemplateProvider = container.getObject(MemPebbleTemplateProvider.class);
  }

  @Test
  public void test_not_readonly() {
    String templateContent = """
        	{{ hiddenSel(path='model.userType', items=items, readonly=false) }}
        """;
    pebbleTemplateProvider.addTemplate("/WEB-INF/pebble/index.peb", templateContent.strip());

    try {
      getCurrentRequest().addParameter("userType", "1");
      executeCurrent("GET", "http://localhost/app/testController/index");

      Params model = new Params();
      model.set("model", getCurrentRequest().getAttribute("model"));
      model.set("items", Arrays.asList(new SelectItemImpl(1, "Type1"), new SelectItemImpl(2, "Type2")));

      StringWriter out = new StringWriter();
      PebbleUtils.executePebble(getCurrentRequest(), getCurrentResponse(), out, "/WEB-INF/pebble/index.peb", model,
          getCurrentRequestContext().getLanguage().getLocale());

      String content = out.toString();
      Assertions.assertEquals("", content);

    } catch (Exception ex) {
      Assertions.fail(ex);
    }
  }

  @Test
  public void test_selected_readonly() {
    String templateContent = """
        	{{ hiddenSel(path='model.userType', items=items, readonly=true) }}
        """;
    pebbleTemplateProvider.addTemplate("/WEB-INF/pebble/index.peb", templateContent.strip());

    try {
      getCurrentRequest().addParameter("userType", "1");
      executeCurrent("GET", "http://localhost/app/testController/index");

      Params model = new Params();
      model.set("model", getCurrentRequest().getAttribute("model"));
      model.set("items", Arrays.asList(new SelectItemImpl(1, "Type1"), new SelectItemImpl(2, "Type2")));

      StringWriter out = new StringWriter();
      PebbleUtils.executePebble(getCurrentRequest(), getCurrentResponse(), out, "/WEB-INF/pebble/index.peb", model,
          getCurrentRequestContext().getLanguage().getLocale());

      String content = out.toString();
      Assertions.assertEquals("<input type=\"hidden\" name=\"userType\" value=\"1\" />", content);

    } catch (Exception ex) {
      Assertions.fail(ex);
    }
  }

  @Test
  public void test_unselected_readonly() {
    String templateContent = """
        	{{ hiddenSel(path='model.userType', items=items, readonly=true) }}
        """;
    pebbleTemplateProvider.addTemplate("/WEB-INF/pebble/index.peb", templateContent.strip());

    try {
      executeCurrent("GET", "http://localhost/app/testController/index");

      Params model = new Params();
      model.set("model", getCurrentRequest().getAttribute("model"));
      model.set("items", Arrays.asList(new SelectItemImpl(1, "Type1"), new SelectItemImpl(2, "Type2")));

      StringWriter out = new StringWriter();
      PebbleUtils.executePebble(getCurrentRequest(), getCurrentResponse(), out, "/WEB-INF/pebble/index.peb", model,
          getCurrentRequestContext().getLanguage().getLocale());

      String content = out.toString();
      Assertions.assertEquals("", content);

    } catch (Exception ex) {
      Assertions.fail(ex);
    }
  }

  @Controller("testController")
  public static class TestController {

    @HttpGet
    public ActionResult index(RequestWrapper request, @Model UserModel model) throws Exception {
      request.storeModel(model);

      return ActionResult.EMPTY;
    }
  }

  public static class UserModel {

    @NotNull
    private Integer userType;

    public Integer getUserType() {
      return userType;
    }

    public void setUserType(Integer userType) {
      this.userType = userType;
    }
  }
}
