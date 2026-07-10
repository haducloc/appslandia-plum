// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Loc Ha
 *
 */
public class CorsPolicyTest {

  @Test
  public void test() {
    var policy = new CorsPolicy().setAllowOrigins("*");
    Assertions.assertNull(policy.getAllowHeaders());
    Assertions.assertNull(policy.getAllowHeadersAsString());
    Assertions.assertNull(policy.getExposeHeaders());

    Assertions.assertFalse(policy.isAllowCredentials());
    Assertions.assertTrue(policy.getMaxAge() == 0);
  }

  @Test
  public void test_anyOrigin() {
    var policy = new CorsPolicy().setAllowOrigins("*");

    Assertions.assertTrue(policy.isAnyOrigin());
    Assertions.assertTrue(policy.allowOrigin("http://myDomain.com"));
  }

  @Test
  public void test_anyHeader() {
    var policy = new CorsPolicy().setAllowOrigins("*").setAllowHeaders("*");
    Assertions.assertEquals("*", policy.getAllowHeadersAsString());
  }

  @Test
  public void test_allowOrigin() {
    var policy = new CorsPolicy().setAllowOrigins("http(s)?://(.+\\.)*myDomain\\.com");

    Assertions.assertTrue(policy.allowOrigin("https://myDomain.com"));
    Assertions.assertTrue(policy.allowOrigin("http://sub.myDomain.com"));
    Assertions.assertTrue(policy.allowOrigin("http://sub.sub.myDomain.com"));
  }

  @Test
  public void test_allowOrigin_multiline() {
    var policy = new CorsPolicy().setAllowOrigins("""
        https://sub1.myDomain.com
        https://sub2.myDomain.com
        """);

    Assertions.assertTrue(policy.allowOrigin("https://sub1.myDomain.com"));
    Assertions.assertTrue(policy.allowOrigin("https://sub2.myDomain.com"));
  }

  @Test
  public void test_allowHeaders() {
    var policy = new CorsPolicy().setAllowOrigins("*").setAllowHeaders("X-Header, X-Header2");

    Assertions.assertTrue(policy.allowHeaders("X-Header"));
    Assertions.assertTrue(policy.allowHeaders("X-Header, x-header2"));
    Assertions.assertFalse(policy.allowHeaders("X-Header, x-header2, x-header3"));
  }

  @Test
  public void test_allowHeaders_multiline() {
    var policy = new CorsPolicy().setAllowOrigins("*").setAllowHeaders("""
          X-Header
          X-Header2
        """);

    Assertions.assertTrue(policy.allowHeaders("X-Header"));
    Assertions.assertTrue(policy.allowHeaders("X-Header, x-header2"));
    Assertions.assertFalse(policy.allowHeaders("X-Header, x-header2, x-header3"));
  }

  @Test
  public void test_setExposeHeaders() {
    var policy = new CorsPolicy().setAllowOrigins("*").setExposeHeaders("X-Header, X-Header2");

    Assertions.assertTrue(policy.getExposeHeaders().contains("X-Header"));
    Assertions.assertTrue(policy.getExposeHeaders().contains("X-Header2"));
  }

  @Test
  public void test_setExposeHeaders_multiline() {
    var policy = new CorsPolicy().setAllowOrigins("*").setExposeHeaders("""
          X-Header
          X-Header2
        """);

    Assertions.assertTrue(policy.getExposeHeaders().contains("X-Header"));
    Assertions.assertTrue(policy.getExposeHeaders().contains("X-Header2"));
  }

  @Test
  public void test_setMaxAge() {
    var policy = new CorsPolicy().setAllowOrigins("*").setMaxAge(365, TimeUnit.DAYS);
    Assertions.assertEquals(TimeUnit.SECONDS.convert(365, TimeUnit.DAYS), policy.getMaxAge());
  }
}
