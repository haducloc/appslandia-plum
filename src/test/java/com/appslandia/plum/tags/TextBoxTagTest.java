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

import com.appslandia.common.formatters.Formatter;
import com.appslandia.common.utils.DateUtils;
import com.appslandia.plum.base.Controller;
import com.appslandia.plum.base.HttpGet;
import com.appslandia.plum.base.MockTestBase;
import com.appslandia.plum.mocks.MockJspContext;
import com.appslandia.plum.utils.ServletUtils;
import com.appslandia.plum.utils.TestUtils;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class TextBoxTagTest extends MockTestBase {

	TextBoxTag tag = new TextBoxTag();
	TestModel model = new TestModel();

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
		getCurrentRequest().setAttribute(ServletUtils.REQUEST_ATTRIBUTE_MODEL, model);
		executeCurrent("GET", "http://localhost/app/testController/index");
	}

	@Test
	public void test() {
		try {
			tag.setPath("model.userName");
			tag.setType("text");

			tag.setMaxlength("100");
			tag.setReadonly(true);
			tag.setPlaceholder("placeholder");

			tag.setRequired(true);
			tag.setHidden(true);
			tag.setAutocomplete("off");

			tag.setDatatag("tag1");
			tag.setClazz("class1");
			tag.setStyle("prop1:value1");
			tag.setTitle("title1");

			tag.doTag();
			String html = tag.getPageContext().getOut().toString();

			Assert.assertTrue(html.contains("id=\"userName\""));
			Assert.assertTrue(html.contains("name=\"userName\""));
			Assert.assertTrue(html.contains("type=\"text\""));

			Assert.assertTrue(html.contains("data-tag=\"tag1\""));
			Assert.assertTrue(html.contains("class=\"class1\""));
			Assert.assertTrue(html.contains("style=\"prop1:value1\""));
			Assert.assertTrue(html.contains("title=\"title1\""));

			Assert.assertTrue(html.contains("maxlength=\"100\""));
			Assert.assertTrue(html.contains("readonly=\"readonly\""));
			Assert.assertTrue(html.contains("placeholder=\"placeholder\""));

			Assert.assertTrue(html.contains("required=\"required\""));
			Assert.assertTrue(html.contains("hidden=\"hidden\""));
			Assert.assertFalse(html.contains("autocomplete=\"on\""));

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_noValue() {
		try {
			tag.setPath("model.userName");
			tag.setType("text");

			tag.doTag();
			String html = tag.getPageContext().getOut().toString();
			Assert.assertTrue(html.contains("value=\"\""));

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_value() {
		try {
			model.setUserName("testUser");
			tag.setPath("model.userName");
			tag.setType("text");

			tag.doTag();
			String html = tag.getPageContext().getOut().toString();
			Assert.assertTrue(html.contains("value=\"testUser\""));

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_escaped() {
		try {
			model.setUserName("< testUser");

			tag.setPath("model.userName");
			tag.setType("text");

			tag.doTag();
			String html = tag.getPageContext().getOut().toString();
			Assert.assertTrue(html.contains("value=\"&lt; testUser\""));

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_readonly() {
		try {
			model.setUserName("testUser");
			tag.setPath("model.userName");
			tag.setType("text");
			tag.setReadonly(true);

			tag.doTag();
			String html = tag.getPageContext().getOut().toString();

			Assert.assertTrue(html.contains("readonly=\"readonly\""));

			// Hidden
			Assert.assertFalse(html.contains("type=\"hidden\""));

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_fmt_date() {
		try {
			model.setDob(DateUtils.iso8601Date("2005-12-31"));
			tag.setPath("model.dob");
			tag.setType("text");

			tag.doTag();
			String html = tag.getPageContext().getOut().toString();

			// No Date Format Specified -> ToString
			Assert.assertTrue(html.contains("value=\"2005-12-31\""));

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_fmt_DateL() {
		try {
			model.setDob(DateUtils.iso8601Date("2005-12-31"));
			tag.setPath("model.dob");
			tag.setType("text");
			tag.setFmt(Formatter.DATE_L);

			tag.doTag();
			String html = tag.getPageContext().getOut().toString();

			// Date Format Specified
			Assert.assertTrue(html.contains("value=\"12/31/2005\""));

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	public static class TestModel {
		private Integer userId;
		private String userName;
		private java.sql.Date dob;
		private boolean isActive;

		public Integer getUserId() {
			return userId;
		}

		public void setUserId(Integer userId) {
			this.userId = userId;
		}

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		public java.sql.Date getDob() {
			return dob;
		}

		public void setDob(java.sql.Date dob) {
			this.dob = dob;
		}

		public boolean isActive() {
			return isActive;
		}

		public void setActive(boolean isActive) {
			this.isActive = isActive;
		}
	}

	@Controller("testController")
	public static class TestController {

		@HttpGet
		public void index() {
		}
	}
}
