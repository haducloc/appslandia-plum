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

package com.appslandia.plum.defaults;

import java.util.HashMap;
import java.util.Map;

import com.appslandia.plum.base.UserPrincipal;

/**
 *
 * @author Loc Ha
 *
 */
public class MemPrincipal extends UserPrincipal {
  private static final long serialVersionUID = 1L;

  public MemPrincipal(MemUser user) {
    super(user.getUsername(), createAttributes(user));
  }

  private static Map<String, Object> createAttributes(MemUser user) {
    Map<String, Object> map = new HashMap<>();
    map.put(ATTRIBUTE_USER_ID, user.getUserId());
    map.put(ATTRIBUTE_USER_NAME, user.getUsername());
    map.put(ATTRIBUTE_NAME, user.getUsername());
    map.put(ATTRIBUTE_ROLES, user.getUserRoles());
    return map;
  }
}
