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

import com.appslandia.common.base.NotImplementedException;
import com.appslandia.common.base.Out;
import com.appslandia.common.utils.STR;
import com.appslandia.plum.base.IdentityHandler;
import com.appslandia.plum.base.InvalidAuth;
import com.appslandia.plum.base.RolesPrincipal;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class MemUserIdentityHandler implements IdentityHandler {

  @Inject
  protected MemUserService userService;

  @Override
  public RolesPrincipal validateIdentity(String module, String identity, Out<String> invalidCode) {
    if (MemModules.isMemModule(module)) {

      var user = userService.getByUsername(identity);
      if (user == null) {
        invalidCode.value = InvalidAuth.CREDENTIAL_INVALID.getCode();
        return null;
      }
      return new RolesPrincipal(new MemPrincipal(user), user.getUserRoles());
    }
    throw new NotImplementedException(STR.fmt("The validateIdentity() for the module '{}' is unhandled.", module));
  }

  @Override
  public RolesPrincipal validateCredentials(String module, String username, String password, Out<String> invalidCode) {
    if (MemModules.isMemModule(module)) {

      var user = userService.getByUsername(username);
      if ((user == null) || !userService.verifyPassword(password, user.getPassword())) {
        invalidCode.value = InvalidAuth.CREDENTIAL_INVALID.getCode();
        return null;
      }
      return new RolesPrincipal(new MemPrincipal(user), user.getUserRoles());
    }
    throw new NotImplementedException(STR.fmt("The validateCredentials() for the module '{}' is unhandled.", module));
  }
}
