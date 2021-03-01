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
public class CacheControlBuilderTest {

	@Test
	public void test_noStore() {
		CacheControlBuilder builder = new CacheControlBuilder();

		builder.noStore();
		Assert.assertTrue(builder.toString().contains("no-store"));
	}

	@Test
	public void test_noCache() {
		CacheControlBuilder builder = new CacheControlBuilder();

		builder.noCache();
		Assert.assertTrue(builder.toString().contains("no-cache"));
	}

	@Test
	public void test_mustRevalidate() {
		CacheControlBuilder builder = new CacheControlBuilder();

		builder.mustRevalidate();
		Assert.assertTrue(builder.toString().contains("must-revalidate"));
	}

	@Test
	public void test_proxyRevalidate() {
		CacheControlBuilder builder = new CacheControlBuilder();

		builder.proxyRevalidate();
		Assert.assertTrue(builder.toString().contains("proxy-revalidate"));
	}

	@Test
	public void test_usePublic() {
		CacheControlBuilder builder = new CacheControlBuilder();

		builder.usePublic();
		Assert.assertTrue(builder.toString().contains("public"));
	}

	@Test
	public void test_usePrivate() {
		CacheControlBuilder builder = new CacheControlBuilder();

		builder.usePrivate();
		Assert.assertTrue(builder.toString().contains("private"));
	}

	@Test
	public void test_noTransform() {
		CacheControlBuilder builder = new CacheControlBuilder();

		builder.noTransform();
		Assert.assertTrue(builder.toString().contains("no-transform"));
	}

	@Test
	public void test_maxAge() {
		CacheControlBuilder builder = new CacheControlBuilder();

		builder.maxAge(10000, TimeUnit.SECONDS);
		Assert.assertTrue(builder.toString().contains("max-age=10000"));
	}

	@Test
	public void test_sMaxAge() {
		CacheControlBuilder builder = new CacheControlBuilder();

		builder.sMaxAge(10000, TimeUnit.SECONDS);
		Assert.assertTrue(builder.toString().contains("s-maxage=10000"));
	}

	@Test
	public void test_combine() {
		CacheControlBuilder builder = new CacheControlBuilder();

		builder.maxAge(10000, TimeUnit.SECONDS).usePrivate();
		Assert.assertTrue(builder.toString().contains("max-age=10000, private"));
	}

	@Test
	public void test_disableCache() {
		CacheControlBuilder builder = new CacheControlBuilder();

		builder.maxAge(0, TimeUnit.SECONDS).noCache().noStore().mustRevalidate();
		Assert.assertTrue(builder.toString().contains("max-age=0, no-cache, no-store, must-revalidate"));
	}
}
