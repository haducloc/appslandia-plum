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

import com.appslandia.common.jwt.JwtHeader;
import com.appslandia.common.jwt.JwtPayload;
import com.appslandia.common.jwt.JwtProcessor;
import com.appslandia.common.jwt.JwtToken;
import com.appslandia.plum.mocks.MemUserDatabase;
import com.appslandia.plum.mocks.MemVersionLiteral;
import com.appslandia.plum.mocks.MockHttpServletRequest;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class HttpStatus401JwtTest extends MockTestBase {

	@Override
	protected void initialize() {
		container.register(TestController.class, TestController.class);
		container.getAppConfig().set(AppConfig.CONFIG_MODULE_NAME, "bearer");

		MemUserDatabase memUserDatabase = container.getObject(MemUserDatabase.class);
		memUserDatabase.addUser("user1", "password");
	}

	void initAccessToken(MockHttpServletRequest request, JwtPayload payload) {
		JwtProcessor jwtProcessor = container.getObject(JwtProcessor.class, MemVersionLiteral.IMPL);
		JwtHeader header = jwtProcessor.newHeader();
		request.setHeader("Authorization", "Bearer " + jwtProcessor.toJwt(new JwtToken(header, payload)));
	}

	@Test
	public void test_testAction_unAuthenticated() {
		try {
			executeCurrent("GET", "http://localhost/app/testController/testAction");

			Assert.assertEquals(getCurrentResponse().getStatus(), 401);

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_testAction_authenticated() {
		try {
			initAccessToken(getCurrentRequest(), new JwtPayload().set(JwtPrincipal.KEY_USER_NAME, "user1").setArray(JwtPrincipal.KEY_USER_ROLES, "user"));

			executeCurrent("GET", "http://localhost/app/testController/testAction");

			Assert.assertEquals(getCurrentResponse().getStatus(), 200);

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Controller("testController")
	public static class TestController {

		@HttpGet
		@Authorize
		public void testAction() throws Exception {
		}
	}
}
