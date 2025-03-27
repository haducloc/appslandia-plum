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

import com.appslandia.common.base.Out;
import com.appslandia.common.base.ToStringBuilder;
import com.appslandia.plum.base.ActionResult;
import com.appslandia.plum.base.AuthContext;
import com.appslandia.plum.base.Authorize;
import com.appslandia.plum.base.Controller;
import com.appslandia.plum.base.HttpGet;
import com.appslandia.plum.base.Module;
import com.appslandia.plum.base.RequestWrapper;
import com.appslandia.plum.results.RedirectResult;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotNull;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
@Controller
@Module(MemModules.MEM_FORM)
public class MemFormController {

  @Inject
  protected AuthContext authContext;

  @HttpGet
  public String login(RequestWrapper request, HttpServletResponse response, @NotNull String username,
      @NotNull String password, boolean rememberMe) throws Exception {

    MemUserCredential modCredential = new MemUserCredential(username, password);
    Out<String> invalidCode = new Out<>();

    if (!this.authContext.authenticate(request, response, modCredential, rememberMe, invalidCode)) {
      return invalidCode.get();
    }
    return request.getRemoteUser();
  }

  @HttpGet
  public String index(RequestWrapper request, HttpServletResponse response) throws Exception {
    return "MemFormController.index: Principal=" + new ToStringBuilder().toString(request.getUserPrincipal());
  }

  @HttpGet
  @Authorize
  public String user(RequestWrapper request, HttpServletResponse response) throws Exception {
    return "MemFormController.user: Principal=" + new ToStringBuilder().toString(request.getUserPrincipal());
  }

  @HttpGet
  @Authorize(roles = { "manager" })
  public String manager(RequestWrapper request, HttpServletResponse response) throws Exception {
    return "MemFormController.manager: Principal=" + new ToStringBuilder().toString(request.getUserPrincipal());
  }

  @HttpGet
  @Authorize(roles = { "admin" })
  public String admin(RequestWrapper request, HttpServletResponse response) throws Exception {
    return "MemFormController.admin: Principal=" + new ToStringBuilder().toString(request.getUserPrincipal());
  }

  @HttpGet
  public ActionResult logout(RequestWrapper request, HttpServletResponse response) throws Exception {
    if (request.getUserPrincipal() != null) {
      request.logout();

      HttpSession session = request.getSession(false);
      if (session != null) {
        session.invalidate();
      }
    }
    return new RedirectResult("Index");
  }
}
