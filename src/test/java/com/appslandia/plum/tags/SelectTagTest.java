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

import com.appslandia.common.models.SelectItemImpl;
import com.appslandia.common.utils.CollectionUtils;
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
public class SelectTagTest extends MockTestBase {

	SelectTag tag = new SelectTag();
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
			tag.setPath("model.userType");
			tag.setSize("5");
			tag.setMultiple(true);

			tag.setRequired(true);
			tag.setHidden(true);

			tag.setDatatag("tag1");
			tag.setClazz("class1");
			tag.setStyle("prop1:value1");
			tag.setTitle("title1");

			tag.doTag();
			String html = tag.getPageContext().getOut().toString();

			Assert.assertTrue(html.contains("id=\"userType\""));
			Assert.assertTrue(html.contains("name=\"userType\""));
			Assert.assertTrue(html.contains("size=\"5\""));
			Assert.assertTrue(html.contains("multiple=\"multiple\""));

			Assert.assertTrue(html.contains("required=\"required\""));
			Assert.assertTrue(html.contains("hidden=\"hidden\""));

			Assert.assertTrue(html.contains("data-tag=\"tag1\""));
			Assert.assertTrue(html.contains("class=\"class1\""));
			Assert.assertTrue(html.contains("style=\"prop1:value1\""));
			Assert.assertTrue(html.contains("title=\"title1\""));

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_listItems() {
		try {
			model.setUserType(2);
			tag.setPath("model.userType");
			tag.setVar("opt");
			tag.setItems(CollectionUtils.toList(new SelectItemImpl(1, "type1"), new SelectItemImpl(2, "type2"), new SelectItemImpl(3, "type3")));

			tag.doTag();
			String html = tag.getPageContext().getOut().toString();

			Assert.assertTrue(html.contains("<option value=\"1\">type1</option>"));
			Assert.assertTrue(html.contains("<option value=\"2\" selected=\"selected\">type2</option>"));
			Assert.assertTrue(html.contains("<option value=\"3\">type3</option>"));

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_directItems() {
		try {
			model.setUserType(2);
			tag.setPath("model.userType");

			tag.addItem("type1", 1);
			tag.addItem("type2", 2);
			tag.addItem("type3", 3);

			tag.doTag();
			String html = tag.getPageContext().getOut().toString();
			Assert.assertTrue(html.contains("<option value=\"1\">type1</option>"));
			Assert.assertTrue(html.contains("<option value=\"2\" selected=\"selected\">type2</option>"));
			Assert.assertTrue(html.contains("<option value=\"3\">type3</option>"));

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_noSelected() {
		try {
			tag.setPath("model.userType");
			tag.addItem("type1", 1);
			tag.addItem("type2", 2);
			tag.addItem("type3", 3);

			tag.doTag();
			String html = tag.getPageContext().getOut().toString();
			Assert.assertFalse(html.contains("selected=\"selected\""));

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_selected_readonly() {
		try {
			model.setUserType(2);
			tag.setPath("model.userType");
			tag.addItem("type1", 1);
			tag.addItem("type2", 2);
			tag.addItem("type3", 3);
			tag.setReadonly(true);

			tag.doTag();
			String html = tag.getPageContext().getOut().toString();
			Assert.assertTrue(html.contains("disabled=\"disabled\""));
			Assert.assertTrue(html.contains("<option value=\"2\" selected=\"selected\">type2</option>"));

			// Has hidden
			Assert.assertTrue(html.contains("type=\"hidden\""));

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_noSelected_readonly() {
		try {
			model.setUserType(0);
			tag.setPath("model.userType");
			tag.addItem("type1", 1);
			tag.addItem("type2", 2);
			tag.addItem("type3", 3);
			tag.setReadonly(true);

			tag.doTag();
			String html = tag.getPageContext().getOut().toString();
			Assert.assertTrue(html.contains("disabled=\"disabled\""));
			Assert.assertFalse(html.contains("selected=\"selected\""));

			// No hidden
			Assert.assertFalse(html.contains("type=\"hidden\""));

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_escaped() {
		try {
			tag.setPath("model.userType");
			tag.addItem(">type1", 1);

			tag.doTag();
			String html = tag.getPageContext().getOut().toString();
			Assert.assertTrue(html.contains("&gt;type1"));

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	public static class TestModel {

		private int userType;

		public int getUserType() {
			return this.userType;
		}

		public void setUserType(int userType) {
			this.userType = userType;
		}
	}

	@Controller("testController")
	public static class TestController {

		@HttpGet
		public void index() {
		}
	}
}
