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

import com.appslandia.common.utils.NormalizeUtils;
import com.appslandia.plum.base.BrowserFeatures;
import com.appslandia.plum.base.Controller;
import com.appslandia.plum.base.HttpGet;
import com.appslandia.plum.base.MockTestBase;
import com.appslandia.plum.mocks.MockJspContext;
import com.appslandia.plum.utils.ServletUtils;
import com.appslandia.plum.utils.TestUtils;

import jakarta.validation.constraints.NotNull;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class TextBoxTagTest extends MockTestBase {

    TextBoxTag tag = new TextBoxTag();
    TestModel model = new TestModel();

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
	getCurrentRequest().setAttribute(ServletUtils.REQUEST_ATTRIBUTE_MODEL, model);

	// VI Locale
	executeCurrent("GET", "http://localhost/app/vi/testController/index");
    }

    @Test
    public void test() {
	try {
	    model.setUserName("user1");
	    tag.setPath("model.userName");

	    tag.doTag();
	    String html = tag.getPageContext().getOut().toString();

	    Assertions.assertEquals("<input id=\"userName\" type=\"text\" name=\"userName\" value=\"user1\" />", NormalizeUtils.removeCrLf(html));

	} catch (Exception ex) {
	    Assertions.fail(ex.getMessage());
	}
    }

    @Test
    public void test_error() {
	try {
	    getCurrentModelState().addError("userName", "The userName field is required.");

	    tag.setPath("model.userName");

	    tag.doTag();
	    String html = tag.getPageContext().getOut().toString();

	    Assertions.assertEquals("<input id=\"userName\" type=\"text\" name=\"userName\" value=\"\" class=\"l-error-field\" />", NormalizeUtils.removeCrLf(html));

	} catch (Exception ex) {
	    Assertions.fail(ex.getMessage());
	}
    }

    @Test
    public void test_salary_text() {
	try {
	    model.setSalary(99999.1235);

	    tag.setPath("model.salary");
	    tag.setType("text");

	    tag.doTag();
	    String html = tag.getPageContext().getOut().toString();

	    // type = text
	    Assertions.assertEquals("<input id=\"salary\" type=\"text\" name=\"salary\" value=\"99999,124\" />", NormalizeUtils.removeCrLf(html));

	} catch (Exception ex) {
	    Assertions.fail(ex.getMessage());
	}
    }

    @Test
    public void test_salary_number() {
	try {
	    model.setSalary(99999.1235);
	    tag.setPath("model.salary");
	    tag.setType("number");

	    tag.doTag();
	    String html = tag.getPageContext().getOut().toString();

	    // type = number
	    Assertions.assertEquals("<input id=\"salary\" type=\"text\" name=\"salary\" value=\"99999,124\" />", NormalizeUtils.removeCrLf(html));

	} catch (Exception ex) {
	    Assertions.fail(ex.getMessage());
	}
    }

    @Test
    public void test_salary_browserFeatures() {
	try {
	    setRequestContextField("browserFeatures", BrowserFeatures.INPUT_NUMBER);

	    model.setSalary(99999.1235);
	    tag.setPath("model.salary");
	    tag.setType("number");

	    tag.doTag();
	    String html = tag.getPageContext().getOut().toString();

	    // type = number + browserFeatures
	    Assertions.assertEquals("<input id=\"salary\" type=\"number\" name=\"salary\" value=\"99999.124\" />", NormalizeUtils.removeCrLf(html));

	} catch (Exception ex) {
	    Assertions.fail(ex.getMessage());
	}
    }

    public static class TestModel {

	@NotNull
	private String userName;

	private double salary;

	public String getUserName() {
	    return userName;
	}

	public void setUserName(String userName) {
	    this.userName = userName;
	}

	public double getSalary() {
	    return salary;
	}

	public void setSalary(double salary) {
	    this.salary = salary;
	}
    }

    @Controller("testController")
    public static class TestController {

	@HttpGet
	public void index() {
	}
    }
}
