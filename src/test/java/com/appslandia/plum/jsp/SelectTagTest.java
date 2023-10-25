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

import com.appslandia.common.models.SelectItemImpl;
import com.appslandia.common.utils.CollectionUtils;
import com.appslandia.common.utils.NormalizeUtils;
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
	executeCurrent("GET", "http://localhost/app/testController/index");
    }

    @Test
    public void test() {
	try {
	    model.setUserType(2);

	    tag.setPath("model.userType");
	    tag.setItems(CollectionUtils.toList(new SelectItemImpl(1, "type1"), new SelectItemImpl(2, "type2")));

	    tag.doTag();
	    String html = tag.getPageContext().getOut().toString();

	    Assertions.assertEquals(
		    "<select id=\"userType\" name=\"userType\"> <option value=\"1\">type1</option> <option value=\"2\" selected=\"selected\">type2</option> </select>",
		    NormalizeUtils.toSingleLine(html));

	} catch (Exception ex) {
	    Assertions.fail(ex.getMessage());
	}
    }

    @Test
    public void test_addOption() {
	try {
	    model.setUserType(2);
	    tag.setPath("model.userType");

	    tag.addOption("type1", 1);
	    tag.addOption("type2", 2);

	    tag.doTag();
	    String html = tag.getPageContext().getOut().toString();

	    Assertions.assertEquals(
		    "<select id=\"userType\" name=\"userType\"> <option value=\"1\">type1</option> <option value=\"2\" selected=\"selected\">type2</option> </select>",
		    NormalizeUtils.toSingleLine(html));

	} catch (Exception ex) {
	    Assertions.fail(ex.getMessage());
	}
    }

    @Test
    public void test_unselected() {
	try {
	    tag.setPath("model.userType");

	    tag.addOption("type1", 1);
	    tag.addOption("type2", 2);

	    tag.doTag();
	    String html = tag.getPageContext().getOut().toString();

	    Assertions.assertEquals("<select id=\"userType\" name=\"userType\"> <option value=\"1\">type1</option> <option value=\"2\">type2</option> </select>",
		    NormalizeUtils.toSingleLine(html));

	} catch (Exception ex) {
	    Assertions.fail(ex.getMessage());
	}
    }

    @Test
    public void test_selected_readonly() {
	try {
	    model.setUserType(2);
	    tag.setPath("model.userType");

	    tag.addOption("type1", 1);
	    tag.addOption("type2", 2);

	    tag.setReadonly(true);

	    tag.doTag();
	    String html = tag.getPageContext().getOut().toString();

	    // type1 is removed from the options
	    // plus hidden
	    Assertions.assertEquals(
		    "<select id=\"userType\" name=\"userType\" disabled=\"disabled\"> <option value=\"2\" selected=\"selected\">type2</option> </select> <input name=\"userType\" value=\"2\" type=\"hidden\" />",
		    NormalizeUtils.toSingleLine(html));

	} catch (Exception ex) {
	    Assertions.fail(ex.getMessage());
	}
    }

    @Test
    public void test_unselected_readonly() {
	try {
	    model.setUserType(0);
	    tag.setPath("model.userType");

	    tag.addOption("type1", 1);
	    tag.addOption("type2", 2);

	    tag.setReadonly(true);

	    tag.doTag();
	    String html = tag.getPageContext().getOut().toString();

	    // Empty dropdown in this case
	    // No hidden

	    Assertions.assertEquals("<select id=\"userType\" name=\"userType\" disabled=\"disabled\"> </select>", NormalizeUtils.toSingleLine(html));

	} catch (Exception ex) {
	    Assertions.fail(ex.getMessage());
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
