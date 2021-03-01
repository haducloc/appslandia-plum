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

import org.junit.Assert;
import org.junit.Test;

import com.appslandia.common.utils.MimeTypes;
import com.appslandia.plum.results.TextResult;
import com.appslandia.plum.utils.TestUtils;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class EnableGzipTest extends MockTestBase {

	@Override
	protected void initialize() {
		container.register(TestController.class, TestController.class);
	}

	@Test
	public void test_testGzip() {
		try {
			getCurrentRequest().setHeader("Accept-Encoding", "gzip");

			executeCurrent("GET", "http://localhost/app/testController/testGzip");

			Assert.assertEquals(getCurrentResponse().getStatus(), 200);
			Assert.assertNull(getCurrentResponse().getHeader("Content-Length"));
			Assert.assertEquals(getCurrentResponse().getHeader("Content-Encoding"), "gzip");

			String data = TestUtils.ungzipToString(getCurrentResponse().getContent());
			Assert.assertEquals(data, "data");

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_testGzip_HEAD() {
		try {
			getCurrentRequest().setHeader("Accept-Encoding", "gzip");

			executeCurrent("HEAD", "http://localhost/app/testController/testGzip");

			Assert.assertEquals(getCurrentResponse().getStatus(), 200);
			Assert.assertNotNull(getCurrentResponse().getHeader("Content-Length"));
			Assert.assertEquals(getCurrentResponse().getHeader("Content-Encoding"), "gzip");

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_testGzip_POST() {
		try {
			getCurrentRequest().setHeader("Accept-Encoding", "gzip");

			executeCurrent("POST", "http://localhost/app/testController/testGzip");

			Assert.assertEquals(getCurrentResponse().getStatus(), 200);
			Assert.assertNull(getCurrentResponse().getHeader("Content-Length"));
			Assert.assertEquals(getCurrentResponse().getHeader("Content-Encoding"), "gzip");

			String data = TestUtils.ungzipToString(getCurrentResponse().getContent());
			Assert.assertEquals(data, "data");

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Controller("testController")
	public static class TestController {

		@HttpGetPost
		@EnableGzip
		public ActionResult testGzip() throws Exception {
			return new TextResult("data", MimeTypes.TEXT_PLAIN);
		}
	}
}
