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
import com.appslandia.common.utils.Asserts;
import com.appslandia.plum.base.ActionResult;
import com.appslandia.plum.base.AuthContext;
import com.appslandia.plum.base.AuthParameters;
import com.appslandia.plum.base.Authorize;
import com.appslandia.plum.base.Controller;
import com.appslandia.plum.base.ExceptionHandler;
import com.appslandia.plum.base.FormLogin;
import com.appslandia.plum.base.HttpGet;
import com.appslandia.plum.base.InvalidAuth;
import com.appslandia.plum.base.Module;
import com.appslandia.plum.base.RequestWrapper;
import com.appslandia.plum.results.ContentResult;
import com.appslandia.plum.results.RedirectResult;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.CredentialValidationResult.Status;
import jakarta.servlet.http.HttpServletResponse;
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

  @Inject
  protected ExceptionHandler exceptionHandler;

  @HttpGet
  @FormLogin
  public ActionResult login(RequestWrapper request, HttpServletResponse response, @NotNull String username,
      @NotNull String password, boolean rememberMe) throws Exception {

    // If the request has a principal but its module does not match the current module,
    // redirect the request to the home page.

    if (request.getUserPrincipal() != null) {
      if (!request.getModule().equals(request.getUserPrincipal().getModule())) {
        return RedirectResult.INDEX;
      }
    }

    if (request.getUserPrincipal() != null) {
      username = request.getUserPrincipal().getName();
      rememberMe = request.getUserPrincipal().isRememberMe();
    }

    try {
      var formCredential = new UsernamePasswordCredential(username, password);
      CredentialValidationResult authResult = null;
      var reauthentication = false;

      if (request.getUserPrincipal() != null) {
        authResult = this.authContext.validate(formCredential, request.getModule());
        Asserts.isTrue(authResult.getStatus() != Status.NOT_VALIDATED);

        if (authResult.getStatus() == Status.VALID) {
          reauthentication = request.getBoolParam(ServletUtils.PARAM_REAUTHENTICATION);
          request.logout();
        }
      }

      // Authenticate
      if (authResult == null || authResult.getStatus() == Status.VALID) {

        var invalidCode = new Out<String>();
        var authParams = new AuthParameters().module(request.getModule()).credential(formCredential)
            .rememberMe(rememberMe).reauthentication(reauthentication);

        var authSuccess = this.authContext.authenticate(request, response, authParams, invalidCode);
        if (authSuccess) {
          return new ContentResult(new ToStringBuilder().toString(request.getUserPrincipal()));
        } else {
          return new ContentResult(invalidCode.get());
        }
      } else {
        return new ContentResult(InvalidAuth.getInvalidCode(authResult));
      }

    } catch (Exception ex) {
      var errorMsg = this.exceptionHandler.getProblem(request, ex).getTitle();
      return new ContentResult(errorMsg);
    }
  }

  @HttpGet
  public ActionResult logout(RequestWrapper request, HttpServletResponse response) throws Exception {
    if (request.getUserPrincipal() != null) {
      request.logout();

      var session = request.getSession(false);
      if (session != null) {
        session.invalidate();
      }
    }
    return new RedirectResult("Index");
  }

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
    return "MemFormController.index: Principal=" + new ToStringBuilder().toString(request.getUserPrincipal());
  }

  @HttpGet
  public String session(RequestWrapper request, HttpServletResponse response) throws Exception {
    return "MemFormController.session: Principal=" + new ToStringBuilder().toString(request.getUserPrincipal())
        + ", Session=" + request.getSession().getId();
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
  @Authorize(reauth = true)
  public String reauth(RequestWrapper request, HttpServletResponse response) throws Exception {
    return "MemFormController.reauth: Principal=" + new ToStringBuilder().toString(request.getUserPrincipal());
  }
}
