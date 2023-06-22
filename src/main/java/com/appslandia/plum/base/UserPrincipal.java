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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.appslandia.common.utils.Asserts;

import jakarta.security.enterprise.CallerPrincipal;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public abstract class UserPrincipal extends CallerPrincipal {
    private static final long serialVersionUID = 1L;

    public static final String USER_ID = "uid";
    public static final String USER_UUID = "uud";

    public static final String USER_DNAME = "udn";
    public static final String USER_EMAIL = "uem";

    public static final String USER_NAME = "una";
    public static final String USER_ROLES = "uro";

    final Map<String, Object> attributes;

    public UserPrincipal(String username, Map<String, Object> attributes) {
	super(username);
	this.attributes = ((attributes != null) && !attributes.isEmpty()) ? Collections.unmodifiableMap(new HashMap<>(attributes)) : Collections.emptyMap();
    }

    public Object get(String attributeName) {
	return this.attributes.get(attributeName);
    }

    public Object getRequired(String attributeName) {
	Object obj = this.attributes.get(attributeName);
	return Asserts.notNull(obj);
    }

    public Object getUserId() {
	return get(USER_ID);
    }

    public String getUserDName() {
	String dn = (String) get(USER_DNAME);
	return (dn != null) ? dn : getName();
    }

    // Internal override only

    public String getModule() {
	throw new UnsupportedOperationException();
    }

    // Internal override only

    public boolean isRememberMe() {
	throw new UnsupportedOperationException();
    }

    // Internal override only

    public long getReauthAt() {
	throw new UnsupportedOperationException();
    }
}
