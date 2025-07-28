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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.appslandia.plum.base.ActionResult;
import com.appslandia.plum.base.AppConfig;
import com.appslandia.plum.base.Controller;
import com.appslandia.plum.base.HttpGet;
import com.appslandia.plum.base.MockTestBase;

/**
 *
 * @author Loc Ha
 *
 */
public class RedirectResultNoSessionTest extends MockTestBase {

  @Override
  protected void initialize() {
    container.register(TestController.class, TestController.class);

    container.getAppConfig().set(AppConfig.CONFIG_ENABLE_SESSION, false);
  }

  @Test
  public void test_testAction() {
    try {
      executeCurrent("GET", "http://localhost/app/testController/testAction");

      Assertions.assertEquals(302, getCurrentResponse().getStatus());

      var location = getCurrentResponse().getHeader("Location");
      Assertions.assertNotNull(location);
      Assertions.assertEquals("/app/testController", location);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_testAction_params() {
    try {
      executeCurrent("GET", "http://localhost/app/testController/testAction_params");

      Assertions.assertEquals(302, getCurrentResponse().getStatus());

      var location = getCurrentResponse().getHeader("Location");
      Assertions.assertNotNull(location);
      Assertions.assertEquals("/app/testController?p1=v1", location);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_redirectToIndex() {
    try {
      executeCurrent("GET", "http://localhost/app/testController/redirectToIndex");

      Assertions.assertEquals(302, getCurrentResponse().getStatus());

      var location = getCurrentResponse().getHeader("Location");
      Assertions.assertNotNull(location);
      Assertions.assertEquals("/app/testController", location);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_redirectToRoot() {
    try {
      executeCurrent("GET", "http://localhost/app/testController/redirectToRoot");

      Assertions.assertEquals(302, getCurrentResponse().getStatus());

      var location = getCurrentResponse().getHeader("Location");
      Assertions.assertNotNull(location);
      Assertions.assertEquals("/app", location);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Controller("testController")
  public static class TestController {

    @HttpGet
    public void index() throws Exception {
    }

    @HttpGet
    public ActionResult testAction() throws Exception {
      return new RedirectResult("index", null);
    }

    @HttpGet
    public ActionResult testAction_params() throws Exception {
      return new RedirectResult("index", null).query("p1", "v1");
    }

    @HttpGet
    public ActionResult redirectToIndex() throws Exception {
      return RedirectResult.INDEX;
    }

    @HttpGet
    public ActionResult redirectToRoot() throws Exception {
      return RedirectResult.ROOT;
    }
  }
}
