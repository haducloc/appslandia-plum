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

package com.appslandia.plum.mocks;

import java.io.Serializable;

import com.appslandia.common.base.InitializeObject;
import com.appslandia.common.utils.ArrayUtils;
import com.appslandia.common.utils.AssertUtils;
import com.appslandia.common.utils.StringUtils;
import com.appslandia.common.utils.ValueUtils;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class MemUser extends InitializeObject implements Serializable {
	private static final long serialVersionUID = 1L;

	private String userName;
	private String password;
	private String email;
	private String[] roles;

	@Override
	protected void init() throws Exception {
		AssertUtils.assertNotNull(this.userName);
		AssertUtils.assertNotNull(this.password);

		if (this.email == null) {
			this.email = createEmail(this.userName);
		}
		this.roles = ValueUtils.valueOrAlt(this.roles, StringUtils.EMPTY_ARRAY);
	}

	public String getUserName() {
		initialize();
		return this.userName;
	}

	public MemUser setUserName(String userName) {
		assertNotInitialized();
		this.userName = userName;
		return this;
	}

	public String getPassword() {
		initialize();
		return this.password;
	}

	public MemUser setPassword(String password) {
		assertNotInitialized();
		this.password = password;
		return this;
	}

	public String getEmail() {
		initialize();
		return this.email;
	}

	public MemUser setEmail(String email) {
		assertNotInitialized();
		this.email = email;
		return this;
	}

	public String[] getRoles() {
		initialize();
		return ArrayUtils.copy(this.roles);
	}

	public MemUser setRoles(String... roles) {
		assertNotInitialized();
		this.roles = roles;
		return this;
	}

	public MemUser copy() {
		MemUser copy = new MemUser();
		copy.userName = this.userName;
		copy.password = this.password;
		copy.email = this.email;
		copy.roles = (this.roles != null) ? ArrayUtils.copy(this.roles) : null;
		return copy;
	}

	public static String createEmail(String userName) {
		return userName + "@memuser.com";
	}
}
