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
import com.appslandia.plum.mocks.MockJspContext;
import com.appslandia.plum.mocks.MockJspFragment;
import com.appslandia.plum.utils.TestUtils;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class FormTagTest extends MockTestBase {

    FormTag tag = new FormTag();

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
	tag.setJspBody(new MockJspFragment(tag.getPageContext()));
	executeCurrent("GET", "http://localhost/app/testController/index");
    }

    @Test
    public void test() {
	try {
	    tag.setAction("index");
	    tag.setController("testController");

	    tag.setId("form1");
	    tag.setName("form1");

	    tag.setAcceptCharset("UTF-8");
	    tag.setEnctype("application/x-www-form-urlencoded");
	    tag.setMethod("POST");

	    tag.setNovalidate(true);
	    tag.setAutocomplete("off");

	    tag.setHidden(true);
	    tag.setDatatag("tag1");
	    tag.setClazz("class1");
	    tag.setStyle("prop1:value1");
	    tag.setTitle("title1");

	    tag.doTag();
	    String html = tag.getPageContext().getOut().toString();

	    Assertions.assertTrue(html.contains("id=\"form1\""));
	    Assertions.assertTrue(html.contains("name=\"form1\""));

	    Assertions.assertTrue(html.contains("accept-charset=\"UTF-8\""));
	    Assertions.assertTrue(html.contains("enctype=\"application/x-www-form-urlencoded\""));
	    Assertions.assertTrue(html.contains("method=\"POST\""));

	    Assertions.assertTrue(html.contains("novalidate=\"novalidate\""));
	    Assertions.assertTrue(html.contains("autocomplete"));

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
    public void test_index() {
	try {
	    tag.setAction("index");
	    tag.setController("testController");

	    tag.doTag();
	    String html = tag.getPageContext().getOut().toString();
	    Assertions.assertTrue(html.contains("/app/testController/"));

	} catch (Exception ex) {
	    Assertions.fail(ex.getMessage());
	}
    }

    @Controller("testController")
    public static class TestController {

	@HttpGet
	public void index() {
	}
    }
}
