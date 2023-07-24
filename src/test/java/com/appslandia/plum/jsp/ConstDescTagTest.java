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

import com.appslandia.common.base.ConstDesc;
import com.appslandia.plum.base.ConstDescProvider;
import com.appslandia.plum.base.Controller;
import com.appslandia.plum.base.HttpGet;
import com.appslandia.plum.base.MockTestBase;
import com.appslandia.plum.mocks.MockJspContext;
import com.appslandia.plum.utils.TestUtils;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class ConstDescTagTest extends MockTestBase {

    ConstDescTag tag = new ConstDescTag();
    ConstDescProvider constDescProvider;

    @BeforeAll
    public static void beforeAllTests() {
	TestUtils.initExpressionEvaluator();
    }

    @Override
    protected void initialize() {
	container.register(TestController.class, TestController.class);
	constDescProvider = container.getObject(ConstDescProvider.class);
    }

    @BeforeEach
    public void beforeEachTest() {
	tag.setJspContext(new MockJspContext(getCurrentRequest(), getCurrentResponse()));
	executeCurrent("GET", "http://localhost/app/testController/index");
    }

    @Test
    public void test() {
	try {
	    constDescProvider.addConstClass(Actives.class);
	    tag.setConstGroup("actives");
	    tag.setValue(Actives.ACTIVE);

	    tag.doTag();
	    String html = tag.getPageContext().getOut().toString();
	    Assertions.assertTrue(html.contains(":actives.active"));

	} catch (Exception ex) {
	    Assertions.fail(ex.getMessage());
	}
    }

    @Test
    public void test_notConfigured() {
	try {
	    tag.setConstGroup("actives");
	    tag.setValue(Actives.INACTIVE);

	    tag.doTag();
	    String html = tag.getPageContext().getOut().toString();
	    Assertions.assertEquals(Integer.toString(Actives.INACTIVE), html);

	} catch (Exception ex) {
	    Assertions.fail(ex.getMessage());
	}
    }

    public static final class Actives {

	@ConstDesc("actives")
	public static final int ACTIVE = 1;

	@ConstDesc("actives")
	public static final int INACTIVE = 0;
    }

    @Controller("testController")
    public static class TestController {

	@HttpGet
	public void index() {
	}
    }
}
