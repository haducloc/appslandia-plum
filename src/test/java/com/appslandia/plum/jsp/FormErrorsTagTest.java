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
import com.appslandia.plum.base.HttpPost;
import com.appslandia.plum.base.MockTestBase;
import com.appslandia.plum.mocks.MockJspContext;
import com.appslandia.plum.mocks.MockJspFragment;
import com.appslandia.plum.utils.TestUtils;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class FormErrorsTagTest extends MockTestBase {

    FormErrorsTag tag = new FormErrorsTag();

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
	executeCurrent("POST", "http://localhost/app/testController/index");
    }

    @Test
    public void test() {
	try {
	    tag.setId("formError1");
	    tag.setHidden(true);
	    tag.setDatatag("tag1");
	    tag.setClazz("class1");
	    tag.setStyle("prop1:value1");
	    tag.setTitle("title1");

	    tag.doTag();
	    String html = tag.getPageContext().getOut().toString();

	    Assertions.assertTrue(html.contains("class1 messages-error"));
	    Assertions.assertTrue(html.contains("display:none"));

	} catch (Exception ex) {
	    Assertions.fail(ex.getMessage());
	}
    }

    @Test
    public void test_hasErrors() {
	try {
	    getCurrentModelState().addError("userName", "userName is required.");
	    getCurrentModelState().addError("model error");

	    tag.doTag();
	    String html = tag.getPageContext().getOut().toString();

	    Assertions.assertTrue(html.contains("messages messages-error"));
	    Assertions.assertFalse(html.contains("display:none"));

	    Assertions.assertTrue(html.contains("<li>model error</li>"));
	    Assertions.assertTrue(html.contains("<li>userName is required.</li>"));

	} catch (Exception ex) {
	    Assertions.fail(ex.getMessage());
	}
    }

    @Test
    public void test_modelLevelOnly() {
	try {
	    getCurrentModelState().addError("userName", "userName is required.");
	    getCurrentModelState().addError("model error");
	    tag.setModelLevelOnly(true);

	    tag.doTag();
	    String html = tag.getPageContext().getOut().toString();

	    Assertions.assertTrue(html.contains("messages messages-error"));
	    Assertions.assertFalse(html.contains("display:none"));

	    Assertions.assertTrue(html.contains("<li>model error</li>"));
	    Assertions.assertFalse(html.contains("<li>userName is required.</li>"));

	} catch (Exception ex) {
	    Assertions.fail(ex.getMessage());
	}
    }

    @Test
    public void test_fieldOrders() {
	try {
	    getCurrentModelState().addError("userName", "userName is required.");
	    getCurrentModelState().addError("password", "password is required.");

	    FieldOrdersTag ordersTag = new FieldOrdersTag();
	    ordersTag.setJspBody(new MockJspFragment(tag.getPageContext(), "userName, password"));
	    tag.setJspBody(new MockJspFragment(tag.getPageContext(), tag, ordersTag));

	    tag.doTag();
	    String html = tag.getPageContext().getOut().toString();

	    Assertions.assertTrue(html.contains("messages messages-error"));
	    Assertions.assertFalse(html.contains("display:none"));

	    int idx1 = html.indexOf("<li>userName is required.</li>");
	    int idx2 = html.indexOf("<li>password is required.</li>");

	    Assertions.assertTrue(idx1 > 0);
	    Assertions.assertTrue(idx2 > 0);
	    Assertions.assertTrue(idx1 < idx2);

	} catch (Exception ex) {
	    Assertions.fail(ex.getMessage());
	}
    }

    @Controller("testController")
    public static class TestController {

	@HttpPost
	public void index() {
	}
    }
}
