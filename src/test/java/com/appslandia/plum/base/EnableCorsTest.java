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
public class EnableCorsTest extends MockTestBase {

  AppPolicyProvider appPolicyProvider;

  @Override
  protected void initialize() {
    container.register(TestController.class, TestController.class);
    appPolicyProvider = container.getObject(AppPolicyProvider.class);
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
      appPolicyProvider
          .registerPolicy(new CorsPolicy().setIncludePaths("/testController/enableCorsAction").setAllowOrigins("*"));

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
      appPolicyProvider.registerPolicy(new CorsPolicy().setIncludePaths("/testController/enableCorsAction")
          .setAllowOrigins("http://myDomain\\.com"));

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
      appPolicyProvider.registerPolicy(new CorsPolicy().setIncludePaths("/testController/enableCorsAction")
          .setAllowOrigins("http://localhost:(\\d)+"));

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
      appPolicyProvider.registerPolicy(new CorsPolicy().setIncludePaths("/testController/enableCorsAction")
          .setAllowOrigins("*").setAllowHeaders("X-Header"));

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
      appPolicyProvider.registerPolicy(new CorsPolicy().setIncludePaths("/testController/enableCorsAction")
          .setAllowOrigins("*").setAllowHeaders("X-Header"));

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
      appPolicyProvider.registerPolicy(new CorsPolicy().setIncludePaths("/testController/enableCorsAction")
          .setAllowOrigins("*").setAllowHeaders("X-Header"));

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
      appPolicyProvider.registerPolicy(new CorsPolicy().setIncludePaths("/testController/enableCorsAction")
          .setAllowOrigins("*").setAllowHeaders("X-Header"));
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
      appPolicyProvider
          .registerPolicy(new CorsPolicy().setIncludePaths("/testController/enableCorsAction").setAllowOrigins("*"));
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
    public void enableCorsAction() throws Exception {
    }
  }
}
