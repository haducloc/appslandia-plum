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

import com.appslandia.common.utils.AssertUtils;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class ResponseKeyBuilderTest extends MockTestBase {

	@Override
	protected void initialize() {
		container.register(TestController.class, TestController.class);
	}

	@Test
	public void test_testAction() {
		executeCurrent("GET", "http://localhost/app/testController/testAction");

		String responseKey = ResponseKeyBuilder.getResponseKey(getCurrentRequest(), "/{controller}/{action}", false);
		AssertUtils.assertNotNull(responseKey);

		Assert.assertEquals(responseKey, "/testController/testAction");
	}

	@Test
	public void test_testAction_lang() {
		executeCurrent("GET", "http://localhost/app/testController/testAction");

		String responseKey = ResponseKeyBuilder.getResponseKey(getCurrentRequest(), "/{language}/{controller}/{action}", false);
		AssertUtils.assertNotNull(responseKey);

		Assert.assertEquals(responseKey, "/en/testController/testAction");
	}

	@Test
	public void test_testAction_params() {

		getCurrentRequest().addParameter("testParam", "param1");
		executeCurrent("GET", "http://localhost/app/testController/testAction");

		String responseKey = ResponseKeyBuilder.getResponseKey(getCurrentRequest(), "/{controller}/{action}?testParam={testParam}", false);
		AssertUtils.assertNotNull(responseKey);

		Assert.assertEquals(responseKey, "/testController/testAction?testParam=param1");
	}

	@Test
	public void test_testAction_gzip() {
		executeCurrent("GET", "http://localhost/app/testController/testAction");

		String responseKey = ResponseKeyBuilder.getResponseKey(getCurrentRequest(), "/{controller}/{action}", true);
		AssertUtils.assertNotNull(responseKey);

		Assert.assertEquals(responseKey, "/testController/testAction#gzip");
	}

	@Controller("testController")
	public static class TestController {

		@HttpGet
		public void testAction() throws Exception {
		}

		@HttpGet
		@PathParams("/{testPath}")
		public void testActionWithPath() throws Exception {
		}
	}
}
