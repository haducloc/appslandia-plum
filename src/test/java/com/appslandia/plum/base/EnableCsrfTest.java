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

/**
 *
 * @author Loc Ha
 *
 */
public class EnableCsrfTest extends MockTestBase {

  SimpleCsrfManager csrfManager;

  @Override
  protected void initialize() {
    container.register(TestController.class, TestController.class);
    csrfManager = container.getObject(CsrfManager.class);
  }

  @Test
  public void test_testCsrf() {
    try {
      executeCurrent("GET", "http://localhost/app/testController/testCsrf");

      var csrfId = (String) getCurrentRequest().getAttribute(SimpleCsrfManager.PARAM_CSRF_ID);
      Assertions.assertNull(csrfId);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_testCsrf_POST() {
    try {
      var request = container.createRequest("GET");
      csrfManager.initCsrf(request);
      var csrfId = (String) request.getAttribute(SimpleCsrfManager.PARAM_CSRF_ID);

      getCurrentRequest().setSession(request.getSession());
      getCurrentRequest().setParameter(SimpleCsrfManager.PARAM_CSRF_ID, csrfId);

      executeCurrent("POST", "http://localhost/app/testController/testCsrf");

      Assertions.assertTrue(getCurrentModelState().isValid(SimpleCsrfManager.PARAM_CSRF_ID));

      // Removed
      var valid = csrfManager.verifyCsrf(getCurrentRequest(), true);
      Assertions.assertTrue(!valid);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_testCsrf_POST_invalid() {
    try {
      var request = container.createRequest("GET");
      csrfManager.initCsrf(request);

      getCurrentRequest().setSession(request.getSession());
      getCurrentRequest().setParameter(SimpleCsrfManager.PARAM_CSRF_ID, "invalid");

      executeCurrent("POST", "http://localhost/app/testController/testCsrf");

      Assertions.assertFalse(getCurrentModelState().isValid(SimpleCsrfManager.PARAM_CSRF_ID));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Controller("testController")
  public static class TestController {

    @HttpGetPost
    @EnableCsrf
    public void testCsrf() throws Exception {
    }
  }
}
