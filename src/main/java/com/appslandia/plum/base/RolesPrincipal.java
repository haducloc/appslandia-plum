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

import java.util.Set;

import com.appslandia.plum.utils.SecurityUtils;

/**
 *
 * @author Loc Ha
 *
 */
public class RolesPrincipal {

  final UserPrincipal principal;
  final String roles;

  public RolesPrincipal(UserPrincipal principal) {
    this(principal, (String) null);
  }

  public RolesPrincipal(UserPrincipal principal, Set<String> userRoles) {
    this(principal, SecurityUtils.toUserRoles(userRoles));
  }

  public RolesPrincipal(UserPrincipal principal, String userRoles) {
    this.principal = principal;
    this.roles = userRoles;
  }

  public UserPrincipal getPrincipal() {
    return this.principal;
  }

  public String getRoles() {
    return this.roles;
  }
}
