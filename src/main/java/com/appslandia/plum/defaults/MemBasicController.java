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

import com.appslandia.common.base.ToStringBuilder;
import com.appslandia.plum.base.ActionResult;
import com.appslandia.plum.base.Authorize;
import com.appslandia.plum.base.Controller;
import com.appslandia.plum.base.HttpGet;
import com.appslandia.plum.base.Module;
import com.appslandia.plum.base.RequestWrapper;
import com.appslandia.plum.results.RedirectResult;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
@Controller
@Module(MemModules.MEM_BASIC)
public class MemBasicController {

  @HttpGet
  public ActionResult invalidateSession(RequestWrapper request, HttpServletResponse response) throws Exception {
    var session = request.getSession(false);
    if (session != null) {
      session.invalidate();
    }
    return new RedirectResult("Index");
  }

  @HttpGet
  public String index(RequestWrapper request, HttpServletResponse response) throws Exception {
    return "MemBasicController.index: Principal=" + new ToStringBuilder().toString(request.getUserPrincipal());
  }

  @HttpGet
  public String session(RequestWrapper request, HttpServletResponse response) throws Exception {
    return "MemBasicController.session: Principal=" + new ToStringBuilder().toString(request.getUserPrincipal())
        + ", Session=" + request.getSession().getId();
  }

  @HttpGet
  public String authorization(RequestWrapper request, HttpServletResponse response) throws Exception {
    return "MemBasicController.authorization: Authorization=" + request.getHeader("Authorization");
  }

  @HttpGet
  @Authorize
  public String user(RequestWrapper request, HttpServletResponse response) throws Exception {
    return "MemBasicController.user: Principal=" + new ToStringBuilder().toString(request.getUserPrincipal());
  }

  @HttpGet
  @Authorize(roles = { "manager" })
  public String manager(RequestWrapper request, HttpServletResponse response) throws Exception {
    return "MemBasicController.manager: Principal=" + new ToStringBuilder().toString(request.getUserPrincipal());
  }

  @HttpGet
  @Authorize(roles = { "admin" })
  public String admin(RequestWrapper request, HttpServletResponse response) throws Exception {
    return "MemBasicController.admin: Principal=" + new ToStringBuilder().toString(request.getUserPrincipal());
  }
}
