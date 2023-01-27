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

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class ContentLangTest extends MockTestBase {

    @Override
    protected void initialize() {
	container.register(TestController.class, TestController.class);

	HeaderPolicyProvider headerPolicyProvider = container.getObject(HeaderPolicyProvider.class);
	headerPolicyProvider.addHeaderPolicy("testLangPolicy", (request, response, context) -> response.setHeader("Content-Language", "ja, en"));
    }

    @Test
    public void test_testNoLangPolicy() {
	try {
	    executeCurrent("GET", "http://localhost/app/testController/testNoLangPolicy");

	    Assertions.assertEquals(200, getCurrentResponse().getStatus());

	    String contentLang = getCurrentResponse().getHeader("Content-Language");
	    Assertions.assertNull(contentLang);

	} catch (Exception ex) {
	    Assertions.fail(ex.getMessage());
	}
    }

    @Test
    public void test_testLangPolicy() {
	try {
	    executeCurrent("GET", "http://localhost/app/testController/testLangPolicy");

	    Assertions.assertEquals(200, getCurrentResponse().getStatus());
	    String contentLang = getCurrentResponse().getHeader("Content-Language");
	    Assertions.assertEquals("ja, en", contentLang);

	} catch (Exception ex) {
	    Assertions.fail(ex.getMessage());
	}
    }

    @Test
    public void test_testDefaultLangPolicy() {
	try {
	    executeCurrent("GET", "http://localhost/app/testController/testDefaultLangPolicy");

	    Assertions.assertEquals(200, getCurrentResponse().getStatus());
	    String contentLang = getCurrentResponse().getHeader("Content-Language");
	    Assertions.assertEquals("en, vi", contentLang);

	} catch (Exception ex) {
	    Assertions.fail(ex.getMessage());
	}
    }

    @Controller("testController")
    public static class TestController {

	@HttpGet
	public void testNoLangPolicy() throws Exception {
	}

	@HttpGet
	@ContentLang("testLangPolicy")
	public void testLangPolicy() throws Exception {
	}

	@HttpGet
	@ContentLang
	public void testDefaultLangPolicy() throws Exception {
	}
    }
}
