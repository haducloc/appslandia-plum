// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.appslandia.plum.mocks.MockHttpServletRequest;

/**
 *
 * @author Loc Ha
 *
 */
public class CsrfManagerTest extends MockTestBase {

  SimpleCsrfManager csrfManager;
  RequestContextParser requestContextParser;

  @Override
  protected void initialize() {
    container.register(TestController.class, TestController.class);
    csrfManager = container.getObject(CsrfManager.class);
    requestContextParser = container.getObject(RequestContextParser.class);
  }

  protected MockHttpServletRequest initCsrf() {
    var request = container.createRequest("GET", "http://localhost/app/testController/testCsrf");
    requestContextParser.initRequestContext(request, container.createResponse());

    csrfManager.initCsrf(request);
    var csrfId = (String) request.getAttribute(SimpleCsrfManager.PARAM_CSRF_ID);
    Assertions.assertNotNull(csrfId);

    return request;
  }

  @Test
  public void test_verifyCsrf() {
    try {
      var request = initCsrf();
      var csrfId = (String) request.getAttribute(SimpleCsrfManager.PARAM_CSRF_ID);

      getCurrentRequest().setMethod("POST").setRequestURL("http://localhost/app/testController/testCsrf");
      getCurrentRequest().setSession(request.getSession());
      getCurrentRequest().addParameter(SimpleCsrfManager.PARAM_CSRF_ID, csrfId);
      requestContextParser.initRequestContext(getCurrentRequest(), getCurrentResponse());

      var valid = csrfManager.verifyCsrf(getCurrentRequest(), true);
      Assertions.assertTrue(valid);

      // Removed
      valid = csrfManager.verifyCsrf(getCurrentRequest(), true);
      Assertions.assertFalse(valid);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_verifyCsrf_keepCsrf() {
    try {
      var request = initCsrf();
      var csrfId = (String) request.getAttribute(SimpleCsrfManager.PARAM_CSRF_ID);

      getCurrentRequest().setMethod("POST").setRequestURL("http://localhost/app/testController/testCsrf");
      getCurrentRequest().setSession(request.getSession());
      getCurrentRequest().addParameter(SimpleCsrfManager.PARAM_CSRF_ID, csrfId);
      requestContextParser.initRequestContext(getCurrentRequest(), getCurrentResponse());

      var valid = csrfManager.verifyCsrf(getCurrentRequest(), false);
      Assertions.assertTrue(valid);

      valid = csrfManager.verifyCsrf(getCurrentRequest(), true);
      Assertions.assertTrue(valid);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_verifyCsrf_invalid() {
    try {
      var request = initCsrf();

      getCurrentRequest().setMethod("POST").setRequestURL("http://localhost/app/testController/testCsrf");
      getCurrentRequest().setSession(request.getSession());
      getCurrentRequest().addParameter(SimpleCsrfManager.PARAM_CSRF_ID, "invalid");
      requestContextParser.initRequestContext(getCurrentRequest(), getCurrentResponse());

      var valid = csrfManager.verifyCsrf(getCurrentRequest(), true);
      Assertions.assertFalse(valid);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_verifyCsrf_noSession() {
    try {
      var request = initCsrf();
      var csrfId = (String) request.getAttribute(SimpleCsrfManager.PARAM_CSRF_ID);

      getCurrentRequest().setMethod("POST").setRequestURL("http://localhost/app/testController/testCsrf");
      // getCurrentRequest().setSession(request.getSession());
      getCurrentRequest().addParameter(SimpleCsrfManager.PARAM_CSRF_ID, csrfId);
      requestContextParser.initRequestContext(getCurrentRequest(), getCurrentResponse());

      var valid = csrfManager.verifyCsrf(getCurrentRequest(), true);
      Assertions.assertFalse(valid);

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
