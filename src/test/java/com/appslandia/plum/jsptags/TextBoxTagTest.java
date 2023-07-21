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

package com.appslandia.plum.jsptags;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.appslandia.common.utils.DateUtils;
import com.appslandia.plum.base.BrowserFeatures;
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

	// Use vi locale
	executeCurrent("GET", "http://localhost/app/vi/testController/index");
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

	    Assertions.assertTrue(html.contains("id=\"userName\""));
	    Assertions.assertTrue(html.contains("name=\"userName\""));
	    Assertions.assertTrue(html.contains("type=\"text\""));

	    Assertions.assertTrue(html.contains("data-tag=\"tag1\""));
	    Assertions.assertTrue(html.contains("class=\"class1\""));
	    Assertions.assertTrue(html.contains("style=\"prop1:value1\""));
	    Assertions.assertTrue(html.contains("title=\"title1\""));

	    Assertions.assertTrue(html.contains("maxlength=\"100\""));
	    Assertions.assertTrue(html.contains("readonly=\"readonly\""));
	    Assertions.assertTrue(html.contains("placeholder=\"placeholder\""));

	    Assertions.assertTrue(html.contains("required=\"required\""));
	    Assertions.assertTrue(html.contains("hidden=\"hidden\""));
	    Assertions.assertFalse(html.contains("autocomplete=\"on\""));

	} catch (Exception ex) {
	    Assertions.fail(ex.getMessage());
	}
    }

    @Test
    public void test_noValue() {
	try {
	    tag.setPath("model.userName");
	    tag.setType("text");

	    tag.doTag();
	    String html = tag.getPageContext().getOut().toString();
	    Assertions.assertTrue(html.contains("value=\"\""));

	} catch (Exception ex) {
	    Assertions.fail(ex.getMessage());
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
	    Assertions.assertTrue(html.contains("value=\"testUser\""));

	} catch (Exception ex) {
	    Assertions.fail(ex.getMessage());
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
	    Assertions.assertTrue(html.contains("value=\"&lt; testUser\""));

	} catch (Exception ex) {
	    Assertions.fail(ex.getMessage());
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

	    Assertions.assertTrue(html.contains("readonly=\"readonly\""));

	    // Hidden
	    Assertions.assertFalse(html.contains("type=\"hidden\""));

	} catch (Exception ex) {
	    Assertions.fail(ex.getMessage());
	}
    }

    @Test
    public void test_inputdate_text() {
	try {
	    model.setDob(DateUtils.iso8601Date("2005-12-31"));
	    tag.setPath("model.dob");
	    tag.setType("text");

	    tag.doTag();
	    String html = tag.getPageContext().getOut().toString();

	    // type = text
	    Assertions.assertTrue(html.contains("value=\"31/12/2005\""));

	} catch (Exception ex) {
	    Assertions.fail(ex.getMessage());
	}
    }

    @Test
    public void test_inputdate_date() {
	try {
	    model.setDob(DateUtils.iso8601Date("2005-12-31"));
	    tag.setPath("model.dob");
	    tag.setType("date");

	    tag.doTag();
	    String html = tag.getPageContext().getOut().toString();

	    // type = date & browserFeatures is not parsed
	    Assertions.assertTrue(html.contains("value=\"2005-12-31\""));

	} catch (Exception ex) {
	    Assertions.fail(ex.getMessage());
	}
    }

    @Test
    public void test_inputdate__inputfeature() {
	try {
	    setRequestContextField("browserFeatures", BrowserFeatures.INPUT_NUMBER);

	    model.setDob(DateUtils.iso8601Date("2005-12-31"));
	    tag.setPath("model.dob");
	    tag.setType("date");

	    tag.doTag();
	    String html = tag.getPageContext().getOut().toString();

	    // type = date & browserFeatures is not INPUT_DATE
	    Assertions.assertTrue(html.contains("value=\"31/12/2005\""));

	} catch (Exception ex) {
	    Assertions.fail(ex.getMessage());
	}
    }

    @Test
    public void test_inputnumber_text() {
	try {
	    model.setSalary(99999.1235);
	    tag.setPath("model.salary");
	    tag.setType("text");

	    tag.doTag();
	    String html = tag.getPageContext().getOut().toString();

	    // type = text
	    Assertions.assertTrue(html.contains("value=\"99999,124\""));

	} catch (Exception ex) {
	    Assertions.fail(ex.getMessage());
	}
    }

    @Test
    public void test_inputnumber_number() {
	try {
	    model.setSalary(99999.1235);
	    tag.setPath("model.salary");
	    tag.setType("number");

	    tag.doTag();
	    String html = tag.getPageContext().getOut().toString();

	    // type = number & browserFeatures is not parsed
	    Assertions.assertTrue(html.contains("value=\"99999.124\""));

	} catch (Exception ex) {
	    Assertions.fail(ex.getMessage());
	}
    }

    @Test
    public void test_inputnumber__inputfeature() {
	try {
	    setRequestContextField("browserFeatures", BrowserFeatures.INPUT_DATE);

	    model.setSalary(99999.1235);
	    tag.setPath("model.salary");
	    tag.setType("number");

	    tag.doTag();
	    String html = tag.getPageContext().getOut().toString();

	    // type = number & browserFeatures is not INPUT_NUMBER
	    Assertions.assertTrue(html.contains("value=\"99999,124\""));

	} catch (Exception ex) {
	    Assertions.fail(ex.getMessage());
	}
    }

    public static class TestModel {
	private Integer userId;
	private String userName;
	private java.sql.Date dob;
	private double salary;
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

	public double getSalary() {
	    return salary;
	}

	public void setSalary(double salary) {
	    this.salary = salary;
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
