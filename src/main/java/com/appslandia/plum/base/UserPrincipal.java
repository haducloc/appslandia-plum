// The MIT License (MIT)
// Copyright © 2015 Loc Ha

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
import com.appslandia.common.utils.CollectionUtils;

import jakarta.security.enterprise.CallerPrincipal;

/**
 *
 * @author Loc Ha
 *
 */
public abstract class UserPrincipal extends CallerPrincipal {
  private static final long serialVersionUID = 1L;

  // https://www.iana.org/assignments/jwt/jwt.xhtml#claims

  public static final String ATTRIBUTE_USER_ID = "user_id";
  public static final String ATTRIBUTE_USER_UUID = "user_uuid";

  public static final String ATTRIBUTE_DISPLAY_NAME = "display_name";
  public static final String ATTRIBUTE_EMAIL = "email";

  public static final String ATTRIBUTE_USER_NAME = "user_name";
  public static final String ATTRIBUTE_ROLES = "roles";

  // Full name
  public static final String ATTRIBUTE_NAME = "name";
  public static final String ATTRIBUTE_PHONE_NUMBER = "phone_number";

  final Map<String, Object> attributes;

  public UserPrincipal(String username, Map<String, Object> attributes) {
    super(username);
    this.attributes = CollectionUtils.hasEntries(attributes) ? Collections.unmodifiableMap(new HashMap<>(attributes))
        : Collections.emptyMap();
  }

  public Object get(String attributeName) {
    return this.attributes.get(attributeName);
  }

  public Object getReq(String attributeName) {
    var obj = this.attributes.get(attributeName);
    return Asserts.notNull(obj);
  }

  public Object getUserId() {
    return get(ATTRIBUTE_USER_ID);
  }

  public String getDisplayName() {
    var dn = (String) get(ATTRIBUTE_DISPLAY_NAME);
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
