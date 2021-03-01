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

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Test;

import com.appslandia.plum.mocks.MemUserDatabase;
import com.appslandia.plum.mocks.MockServletUtils;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class HttpStatus403BasicTest extends MockTestBase {

	@Override
	protected void initialize() {
		container.register(TestController.class, TestController.class);

		MemUserDatabase memUserDatabase = container.getObject(MemUserDatabase.class);
		memUserDatabase.addUser("user1", "password", new String[] { "user" });
		memUserDatabase.addUser("manager1", "password", new String[] { "manager" });

		AuthorizePolicyProvider authorizePolicyProvider = container.getObject(AuthorizePolicyProvider.class);
		authorizePolicyProvider.addAuthorizePolicy("manager1", new AuthorizePolicy() {

			@Override
			public boolean authorize(UserPrincipal principal) {
				return principal.getName().equalsIgnoreCase("manager1");
			}
		});
	}

	@Test
	public void test_testManager_unAuthenticated() {
		try {
			executeCurrent("GET", "http://localhost/app/testController/testManager");

			Assert.assertEquals(getCurrentResponse().getStatus(), 401);

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_testManager_unAuthorized() {
		try {
			getCurrentRequest().setHeader("Authorization", "Basic " + MockServletUtils.createBasicCredential("user1", "password"));

			executeCurrent("GET", "http://localhost/app/testController/testManager");

			Assert.assertEquals(getCurrentResponse().getStatus(), 403);

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_testManager_authorized() {
		try {
			getCurrentRequest().setHeader("Authorization", "Basic " + MockServletUtils.createBasicCredential("manager1", "password"));

			executeCurrent("GET", "http://localhost/app/testController/testManager");

			Assert.assertEquals(getCurrentResponse().getStatus(), 200);

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_testPolicyManager1_unauthorized() {
		try {
			getCurrentRequest().setHeader("Authorization", "Basic " + MockServletUtils.createBasicCredential("user1", "password"));

			executeCurrent("GET", "http://localhost/app/testController/testPolicyManager1");

			Assert.assertEquals(getCurrentResponse().getStatus(), 403);

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_testPolicyManager1_authorized() {
		try {
			getCurrentRequest().setHeader("Authorization", "Basic " + MockServletUtils.createBasicCredential("manager1", "password"));

			executeCurrent("GET", "http://localhost/app/testController/testPolicyManager1");

			Assert.assertEquals(getCurrentResponse().getStatus(), 200);

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_testForbiddenException_unAuthorized() {
		try {
			getCurrentRequest().setHeader("Authorization", "Basic " + MockServletUtils.createBasicCredential("user1", "password"));

			executeCurrent("GET", "http://localhost/app/testController/testForbiddenException");

			Assert.assertEquals(getCurrentResponse().getStatus(), 403);

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Controller("testController")
	public static class TestController {

		@HttpGet
		@Authorize(roles = { "manager" })
		public void testManager() throws Exception {
		}

		@HttpGet
		@Authorize(policies = { "manager1" })
		public void testPolicyManager1() throws Exception {
		}

		@HttpGet
		@Authorize
		public void testForbiddenException(HttpServletRequest request) throws Exception {
			if (!request.isUserInRole("manager")) {
				throw new ForbiddenException("manager is required");
			}
		}
	}
}
