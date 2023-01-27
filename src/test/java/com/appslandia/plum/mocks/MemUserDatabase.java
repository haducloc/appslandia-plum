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

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.appslandia.common.base.CaseInsensitiveMap;
import com.appslandia.common.base.InitializeObject;
import com.appslandia.common.crypto.PasswordDigester;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class MemUserDatabase extends InitializeObject {

    private Map<String, MemUser> userMap = new CaseInsensitiveMap<>(new ConcurrentHashMap<>());
    final PasswordDigester passwordDigester = new PasswordDigester();

    @Override
    protected void init() throws Exception {
	this.userMap = Collections.unmodifiableMap(this.userMap);
    }

    public Map<String, MemUser> getUserMap() {
	this.assertNotInitialized();
	return this.userMap;
    }

    public MemUser getUser(String username) {
	this.assertNotInitialized();
	return this.userMap.get(username);
    }

    public boolean verifyPassword(String password, String digested) {
	this.assertNotInitialized();
	return this.passwordDigester.verify(password, digested);
    }

    public void addUser(String username, String password) {
	addUser(username, password, null);
    }

    public void addUser(String username, String password, String roles) {
	this.assertNotInitialized();

	MemUser user = new MemUser();
	user.setUsername(username);
	user.setPassword(this.passwordDigester.digest(password));
	user.setRoles(roles);
	this.userMap.put(username, user);
    }

    public void addUser(MemUser user) {
	this.assertNotInitialized();

	MemUser copy = user.copy();
	copy.setPassword(this.passwordDigester.digest(copy.getPassword()));
	this.userMap.put(copy.getUsername(), copy);
    }
}
