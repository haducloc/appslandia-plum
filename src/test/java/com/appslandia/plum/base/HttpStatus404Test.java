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

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class HttpStatus404Test extends MockTestBase {

	@Override
	protected void initialize() {
		container.register(TestController.class, TestController.class);
	}

	@Test
	public void test_notFound() {
		try {
			executeCurrent("GET", "http://localhost/app/testController/notAction");

			Assert.assertEquals(getCurrentResponse().getStatus(), 404);

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_actionNoPathParams_invalidPathParamsAdded() {
		try {
			executeCurrent("GET", "http://localhost/app/testController/actionNoPathParams/v1");

			Assert.assertEquals(getCurrentResponse().getStatus(), 404);

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_actionWithPathParams_pathParamsMissed() {
		try {
			executeCurrent("GET", "http://localhost/app/testController/actionWithPathParams/v1");

			Assert.assertEquals(getCurrentResponse().getStatus(), 404);

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_actionWithPathParams_invalidPathParamsFormat() {
		try {
			executeCurrent("GET", "http://localhost/app/testController/actionWithPathParams/v1/v2");

			Assert.assertEquals(getCurrentResponse().getStatus(), 404);

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_actionNotFoundException() {
		try {
			executeCurrent("GET", "http://localhost/app/testController/actionNotFoundException");

			Assert.assertEquals(getCurrentResponse().getStatus(), 404);

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Controller("testController")
	public static class TestController {

		@HttpGet
		public void actionNoPathParams() throws Exception {
		}

		@HttpGet
		@PathParams("/{p1}/{p2}-{p3}")
		public void actionWithPathParams() throws Exception {
		}

		@HttpGet
		public void actionNotFoundException() throws Exception {
			throw new NotFoundException("actionNotFoundException");
		}
	}
}
