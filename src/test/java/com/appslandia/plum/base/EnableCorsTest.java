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

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class EnableCorsTest extends MockTestBase {

	CorsPolicyProvider corsPolicyProvider;

	@Override
	protected void initialize() {
		container.register(TestController.class, TestController.class);
		corsPolicyProvider = container.getObject(CorsPolicyProvider.class);
	}

	@Test
	public void test_testAction() {
		try {
			getCurrentRequest().setHeader("Origin", "http://myDomain.com");
			getCurrentRequest().setHeader("Access-Control-Request-Method", "POST");

			executeCurrent("OPTIONS", "http://localhost/app/testController/testAction");

			Assert.assertEquals(getCurrentResponse().getStatus(), 405);
			Assert.assertNull(getCurrentResponse().getHeader("Allow"));

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_testAction_sameOrigin() {
		try {
			getCurrentRequest().setHeader("Origin", "http://localhost");

			executeCurrent("POST", "http://localhost/app/testController/testAction");

			Assert.assertEquals(getCurrentResponse().getStatus(), 200);
			Assert.assertNull(getCurrentResponse().getHeader(CorsPolicyHandler.HEADER_AC_ALLOW_ORIGIN));

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_testAction_crossOrigin() {
		try {
			getCurrentRequest().setHeader("Origin", "http://myDomain.com");

			executeCurrent("POST", "http://localhost/app/testController/testAction");

			Assert.assertEquals(getCurrentResponse().getStatus(), 403);
			Assert.assertNull(getCurrentResponse().getHeader(CorsPolicyHandler.HEADER_AC_ALLOW_ORIGIN));

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_enableCorsAction_allowed_anyOrigin() {
		try {
			corsPolicyProvider.addCorsPolicy(new CorsPolicy().setName("testCors").anyOrigin());

			getCurrentRequest().setHeader("Origin", "http://myDomain.com");
			getCurrentRequest().setHeader("Access-Control-Request-Method", "POST");

			executeCurrent("OPTIONS", "http://localhost/app/testController/enableCorsAction");

			Assert.assertEquals(getCurrentResponse().getStatus(), 200);
			Assert.assertEquals(getCurrentResponse().getHeader(CorsPolicyHandler.HEADER_AC_ALLOW_ORIGIN), "*");

			String allows = getCurrentResponse().getHeader(CorsPolicyHandler.HEADER_AC_ALLOW_METHODS);
			Assert.assertNotNull(allows);

			Assert.assertTrue(allows.contains("GET"));
			Assert.assertTrue(allows.contains("HEAD"));
			Assert.assertTrue(allows.contains("POST"));
			Assert.assertTrue(allows.contains("OPTIONS"));

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_enableCorsAction_allowed_origin() {
		try {
			corsPolicyProvider.addCorsPolicy(new CorsPolicy().setName("testCors").setAllowOrigins("http://myDomain\\.com"));

			getCurrentRequest().setHeader("Origin", "http://myDomain.com");
			getCurrentRequest().setHeader("Access-Control-Request-Method", "POST");

			executeCurrent("OPTIONS", "http://localhost/app/testController/enableCorsAction");

			Assert.assertEquals(getCurrentResponse().getStatus(), 200);
			Assert.assertEquals(getCurrentResponse().getHeader(CorsPolicyHandler.HEADER_AC_ALLOW_ORIGIN), "http://myDomain.com");

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_enableCorsAction_notAllowed_origin() {
		try {
			corsPolicyProvider.addCorsPolicy(new CorsPolicy().setName("testCors").setAllowOrigins("http://localhost:(\\d)+"));

			getCurrentRequest().setHeader("Origin", "http://myDomain.com");
			getCurrentRequest().setHeader("Access-Control-Request-Method", "POST");

			executeCurrent("OPTIONS", "http://localhost/app/testController/enableCorsAction");

			Assert.assertEquals(getCurrentResponse().getStatus(), 403);
			Assert.assertNull(getCurrentResponse().getHeader(CorsPolicyHandler.HEADER_AC_ALLOW_ORIGIN));

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_enableCorsAction_notAllowed_requestMethod() {
		try {
			corsPolicyProvider.addCorsPolicy(new CorsPolicy().setName("testCors").anyOrigin().setAllowHeaders("X-Header"));

			getCurrentRequest().setHeader("Origin", "http://myDomain.com");
			getCurrentRequest().setHeader("Access-Control-Request-Method", "PUT");

			executeCurrent("OPTIONS", "http://localhost/app/testController/enableCorsAction");

			Assert.assertEquals(getCurrentResponse().getStatus(), 403);
			Assert.assertNull(getCurrentResponse().getHeader(CorsPolicyHandler.HEADER_AC_ALLOW_ORIGIN));

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_enableCorsAction_allowed_allowHeaders() {
		try {
			corsPolicyProvider.addCorsPolicy(new CorsPolicy().setName("testCors").anyOrigin().setAllowHeaders("X-Header"));

			getCurrentRequest().setHeader("Origin", "http://myDomain.com");
			getCurrentRequest().setHeader("Access-Control-Request-Method", "POST");
			getCurrentRequest().setHeader("Access-Control-Request-Headers", "x-header");

			executeCurrent("OPTIONS", "http://localhost/app/testController/enableCorsAction");

			Assert.assertEquals(getCurrentResponse().getStatus(), 200);
			Assert.assertEquals(getCurrentResponse().getHeader(CorsPolicyHandler.HEADER_AC_ALLOW_ORIGIN), "*");
			Assert.assertEquals(getCurrentResponse().getHeader(CorsPolicyHandler.HEADER_AC_ALLOW_HEADERS), "x-header");

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_enableCorsAction_notAllowed_requestHeaders() {
		try {
			corsPolicyProvider.addCorsPolicy(new CorsPolicy().setName("testCors").anyOrigin().setAllowHeaders("X-Header"));

			getCurrentRequest().setHeader("Origin", "http://myDomain.com");
			getCurrentRequest().setHeader("Access-Control-Request-Method", "POST");
			getCurrentRequest().setHeader("Access-Control-Request-Headers", "X-Header1");

			executeCurrent("OPTIONS", "http://localhost/app/testController/enableCorsAction");

			Assert.assertEquals(getCurrentResponse().getStatus(), 403);
			Assert.assertNull(getCurrentResponse().getHeader(CorsPolicyHandler.HEADER_AC_ALLOW_ORIGIN));

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_enableCorsAction_sameOrigin() {
		try {
			getCurrentRequest().setHeader("Origin", "http://localhost");

			executeCurrent("POST", "http://localhost/app/testController/enableCorsAction");

			Assert.assertEquals(getCurrentResponse().getStatus(), 200);
			Assert.assertNull(getCurrentResponse().getHeader(CorsPolicyHandler.HEADER_AC_ALLOW_ORIGIN));

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_enableCorsAction_crossOrigin() {
		try {
			corsPolicyProvider.addCorsPolicy(new CorsPolicy().setName("testCors").anyOrigin().setAllowHeaders("X-Header"));

			getCurrentRequest().setHeader("Origin", "http://myDomain.com");

			executeCurrent("POST", "http://localhost/app/testController/enableCorsAction");

			Assert.assertEquals(getCurrentResponse().getStatus(), 200);
			Assert.assertEquals(getCurrentResponse().getHeader(CorsPolicyHandler.HEADER_AC_ALLOW_ORIGIN), "*");

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_enableCorsAction_vary() {
		try {
			corsPolicyProvider.addCorsPolicy(new CorsPolicy().setName("testCors").setAllowOrigins("http://myDomain\\.com"));

			getCurrentRequest().setHeader("Origin", "http://myDomain.com");

			executeCurrent("POST", "http://localhost/app/testController/enableCorsAction");

			Assert.assertEquals(getCurrentResponse().getStatus(), 200);
			Assert.assertNotNull(getCurrentResponse().getHeader(CorsPolicyHandler.HEADER_VARY));

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_enableCorsAction_notVary() {
		try {
			corsPolicyProvider.addCorsPolicy(new CorsPolicy().setName("testCors").anyOrigin());
			getCurrentRequest().setHeader("Origin", "http://myDomain.com");

			executeCurrent("POST", "http://localhost/app/testController/enableCorsAction");

			Assert.assertEquals(getCurrentResponse().getStatus(), 200);
			Assert.assertNull(getCurrentResponse().getHeader(CorsPolicyHandler.HEADER_VARY));

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Controller("testController")
	public static class TestController {

		@HttpGetPost
		public void testAction() throws Exception {
		}

		@HttpGetPost
		@EnableCors("testCors")
		public void enableCorsAction() throws Exception {
		}
	}
}
