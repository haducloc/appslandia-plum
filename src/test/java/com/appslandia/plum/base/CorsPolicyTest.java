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

package com.appslandia.plum.base;

import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class CorsPolicyTest {

	@Test
	public void test() {
		CorsPolicy policy = new CorsPolicy().setName("testCors").anyOrigin();
		Assert.assertNull(policy.getAllowHeaders());
		Assert.assertNull(policy.getAllowHeadersString());
		Assert.assertNull(policy.getExposeHeaders());

		Assert.assertFalse(policy.isAllowCredentials());
		Assert.assertTrue(policy.getMaxAge() == 0);
	}

	@Test
	public void test_anyOrigin() {
		CorsPolicy policy = new CorsPolicy().setName("testCors").anyOrigin();

		Assert.assertTrue(policy.isAnyOrigin());
		Assert.assertTrue(policy.allowOrigin("http://myDomain.com"));
		Assert.assertEquals(policy.getAllowOrigin("http://myDomain.com"), "*");
	}

	@Test
	public void test_anyHeader() {
		CorsPolicy policy = new CorsPolicy().setName("testCors").anyOrigin().anyHeader();
		Assert.assertEquals(policy.getAllowHeadersString(), "*");
	}

	@Test
	public void test_allowOrigin() {
		CorsPolicy policy = new CorsPolicy().setName("testCors").setAllowOrigins("http(s)?://(.+\\.)*myDomain\\.com");

		Assert.assertTrue(policy.allowOrigin("https://myDomain.com"));
		Assert.assertTrue(policy.allowOrigin("http://sub.myDomain.com"));
		Assert.assertTrue(policy.allowOrigin("http://sub.sub.myDomain.com"));
	}

	@Test
	public void test_allowHeaders() {
		CorsPolicy policy = new CorsPolicy().setName("testCors").anyOrigin();
		policy.setAllowHeaders("X-Header", "X-Header2");

		Assert.assertTrue(policy.allowHeaders("X-Header"));
		Assert.assertTrue(policy.allowHeaders("X-Header, x-header2"));
		Assert.assertFalse(policy.allowHeaders("X-Header, x-header2, x-header3"));
	}

	@Test
	public void test_setExposeHeaders() {
		CorsPolicy policy = new CorsPolicy().setName("testCors").anyOrigin();
		policy.setExposeHeaders("X-Header", "X-Header2");

		Assert.assertTrue(policy.getExposeHeaders().contains("X-Header"));
		Assert.assertTrue(policy.getExposeHeaders().contains("X-Header2"));
	}

	@Test
	public void test_setAllowAnyOrigin() {
		CorsPolicy policy = new CorsPolicy().setName("testCors").anyOrigin();
		policy.setAllowCredentials(true);

		Assert.assertTrue(policy.isAllowCredentials());
	}

	@Test
	public void test_setMaxAge() {
		CorsPolicy policy = new CorsPolicy().setName("testCors").anyOrigin();
		policy.setMaxAge(365, TimeUnit.DAYS);

		Assert.assertEquals(policy.getMaxAge(), TimeUnit.SECONDS.convert(365, TimeUnit.DAYS));
	}
}
