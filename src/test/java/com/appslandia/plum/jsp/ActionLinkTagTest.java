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

package com.appslandia.plum.jsp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.appslandia.plum.base.Controller;
import com.appslandia.plum.base.HttpGet;
import com.appslandia.plum.base.MockTestBase;
import com.appslandia.plum.base.PathParams;
import com.appslandia.plum.mocks.MockJspContext;
import com.appslandia.plum.utils.TestUtils;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class ActionLinkTagTest extends MockTestBase {

    ActionLinkTag tag = new ActionLinkTag();

    @BeforeAll
    public static void beforeAllTests() {
	TestUtils.initExpressionEvaluator();
    }

    @Override
    protected void initialize() {
	container.register(TestController.class, TestController.class);
    }

    @BeforeEach
    public void beforeEachTest() {
	tag.setJspContext(new MockJspContext(getCurrentRequest(), getCurrentResponse()));
	executeCurrent("GET", "http://localhost/app/testController/index");
    }

    @Test
    public void test() {
	try {
	    tag.setAction("actionNoParams");
	    tag.setController("testController");

	    tag.setId("id1");
	    tag.setHidden(true);

	    tag.setDatatag("tag1");
	    tag.setClazz("class1");
	    tag.setStyle("prop1:value1");
	    tag.setTitle("title1");

	    tag.doTag();
	    String html = tag.getPageContext().getOut().toString();

	    Assertions.assertTrue(html.contains("id=\"id1\""));
	    Assertions.assertTrue(html.contains("hidden=\"hidden\""));

	    Assertions.assertTrue(html.contains("data-tag=\"tag1\""));
	    Assertions.assertTrue(html.contains("class=\"class1\""));
	    Assertions.assertTrue(html.contains("style=\"prop1:value1\""));
	    Assertions.assertTrue(html.contains("title=\"title1\""));

	} catch (Exception ex) {
	    Assertions.fail(ex.getMessage());
	}
    }

    @Test
    public void test_actionNoParams() {
	try {
	    tag.setAction("actionNoParams");
	    tag.setController("testController");

	    tag.doTag();
	    String html = tag.getPageContext().getOut().toString();

	    Assertions.assertTrue(html.contains("/app/testController/actionNoParams/"));

	} catch (Exception ex) {
	    Assertions.fail(ex.getMessage());
	}
    }

    @Test
    public void test_actionPathParams() {
	try {
	    tag.setAction("actionPathParams");
	    tag.setController("testController");

	    tag.setDynamicAttribute(null, "__p1", "param1");
	    tag.setDynamicAttribute(null, "__p2", "param2");

	    tag.doTag();
	    String html = tag.getPageContext().getOut().toString();

	    Assertions.assertTrue(html.contains("/app/testController/actionPathParams/param1/?p2=param2"));

	} catch (Exception ex) {
	    Assertions.fail(ex.getMessage());
	}
    }

    @Test
    public void test_testEvents() {
	try {
	    tag.setAction("actionNoParams");
	    tag.setController("testController");

	    tag.setDynamicAttribute(null, "onclick", "handleOnClick();");

	    tag.doTag();
	    String html = tag.getPageContext().getOut().toString();

	    Assertions.assertTrue(html.contains("onclick=\"handleOnClick();\""));

	} catch (Exception ex) {
	    Assertions.fail(ex.getMessage());
	}
    }

    @Controller("testController")
    public static class TestController {

	@HttpGet
	public void index() {
	}

	@HttpGet
	public void actionNoParams() {
	}

	@HttpGet
	@PathParams("/{p1}")
	public void actionPathParams() {
	}
    }
}