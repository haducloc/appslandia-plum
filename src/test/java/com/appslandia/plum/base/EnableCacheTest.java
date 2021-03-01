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

import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Test;

import com.appslandia.common.caching.AppCacheManager;
import com.appslandia.common.utils.MimeTypes;
import com.appslandia.plum.results.TextResult;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class EnableCacheTest extends MockTestBase {

	@Override
	protected void initialize() {
		container.register(TestController.class, TestController.class);

		MemAppCacheManager appCacheManager = container.getObject(AppCacheManager.class);
		appCacheManager.createCache(EnableCache.DEFAULT_CACHE, 32);
	}

	private void initCacheResponse() throws Exception {
		execute(container.createRequest("GET", "http://localhost/app/testController/testCache"), container.createResponse());
	}

	@Test
	public void test_testCache() {
		try {
			executeCurrent("GET", "http://localhost/app/testController/testCache");

			Assert.assertEquals(getCurrentResponse().getStatus(), 200);
			Assert.assertEquals(getCurrentResponse().getContent().toString(StandardCharsets.UTF_8), "data");
			Assert.assertNotNull(getCurrentResponse().getHeader("Content-Length"));

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_testCache_HEAD() {
		try {
			executeCurrent("HEAD", "http://localhost/app/testController/testCache");

			Assert.assertEquals(getCurrentResponse().getStatus(), 200);
			Assert.assertNotNull(getCurrentResponse().getHeader("Content-Length"));

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_testCache_next() {
		try {
			initCacheResponse();
			getCurrentRequest().addParameter("next", "true");

			executeCurrent("GET", "http://localhost/app/testController/testCache");

			Assert.assertEquals(getCurrentResponse().getStatus(), 200);
			Assert.assertEquals(getCurrentResponse().getContent().toString(StandardCharsets.UTF_8), "data");
			Assert.assertNotNull(getCurrentResponse().getHeader("Content-Length"));

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_testCache_next_HEAD() {
		try {
			initCacheResponse();
			getCurrentRequest().addParameter("next", "true");

			executeCurrent("HEAD", "http://localhost/app/testController/testCache");

			Assert.assertEquals(getCurrentResponse().getStatus(), 200);
			Assert.assertNotNull(getCurrentResponse().getHeader("Content-Length"));

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Controller("testController")
	public static class TestController {

		@HttpGet
		@EnableCache(cacheKey = "/{controller}/{action}")
		public ActionResult testCache(HttpServletRequest request) throws Exception {
			if (request.getParameter("next") != null) {
				throw new Exception();
			}
			return new TextResult("data", MimeTypes.TEXT_PLAIN);
		}
	}
}
