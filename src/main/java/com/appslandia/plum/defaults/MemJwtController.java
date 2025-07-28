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

import java.util.concurrent.TimeUnit;

import com.appslandia.common.base.Out;
import com.appslandia.common.base.ToStringBuilder;
import com.appslandia.common.jose.JwtSigner;
import com.appslandia.common.jose.JwtToken;
import com.appslandia.plum.base.Authorize;
import com.appslandia.plum.base.Controller;
import com.appslandia.plum.base.HttpGet;
import com.appslandia.plum.base.IdentityHandler;
import com.appslandia.plum.base.Module;
import com.appslandia.plum.base.RequestWrapper;
import com.appslandia.plum.base.UserPrincipal;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
@Controller
@Module(MemModules.MEM_JWT)
public class MemJwtController {

  @Inject
  @MemVersion
  protected JwtSigner jwtSigner;

  @Inject
  protected IdentityHandler identityHandler;

  @HttpGet
  public String login(RequestWrapper request, HttpServletResponse response, @NotNull String username,
      @NotNull String password) throws Exception {

    var invalidCode = new Out<String>();
    var rolesPrincipal = this.identityHandler.validateCredentials(request.getModule(), username, password, invalidCode);

    if (rolesPrincipal == null) {
      return invalidCode.get();
    }

    var header = this.jwtSigner.newHeader();
    var payload = this.jwtSigner.newPayload().setExp(1, TimeUnit.DAYS).setIatNow();

    payload.set(UserPrincipal.ATTRIBUTE_USER_ID, rolesPrincipal.getPrincipal().getUserId());
    payload.set(UserPrincipal.ATTRIBUTE_USER_NAME, rolesPrincipal.getPrincipal().getName());
    payload.set(UserPrincipal.ATTRIBUTE_NAME, rolesPrincipal.getPrincipal().getName());
    payload.set(UserPrincipal.ATTRIBUTE_ROLES, rolesPrincipal.getRoles());

    var jwt = this.jwtSigner.sign(new JwtToken(header, payload));
    return jwt;
  }

  @HttpGet
  public String index(RequestWrapper request, HttpServletResponse response) throws Exception {
    return "MemJwtController.index: Principal=" + new ToStringBuilder().toString(request.getUserPrincipal());
  }

  @HttpGet
  @Authorize
  public String user(RequestWrapper request, HttpServletResponse response) throws Exception {
    return "MemJwtController.user: Principal=" + new ToStringBuilder().toString(request.getUserPrincipal());
  }

  @HttpGet
  @Authorize(roles = { "manager" })
  public String manager(RequestWrapper request, HttpServletResponse response) throws Exception {
    return "MemJwtController.manager: Principal=" + new ToStringBuilder().toString(request.getUserPrincipal());
  }

  @HttpGet
  @Authorize(roles = { "admin" })
  public String admin(RequestWrapper request, HttpServletResponse response) throws Exception {
    return "MemJwtController.admin: Principal=" + new ToStringBuilder().toString(request.getUserPrincipal());
  }
}
