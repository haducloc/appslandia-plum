// Licensed under the MIT License.
// See LICENSE file in the project root for details.

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
  public static class TestController extends ControllerBase {

    @HttpGetPost
    @EnableCsrf
    public void testCsrf() throws Exception {
    }
  }
}
