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
public class EnableCorsTest extends MockTestBase {

  CorsPolicyProvider corsPolicyProvider;

  @Override
  protected void initialize() {
    container.register(TestController.class, TestController.class);
    corsPolicyProvider = container.getObject(CorsPolicyProvider.class);
  }

  @Test
  public void test_testAction() {
    try {
      getCurrentRequest().setHeader("Origin", "http://myDomain.com");
      getCurrentRequest().setHeader("Access-Control-Request-Method", "POST");

      executeCurrent("OPTIONS", "http://localhost/app/testController/testAction");

      Assertions.assertEquals(403, getCurrentResponse().getStatus());
      Assertions.assertNull(getCurrentResponse().getHeader("Allow"));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_testAction_sameOrigin() {
    try {
      getCurrentRequest().setHeader("Origin", "http://localhost");

      executeCurrent("POST", "http://localhost/app/testController/testAction");

      Assertions.assertEquals(200, getCurrentResponse().getStatus());
      Assertions.assertNull(getCurrentResponse().getHeader(CorsPolicy.HEADER_AC_ALLOW_ORIGIN));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_testAction_crossOrigin() {
    try {
      getCurrentRequest().setHeader("Origin", "http://myDomain.com");

      executeCurrent("POST", "http://localhost/app/testController/testAction");

      Assertions.assertEquals(403, getCurrentResponse().getStatus());
      Assertions.assertNull(getCurrentResponse().getHeader(CorsPolicy.HEADER_AC_ALLOW_ORIGIN));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_enableCorsAction_allowed_anyOrigin() {
    try {
      corsPolicyProvider.registerCorsPolicy("__default", new CorsPolicy().setAllowOrigins("*"));

      getCurrentRequest().setHeader("Origin", "http://myDomain.com");
      getCurrentRequest().setHeader("Access-Control-Request-Method", "POST");

      executeCurrent("OPTIONS", "http://localhost/app/testController/enableCorsAction");

      Assertions.assertEquals(200, getCurrentResponse().getStatus());
      Assertions.assertEquals("*", getCurrentResponse().getHeader(CorsPolicy.HEADER_AC_ALLOW_ORIGIN));

      var allows = getCurrentResponse().getHeader(CorsPolicy.HEADER_AC_ALLOW_METHODS);
      Assertions.assertNotNull(allows);

      Assertions.assertTrue(allows.contains("GET"));
      Assertions.assertTrue(allows.contains("HEAD"));
      Assertions.assertTrue(allows.contains("POST"));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_enableCorsAction_allowed_origin() {
    try {
      corsPolicyProvider.registerCorsPolicy("__default", new CorsPolicy().setAllowOrigins("http://myDomain\\.com"));

      getCurrentRequest().setHeader("Origin", "http://myDomain.com");
      getCurrentRequest().setHeader("Access-Control-Request-Method", "POST");

      executeCurrent("OPTIONS", "http://localhost/app/testController/enableCorsAction");

      Assertions.assertEquals(200, getCurrentResponse().getStatus());
      Assertions.assertEquals("http://myDomain.com", getCurrentResponse().getHeader(CorsPolicy.HEADER_AC_ALLOW_ORIGIN));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_enableCorsAction_notAllowed_origin() {
    try {
      corsPolicyProvider.registerCorsPolicy("__default", new CorsPolicy().setAllowOrigins("http://localhost:(\\d)+"));

      getCurrentRequest().setHeader("Origin", "http://myDomain.com");
      getCurrentRequest().setHeader("Access-Control-Request-Method", "POST");

      executeCurrent("OPTIONS", "http://localhost/app/testController/enableCorsAction");

      Assertions.assertEquals(403, getCurrentResponse().getStatus());
      Assertions.assertNull(getCurrentResponse().getHeader(CorsPolicy.HEADER_AC_ALLOW_ORIGIN));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_enableCorsAction_notAllowed_requestMethod() {
    try {
      corsPolicyProvider.registerCorsPolicy("__default",
          new CorsPolicy().setAllowOrigins("*").setAllowHeaders("X-Header"));

      getCurrentRequest().setHeader("Origin", "http://myDomain.com");
      getCurrentRequest().setHeader("Access-Control-Request-Method", "PUT");

      executeCurrent("OPTIONS", "http://localhost/app/testController/enableCorsAction");

      Assertions.assertEquals(403, getCurrentResponse().getStatus());
      Assertions.assertNull(getCurrentResponse().getHeader(CorsPolicy.HEADER_AC_ALLOW_ORIGIN));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_enableCorsAction_allowed_allowHeaders() {
    try {
      corsPolicyProvider.registerCorsPolicy("__default",
          new CorsPolicy().setAllowOrigins("*").setAllowHeaders("X-Header"));

      getCurrentRequest().setHeader("Origin", "http://myDomain.com");
      getCurrentRequest().setHeader("Access-Control-Request-Method", "POST");
      getCurrentRequest().setHeader("Access-Control-Request-Headers", "x-header");

      executeCurrent("OPTIONS", "http://localhost/app/testController/enableCorsAction");

      Assertions.assertEquals(200, getCurrentResponse().getStatus());
      Assertions.assertEquals("*", getCurrentResponse().getHeader(CorsPolicy.HEADER_AC_ALLOW_ORIGIN));
      Assertions.assertEquals("x-header", getCurrentResponse().getHeader(CorsPolicy.HEADER_AC_ALLOW_HEADERS));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_enableCorsAction_notAllowed_requestHeaders() {
    try {
      corsPolicyProvider.registerCorsPolicy("__default",
          new CorsPolicy().setAllowOrigins("*").setAllowHeaders("X-Header"));

      getCurrentRequest().setHeader("Origin", "http://myDomain.com");
      getCurrentRequest().setHeader("Access-Control-Request-Method", "POST");
      getCurrentRequest().setHeader("Access-Control-Request-Headers", "X-Header1");

      executeCurrent("OPTIONS", "http://localhost/app/testController/enableCorsAction");

      Assertions.assertEquals(403, getCurrentResponse().getStatus());
      Assertions.assertNull(getCurrentResponse().getHeader(CorsPolicy.HEADER_AC_ALLOW_ORIGIN));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_enableCorsAction_sameOrigin() {
    try {
      getCurrentRequest().setHeader("Origin", "http://localhost");

      executeCurrent("POST", "http://localhost/app/testController/enableCorsAction");
      Assertions.assertEquals(200, getCurrentResponse().getStatus());

      Assertions.assertNull(getCurrentResponse().getHeader(CorsPolicy.HEADER_AC_ALLOW_ORIGIN));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_enableCorsAction_crossOrigin() {
    try {
      corsPolicyProvider.registerCorsPolicy("__default",
          new CorsPolicy().setAllowOrigins("*").setAllowHeaders("X-Header"));
      getCurrentRequest().setHeader("Origin", "http://myDomain.com");

      executeCurrent("POST", "http://localhost/app/testController/enableCorsAction");
      Assertions.assertEquals(200, getCurrentResponse().getStatus());

      Assertions.assertEquals("*", getCurrentResponse().getHeader(CorsPolicy.HEADER_AC_ALLOW_ORIGIN));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_enableCorsAction_vary() {
    try {
      corsPolicyProvider.registerCorsPolicy("__default", new CorsPolicy().setAllowOrigins("*"));
      getCurrentRequest().setHeader("Origin", "http://myDomain.com");

      executeCurrent("POST", "http://localhost/app/testController/enableCorsAction");
      Assertions.assertEquals(200, getCurrentResponse().getStatus());

      var vary = getCurrentResponse().getHeaders(CorsPolicy.HEADER_VARY);
      Assertions.assertFalse(vary.contains("Origin"));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Controller("testController")
  public static class TestController extends ControllerBase {

    @HttpGetPost
    public void testAction() throws Exception {
    }

    @HttpGetPost
    @EnableCors
    public void enableCorsAction() throws Exception {
    }
  }
}
