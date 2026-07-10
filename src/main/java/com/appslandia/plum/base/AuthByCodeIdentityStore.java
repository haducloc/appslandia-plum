// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import com.appslandia.common.base.Out;
import com.appslandia.common.utils.STR;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.security.enterprise.credential.Credential;
import jakarta.servlet.http.HttpServletRequest;

/**
 *
 * @author Loc Ha
 *
 */
public abstract class AuthByCodeIdentityStore extends IdentityStoreBase {

  public static final String CONFIG_BOUND_CLIENT_IP = AuthByCodeIdentityStore.class.getName() + ".bound_client_ip";
  public static final String CONFIG_BOUND_USER_AGENT = AuthByCodeIdentityStore.class.getName() + ".bound_user_agent";

  @Inject
  protected AppConfig appConfig;

  @Inject
  protected AppLogger appLogger;

  @Inject
  protected AuthTokenHandler authTokenHandler;

  @Inject
  protected HttpServletRequest currentRequest;

  protected boolean boundClientIp;
  protected boolean boundUserAgent;

  @PostConstruct
  protected void initialize() {
    boundClientIp = appConfig.getBool(CONFIG_BOUND_CLIENT_IP, false);
    boundUserAgent = appConfig.getBool(CONFIG_BOUND_USER_AGENT, true);

    appLogger.info(STR.fmt("{}: {}", CONFIG_BOUND_CLIENT_IP, boundClientIp));
    appLogger.info(STR.fmt("{}: {}", CONFIG_BOUND_USER_AGENT, boundUserAgent));
  }

  protected abstract AuthResult doValidate(String module, String identity);

  @Override
  protected AuthResult doValidate(String module, Credential credential) {
    if (!(credential instanceof AuthByCodeCredential authByCodeCred)) {
      return AuthResult.NOT_VALIDATED_RESULT;
    }
    var clientBoundData = getClientBoundData(currentRequest, boundClientIp, boundUserAgent);

    // Verify Token
    var invalidCode = new Out<String>();
    var authToken = authTokenHandler.verifyToken(module, authByCodeCred.getSeries(), authByCodeCred.getToken(),
        authByCodeCred.getCode(), clientBoundData, invalidCode);

    if (invalidCode.value != null) {
      return AuthResult.toInvalidResult(invalidCode.value);
    }

    return doValidate(module, authToken.getIdentity());
  }

  public SeriesToken saveToken(String module, String identity, String code, int expiresInSec) {
    var clientBoundData = getClientBoundData(currentRequest, boundClientIp, boundUserAgent);
    return authTokenHandler.saveToken(module, identity, code, clientBoundData, expiresInSec);
  }

  protected String getClientBoundData(HttpServletRequest request, boolean boundClientIp, boolean boundUserAgent) {
    return ServletUtils.getClientBoundData(request, boundClientIp, boundUserAgent);
  }
}
