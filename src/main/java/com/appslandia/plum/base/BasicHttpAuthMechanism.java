// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.nio.charset.StandardCharsets;

import com.appslandia.common.base.BaseEncoder;

import jakarta.security.enterprise.credential.Credential;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * The implementation must be exposed as a CDI bean with @ApplicationScoped and annotated with @MappedID using the using
 * the target module name.
 *
 * @author Loc Ha
 *
 */
public abstract class BasicHttpAuthMechanism extends AuthorizationMechanismBase {

  @Override
  public AuthMethod getAuthMethod() {
    return AuthMethod.BASIC;
  }

  protected Credential createCredential(String username, String password) {
    return new UsernamePasswordCredential(username, password);
  }

  @Override
  protected Credential doParseCredential(String credential) throws Exception {
    var usernamePwd = new String(BaseEncoder.BASE64.decode(credential), StandardCharsets.UTF_8);

    // username:password
    var idx = usernamePwd.indexOf(":");
    if (idx < 0) {
      return null;
    }
    var username = usernamePwd.substring(0, idx).strip();
    var password = usernamePwd.substring(idx + 1);

    if (username.isEmpty() || password.isEmpty()) {
      return null;
    }
    return createCredential(username, password);
  }

  @Override
  public void askAuthenticate(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext)
      throws Exception {

    var challenge = getAuthMethod().getType() + " realm=\"" + requestContext.getModule() + "\"";
    response.setHeader("WWW-Authenticate", challenge);
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
  }
}
