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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.appslandia.common.base.Params;
import com.appslandia.common.utils.AssertUtils;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class ActionParserTest extends MockTestBase {

	ActionParser actionParser;

	@Override
	protected void initialize() {
		container.register(TestController.class, TestController.class);
		actionParser = container.getObject(ActionParser.class);
	}

	@Test
	public void test_index() {
		try {
			getCurrentRequest().setRequestURL("http://localhost/app/testController");
			List<String> pathItems = RequestContextParser.parsePathItems(getCurrentRequest());

			Map<String, String> pathParamMap = new HashMap<>();
			ActionDesc actionDesc = actionParser.parse(pathItems, pathParamMap);
			AssertUtils.assertNotNull(actionDesc);

			Assert.assertEquals(actionDesc.getAction(), "index");
			Assert.assertEquals(actionDesc.getController(), "testController");
			Assert.assertEquals(pathParamMap.size(), 0);

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_actionNoPathParams() {
		try {
			getCurrentRequest().setRequestURL("http://localhost/app/testController/actionNoPathParams");
			List<String> pathItems = RequestContextParser.parsePathItems(getCurrentRequest());

			Map<String, String> pathParamMap = new HashMap<>();
			ActionDesc actionDesc = actionParser.parse(pathItems, pathParamMap);
			AssertUtils.assertNotNull(actionDesc);

			Assert.assertEquals(actionDesc.getAction(), "actionNoPathParams");
			Assert.assertEquals(actionDesc.getController(), "testController");
			Assert.assertEquals(pathParamMap.size(), 0);

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_actionNoPathParams_invalidPathParamsAdded() {
		try {
			getCurrentRequest().setRequestURL("http://localhost/app/testController/actionNoPathParams/param1");
			List<String> pathItems = RequestContextParser.parsePathItems(getCurrentRequest());

			Map<String, String> pathParamMap = new HashMap<>();
			ActionDesc actionDesc = actionParser.parse(pathItems, pathParamMap);
			Assert.assertNull(actionDesc);

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_actionPathParams_pathParamsProvided() {
		try {
			getCurrentRequest().setRequestURL("http://localhost/app/testController/actionPathParams/param1/param2");
			List<String> pathItems = RequestContextParser.parsePathItems(getCurrentRequest());

			Map<String, String> pathParamMap = new HashMap<>();
			ActionDesc actionDesc = actionParser.parse(pathItems, pathParamMap);
			AssertUtils.assertNotNull(actionDesc);

			Assert.assertEquals(actionDesc.getAction(), "actionPathParams");
			Assert.assertEquals(actionDesc.getController(), "testController");
			Assert.assertEquals(pathParamMap.size(), 2);

			Assert.assertEquals(pathParamMap.get("param1"), "param1");
			Assert.assertEquals(pathParamMap.get("param2"), "param2");

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_actionPathParams_pathParamsMissed() {
		try {
			getCurrentRequest().setRequestURL("http://localhost/app/testController/actionPathParams/param1");
			List<String> pathItems = RequestContextParser.parsePathItems(getCurrentRequest());

			Map<String, String> pathParamMap = new HashMap<>();
			ActionDesc actionDesc = actionParser.parse(pathItems, pathParamMap);
			Assert.assertNull(actionDesc);

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_actionSubPathParams_pathParamsProvided() {
		try {
			getCurrentRequest().setRequestURL("http://localhost/app/testController/actionSubPathParams/param1-param2");
			List<String> pathItems = RequestContextParser.parsePathItems(getCurrentRequest());

			Map<String, String> pathParamMap = new HashMap<>();
			ActionDesc actionDesc = actionParser.parse(pathItems, pathParamMap);
			AssertUtils.assertNotNull(actionDesc);

			Assert.assertEquals(actionDesc.getAction(), "actionSubPathParams");
			Assert.assertEquals(actionDesc.getController(), "testController");
			Assert.assertEquals(pathParamMap.size(), 2);

			Assert.assertEquals(pathParamMap.get("param1"), "param1");
			Assert.assertEquals(pathParamMap.get("param2"), "param2");

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_actionSubPathParams_pathParamsMissed() {
		try {
			getCurrentRequest().setRequestURL("http://localhost/app/testController/actionSubPathParams/param1");
			List<String> pathItems = RequestContextParser.parsePathItems(getCurrentRequest());

			Map<String, String> pathParamMap = new HashMap<>();
			ActionDesc actionDesc = actionParser.parse(pathItems, pathParamMap);
			Assert.assertNull(actionDesc);

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_actionSubPathParams_invalidPathParamsAdded() {
		try {
			getCurrentRequest().setRequestURL("http://localhost/app/testController/actionSubPathParams/param1-param2/param3");
			List<String> pathItems = RequestContextParser.parsePathItems(getCurrentRequest());

			Map<String, String> pathParamMap = new HashMap<>();
			ActionDesc actionDesc = actionParser.parse(pathItems, pathParamMap);
			Assert.assertNull(actionDesc);

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_toActionLink() {
		try {
			executeCurrent("GET", "http://localhost/app/testController/index");

			Map<String, Object> attributes = new Params().set("class", "class1");
			String link = actionParser.toActionLink(getCurrentRequest(), "testController", "index", new Params().set("key1", "val1"), attributes, ">link");

			Assert.assertEquals(link, "<a href=\"/app/testController/?key1=val1\" class=\"class1\">&gt;link</a>");

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Controller("testController")
	@Home
	public static class TestController {

		@HttpGet
		public void index() {
		}

		@HttpGet
		public void actionNoPathParams() {
		}

		@HttpGet
		@PathParams("/{param1}/{param2}")
		public void actionPathParams() {
		}

		@HttpGet
		@PathParams("/{param1}-{param2}")
		public void actionSubPathParams() {
		}
	}
}
