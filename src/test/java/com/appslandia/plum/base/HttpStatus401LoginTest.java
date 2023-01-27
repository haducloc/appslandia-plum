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

import com.appslandia.common.base.Out;
import com.appslandia.common.utils.AssertUtils;
import com.appslandia.plum.mocks.MemUserDatabase;
import com.appslandia.plum.mocks.MemUserPasswordCredential;

import jakarta.inject.Inject;
import jakarta.security.auth.message.AuthException;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class HttpStatus401LoginTest extends MockTestBase {

    @Override
    protected void initialize() {
	container.register(TestController.class, TestController.class);
	container.getAppConfig().set(AppConfig.CONFIG_MODULE_NAME, "form");
	container.getAppConfig().set(AppConfig.CONFIG_ENABLE_SESSION, "true");

	MemUserDatabase memUserDatabase = container.getObject(MemUserDatabase.class);
	memUserDatabase.addUser("user1", "password");
    }

    @Test
    public void test_testAction_unAuthenticated() {
	try {
	    executeCurrent("GET", "http://localhost/app/testController/testAction");

	    Assertions.assertEquals(302, getCurrentResponse().getStatus());
	    String location = getCurrentResponse().getHeader("Location");
	    AssertUtils.assertNotNull(location);

	} catch (Exception ex) {
	    Assertions.fail(ex.getMessage());
	}
    }

    @Test
    public void test_loginAction() {
	try {
	    executeCurrent("POST", "http://localhost/app/testController/loginAction");

	    AssertUtils.assertNotNull(getCurrentRequest().getUserPrincipal());
	    Assertions.assertEquals(200, getCurrentResponse().getStatus());

	} catch (Exception ex) {
	    Assertions.fail(ex.getMessage());
	}
    }

    @Controller("testController")
    public static class TestController {

	@Inject
	protected AuthContext authContext;

	@HttpGet
	@Authorize
	public void testAction() throws Exception {
	}

	@HttpGetPost
	@FormLogin
	public void loginAction(RequestAccessor request, HttpServletResponse response) throws Exception {
	    if (request.isGetOrHead()) {
		return;
	    }

	    Out<String> invalidCode = new Out<>();
	    MemUserPasswordCredential credential = new MemUserPasswordCredential("user1", "password");
	    boolean isValid = this.authContext.authenticate(request, response, credential, false, invalidCode);

	    if (!isValid) {
		throw new AuthException(invalidCode.val());
	    }
	}
    }
}
