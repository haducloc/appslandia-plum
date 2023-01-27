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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.appslandia.plum.mocks.MockHttpServletRequest;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class CsrfManagerTest extends MockTestBase {

    SimpleCsrfManager csrfManager;
    RequestContextParser requestContextParser;

    @Override
    protected void initialize() {
	container.register(TestController.class, TestController.class);
	csrfManager = container.getObject(CsrfManager.class);
	requestContextParser = container.getObject(RequestContextParser.class);
    }

    @Test
    public void test_initCsrf() {
	try {
	    getCurrentRequest().setRequestURL("http://localhost/app/testController/testCsrf");
	    requestContextParser.parse(getCurrentRequest(), getCurrentResponse());

	    csrfManager.initCsrf(getCurrentRequest());
	    String csrfId = (String) getCurrentRequest().getAttribute(SimpleCsrfManager.REQUEST_ATTRIBUTE_CSRF_DATA);
	    Assertions.assertNotNull(csrfId);

	} catch (Exception ex) {
	    Assertions.fail(ex.getMessage());
	}
    }

    @Test
    public void test_verifyCsrf() {
	try {
	    MockHttpServletRequest request = container.createRequest("GET", "http://localhost/app/testController/testCsrf");
	    requestContextParser.parse(request, container.createResponse());

	    csrfManager.initCsrf(request);
	    String csrfId = (String) request.getAttribute(SimpleCsrfManager.REQUEST_ATTRIBUTE_CSRF_DATA);

	    getCurrentRequest().setMethod("POST").setRequestURL("http://localhost/app/testController/testCsrf");
	    getCurrentRequest().setSession(request.getSession());
	    getCurrentRequest().addParameter(SimpleCsrfManager.PARAM_CSRF_ID, csrfId);
	    requestContextParser.parse(getCurrentRequest(), getCurrentResponse());

	    boolean valid = csrfManager.verifyCsrf(getCurrentRequest(), true);
	    Assertions.assertTrue(valid);

	    // Removed
	    valid = csrfManager.verifyCsrf(getCurrentRequest(), true);
	    Assertions.assertFalse(valid);

	} catch (Exception ex) {
	    Assertions.fail(ex.getMessage());
	}
    }

    @Test
    public void test_verifyCsrf_keep() {
	try {
	    MockHttpServletRequest request = container.createRequest("GET", "http://localhost/app/testController/testCsrf");
	    requestContextParser.parse(request, container.createResponse());

	    csrfManager.initCsrf(request);
	    String csrfId = (String) request.getAttribute(SimpleCsrfManager.REQUEST_ATTRIBUTE_CSRF_DATA);

	    getCurrentRequest().setMethod("POST").setRequestURL("http://localhost/app/testController/testCsrf");
	    getCurrentRequest().setSession(request.getSession());
	    getCurrentRequest().addParameter(SimpleCsrfManager.PARAM_CSRF_ID, csrfId);
	    requestContextParser.parse(getCurrentRequest(), getCurrentResponse());

	    boolean valid = csrfManager.verifyCsrf(getCurrentRequest(), false);
	    Assertions.assertTrue(valid);

	    valid = csrfManager.verifyCsrf(getCurrentRequest(), true);
	    Assertions.assertTrue(valid);

	} catch (Exception ex) {
	    Assertions.fail(ex.getMessage());
	}
    }

    @Test
    public void test_verifyCsrf_invalid() {
	try {
	    MockHttpServletRequest request = container.createRequest("GET", "http://localhost/app/testController/testCsrf");
	    requestContextParser.parse(request, container.createResponse());
	    csrfManager.initCsrf(request);

	    getCurrentRequest().setMethod("POST").setRequestURL("http://localhost/app/testController/testCsrf");
	    getCurrentRequest().setSession(request.getSession());
	    getCurrentRequest().addParameter(SimpleCsrfManager.PARAM_CSRF_ID, "invalid");
	    requestContextParser.parse(getCurrentRequest(), getCurrentResponse());

	    boolean valid = csrfManager.verifyCsrf(getCurrentRequest(), true);
	    Assertions.assertFalse(valid);

	} catch (Exception ex) {
	    Assertions.fail(ex.getMessage());
	}
    }

    @Test
    public void test_verifyCsrf_noSession() {
	try {
	    MockHttpServletRequest request = container.createRequest("GET", "http://localhost/app/testController/testCsrf");
	    requestContextParser.parse(request, container.createResponse());

	    csrfManager.initCsrf(request);
	    String csrfId = (String) request.getAttribute(SimpleCsrfManager.REQUEST_ATTRIBUTE_CSRF_DATA);

	    getCurrentRequest().setMethod("POST").setRequestURL("http://localhost/app/testController/testCsrf");
	    // getCurrentRequest().setSession(request.getSession());
	    getCurrentRequest().addParameter(SimpleCsrfManager.PARAM_CSRF_ID, csrfId);
	    requestContextParser.parse(getCurrentRequest(), getCurrentResponse());

	    boolean valid = csrfManager.verifyCsrf(getCurrentRequest(), true);
	    Assertions.assertFalse(valid);

	} catch (Exception ex) {
	    Assertions.fail(ex.getMessage());
	}
    }

    @Controller("testController")
    public static class TestController {

	@HttpGetPost
	@EnableCsrf
	public void testCsrf() throws Exception {
	}
    }
}
