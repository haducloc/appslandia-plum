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

package com.appslandia.plum.tags;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.appslandia.plum.base.Controller;
import com.appslandia.plum.base.HttpPost;
import com.appslandia.plum.base.MockTestBase;
import com.appslandia.plum.mocks.MockJspContext;
import com.appslandia.plum.utils.TestUtils;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class FieldErrorTest extends MockTestBase {

	FieldErrorTag tag = new FieldErrorTag();

	@BeforeClass
	public static void onInvokingClass() {
		TestUtils.initExpressionEvaluator();
	}

	@Override
	protected void initialize() {
		container.register(TestController.class, TestController.class);
	}

	@Before
	public void onInvokingTest() {
		tag.setJspContext(new MockJspContext(getCurrentRequest(), getCurrentResponse()));
		executeCurrent("POST", "http://localhost/app/testController/index");
	}

	@Test
	public void test() {
		try {
			tag.setField("userName");

			tag.doTag();
			String html = tag.getPageContext().getOut().toString();

			Assert.assertTrue(html.contains("field-error-msg"));
			Assert.assertTrue(html.contains("display:none"));

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_error() {
		try {
			getCurrentModelState().addError("userName", "userName is required.");

			tag.setField("userName");
			tag.setClazz("class1");

			tag.doTag();
			String html = tag.getPageContext().getOut().toString();

			Assert.assertTrue(html.contains("field-error-msg"));
			Assert.assertFalse(html.contains("display:none"));
			Assert.assertTrue(html.contains("userName is required."));

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Controller("testController")
	public static class TestController {

		@HttpPost
		public void index() {
		}
	}
}
