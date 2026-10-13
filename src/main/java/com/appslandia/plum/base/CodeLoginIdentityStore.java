// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import com.appslandia.common.base.Out;
import com.appslandia.common.utils.Arguments;
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
public abstract class CodeLoginIdentityStore extends IdentityStoreBase {

  public static final String PURPOSE_CODE_LOGIN = "login_code";

  public static final String CONFIG_BOUND_CLIENT_IP = "code_login.bound_client_ip";
  public static final String CONFIG_BOUND_USER_AGENT = "code_login.bound_user_agent";
  public static final String CONFIG_EXPIRES_IN_SEC = "code_login.expires_in_sec";
  public static final String CONFIG_EXPIRY_LEEWAY_MS = "code_login.expiry_leeway_ms";
  public static final String CONFIG_MAX_CODE_ATTEMPTS = "code_login.max_code_attempts";

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

  protected int expiresInSec;
  protected int expiryLeewayMs;
  protected int maxCodeAttempts;

  @PostConstruct
  protected void initialize() {
    boundClientIp = appConfig.getBool(CONFIG_BOUND_CLIENT_IP, false);
    boundUserAgent = appConfig.getBool(CONFIG_BOUND_USER_AGENT, true);

    // 5 minutes
    expiresInSec = appConfig.getInt(CONFIG_EXPIRES_IN_SEC, 300);
    expiryLeewayMs = appConfig.getInt(CONFIG_EXPIRY_LEEWAY_MS, 0);
    maxCodeAttempts = appConfig.getInt(CONFIG_MAX_CODE_ATTEMPTS, 5);

    appLogger.info("{}: {}", CONFIG_BOUND_CLIENT_IP, boundClientIp);
    appLogger.info("{}: {}", CONFIG_BOUND_USER_AGENT, boundUserAgent);
    appLogger.info("{}: {}", CONFIG_EXPIRES_IN_SEC, expiresInSec);
    appLogger.info("{}: {}", CONFIG_EXPIRY_LEEWAY_MS, expiryLeewayMs);
    appLogger.info("{}: {}", CONFIG_MAX_CODE_ATTEMPTS, maxCodeAttempts);

    Arguments.isTrue(expiresInSec > 0);
    Arguments.isTrue(expiryLeewayMs >= 0);
    Arguments.isTrue(maxCodeAttempts > 0);
  }

  protected abstract AuthResult doValidate(String module, String identity);

  @Override
  protected AuthResult doValidate(String module, Credential credential) {
    if (!(credential instanceof CodeLoginCredential codeCred)) {
      return AuthResult.NOT_VALIDATED_RESULT;
    }

    var clientBoundData = getClientBoundData(currentRequest, boundClientIp, boundUserAgent);

    // Verify Token
    var invalidCode = new Out<String>();

    var authToken = authTokenHandler.verifyToken(module, PURPOSE_CODE_LOGIN, codeCred.getSeries(), codeCred.getToken(),
        codeCred.getCode(), clientBoundData, expiryLeewayMs, invalidCode);

    if (invalidCode.value != null) {
      return AuthResult.toInvalidResult(invalidCode.value);
    }

    return doValidate(module, authToken.getIdentity());
  }

  public SeriesToken saveToken(String module, String identity, String code) {
    Arguments.notNull(code);

    var clientBoundData = getClientBoundData(currentRequest, boundClientIp, boundUserAgent);
    return authTokenHandler.saveToken(module, PURPOSE_CODE_LOGIN, identity, null, clientBoundData, expiresInSec, code,
        maxCodeAttempts);
  }

  protected String getClientBoundData(HttpServletRequest request, boolean boundClientIp, boolean boundUserAgent) {
    return ServletUtils.getClientBoundData(request, boundClientIp, boundUserAgent);
  }
}
