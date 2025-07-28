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
    var policy = new CorsPolicy().setName("testCors").setAnyOrigin();
    Assertions.assertNull(policy.getAllowHeaders());
    Assertions.assertNull(policy.getAllowHeadersAsString());
    Assertions.assertNull(policy.getExposeHeaders());

    Assertions.assertFalse(policy.isAllowCredentials());
    Assertions.assertTrue(policy.getMaxAge() == 0);
  }

  @Test
  public void test_anyOrigin() {
    var policy = new CorsPolicy().setName("testCors").setAnyOrigin();

    Assertions.assertTrue(policy.isAnyOrigin());
    Assertions.assertTrue(policy.allowOrigin("http://myDomain.com"));
    Assertions.assertEquals("*", policy.getAllowOrigin("http://myDomain.com"));
  }

  @Test
  public void test_anyHeader() {
    var policy = new CorsPolicy().setName("testCors").setAnyOrigin().setAnyHeader();
    Assertions.assertEquals("*", policy.getAllowHeadersAsString());
  }

  @Test
  public void test_allowOrigin() {
    var policy = new CorsPolicy().setName("testCors").setAllowOrigins("http(s)?://(.+\\.)*myDomain\\.com");

    Assertions.assertTrue(policy.allowOrigin("https://myDomain.com"));
    Assertions.assertTrue(policy.allowOrigin("http://sub.myDomain.com"));
    Assertions.assertTrue(policy.allowOrigin("http://sub.sub.myDomain.com"));
  }

  @Test
  public void test_allowHeaders() {
    var policy = new CorsPolicy().setName("testCors").setAnyOrigin();
    policy.setAllowHeaders("X-Header", "X-Header2");

    Assertions.assertTrue(policy.allowHeaders("X-Header"));
    Assertions.assertTrue(policy.allowHeaders("X-Header, x-header2"));
    Assertions.assertFalse(policy.allowHeaders("X-Header, x-header2, x-header3"));
  }

  @Test
  public void test_setExposeHeaders() {
    var policy = new CorsPolicy().setName("testCors").setAnyOrigin();
    policy.setExposeHeaders("X-Header", "X-Header2");

    Assertions.assertTrue(policy.getExposeHeaders().contains("X-Header"));
    Assertions.assertTrue(policy.getExposeHeaders().contains("X-Header2"));
  }

  @Test
  public void test_setAllowAnyOrigin() {
    var policy = new CorsPolicy().setName("testCors").setAnyOrigin();
    policy.setAllowCredentials(true);

    Assertions.assertTrue(policy.isAllowCredentials());
  }

  @Test
  public void test_setMaxAge() {
    var policy = new CorsPolicy().setName("testCors").setAnyOrigin();
    policy.setMaxAge(365, TimeUnit.DAYS);

    Assertions.assertEquals(TimeUnit.SECONDS.convert(365, TimeUnit.DAYS), policy.getMaxAge());
  }
}
