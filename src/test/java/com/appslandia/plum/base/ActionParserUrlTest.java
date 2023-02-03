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
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class ActionParserUrlTest extends MockTestBase {

    ActionParser actionParser;
    RequestContextParser requestContextParser;

    @Override
    protected void initialize() {
	container.register(TestController.class, TestController.class);
	actionParser = container.getObject(ActionParser.class);
	requestContextParser = container.getObject(RequestContextParser.class);
    }

    @Test
    public void test_index() {
	try {
	    getCurrentRequest().setRequestURL("http://localhost/app/testController/index/param1");
	    requestContextParser.parse(getCurrentRequest(), getCurrentResponse());

	    Map<String, Object> params = new HashMap<>();
	    params.put("param1", "param1");
	    params.put("param2", "param2");

	    String url = actionParser.toActionUrl(getCurrentRequest(), "testController", "index", params, false);
	    Assertions.assertEquals("/app/testController/index/param1/?param2=param2", url);

	} catch (Exception ex) {
	    Assertions.fail(ex.getMessage());
	}
    }

    @Test
    public void test_index_navQueryString() {
	try {
	    getCurrentRequest().setRequestURL("http://localhost/app/testController/testQueryString");
	    requestContextParser.parse(getCurrentRequest(), getCurrentResponse());

	    Map<String, Object> params = new HashMap<>();
	    params.put(ActionParser.PARAM_QUERY_STRING, "param1=param1&param2=param+2");
	    params.put("param3", "param3");

	    String url = actionParser.toActionUrl(getCurrentRequest(), "testController", "testQueryString", params, false);
	    Assertions.assertEquals("/app/testController/testQueryString/?param1=param1&param2=param+2&param3=param3", url);

	} catch (Exception ex) {
	    Assertions.fail(ex.getMessage());
	}
    }

    @Test
    public void test_actionNoPathParams() {
	try {
	    getCurrentRequest().setRequestURL("http://localhost/app/testController/actionNoPathParams");
	    requestContextParser.parse(getCurrentRequest(), getCurrentResponse());

	    String url = actionParser.toActionUrl(getCurrentRequest(), "testController", "actionNoPathParams", null, false);
	    Assertions.assertEquals("/app/testController/actionNoPathParams/", url);

	} catch (Exception ex) {
	    Assertions.fail(ex.getMessage());
	}
    }

    @Test
    public void test_actionPathParams() {
	try {
	    getCurrentRequest().setRequestURL("http://localhost/app/testController/actionPathParams/param1/param2");
	    requestContextParser.parse(getCurrentRequest(), getCurrentResponse());

	    Map<String, Object> params = new HashMap<>();
	    params.put("param1", "param1");
	    params.put("param2", "param2");

	    String url = actionParser.toActionUrl(getCurrentRequest(), "testController", "actionPathParams", params, false);
	    Assertions.assertEquals("/app/testController/actionPathParams/param1/param2/", url);

	} catch (Exception ex) {
	    Assertions.fail(ex.getMessage());
	}
    }

    @Test
    public void test_actionPathParams_lang() {
	try {
	    getCurrentRequest().setRequestURL("http://localhost/app/en/testController/actionPathParams/param1/param2");
	    requestContextParser.parse(getCurrentRequest(), getCurrentResponse());

	    Map<String, Object> params = new HashMap<>();
	    params.put("param1", "param1");
	    params.put("param2", "param2");

	    String url = actionParser.toActionUrl(getCurrentRequest(), "testController", "actionPathParams", params, false);
	    Assertions.assertEquals("/app/en/testController/actionPathParams/param1/param2/", url);

	} catch (Exception ex) {
	    Assertions.fail(ex.getMessage());
	}
    }

    @Test
    public void test_actionPathParams_pathParamsMissed() {
	try {
	    getCurrentRequest().setRequestURL("http://localhost/app/testController/actionPathParams/param1/param2");
	    requestContextParser.parse(getCurrentRequest(), getCurrentResponse());

	    Map<String, Object> params = new HashMap<>();
	    params.put("param1", "param1");

	    actionParser.toActionUrl(getCurrentRequest(), "testController", "actionPathParams", params, false);
	    Assertions.fail();

	} catch (Exception ex) {
	    Assertions.assertTrue(ex instanceof IllegalArgumentException);
	}
    }

    @Test
    public void test_actionSubPathParams() {
	try {
	    getCurrentRequest().setRequestURL("http://localhost/app/testController/actionSubPathParams/param1-param2");
	    requestContextParser.parse(getCurrentRequest(), getCurrentResponse());

	    Map<String, Object> params = new HashMap<>();
	    params.put("param1", "param1");
	    params.put("param2", "param2");

	    String url = actionParser.toActionUrl(getCurrentRequest(), "testController", "actionSubPathParams", params, false);
	    Assertions.assertEquals("/app/testController/actionSubPathParams/param1-param2/", url);

	} catch (Exception ex) {
	    Assertions.fail(ex.getMessage());
	}
    }

    @Test
    public void test_actionSubPathParams_pathParamsMissed() {
	try {
	    getCurrentRequest().setRequestURL("http://localhost/app/testController/actionSubPathParams/param1-param2");
	    requestContextParser.parse(getCurrentRequest(), getCurrentResponse());

	    Map<String, Object> params = new HashMap<>();
	    params.put("param1", "param1");

	    actionParser.toActionUrl(getCurrentRequest(), "testController", "actionSubPathParams", params, false);
	    Assertions.fail();

	} catch (Exception ex) {
	    Assertions.assertTrue(ex instanceof IllegalArgumentException);
	}
    }

    @Test
    public void test_actionSubPathParams_withQueryParams() {
	try {
	    getCurrentRequest().setRequestURL("http://localhost/app/testController/actionSubPathParams/param1-param2");
	    requestContextParser.parse(getCurrentRequest(), getCurrentResponse());

	    Map<String, Object> params = new LinkedHashMap<>();
	    params.put("param1", "param1");
	    params.put("param2", "param2");
	    params.put("param3", "param3");
	    params.put("param4", "param4");

	    String url = actionParser.toActionUrl(getCurrentRequest(), "testController", "actionSubPathParams", params, false);
	    Assertions.assertEquals("/app/testController/actionSubPathParams/param1-param2/?param3=param3&param4=param4", url);

	} catch (Exception ex) {
	    Assertions.fail(ex.getMessage());
	}
    }

    @Controller("testController")
    public static class TestController {

	@HttpGet
	@PathParams("/{param1}")
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

	@HttpGet
	public void testQueryString() {
	}
    }
}
