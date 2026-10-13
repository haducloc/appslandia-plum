// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.oauth2;

import java.net.http.HttpClient;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.appslandia.common.base.TokenGenerator;
import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.CollectionUtils;
import com.appslandia.common.utils.ConfigUtils;
import com.appslandia.common.utils.STR;
import com.appslandia.common.utils.StringUtils;
import com.appslandia.common.utils.URLUtils;
import com.appslandia.plum.base.AppConfig;
import com.appslandia.plum.base.AppLogger;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Each OAuth2 provider must provide an implementation of this abstract class, exposed as an {@link ApplicationScoped}
 * CDI bean.
 *
 * @author Loc Ha
 */
public abstract class OAuth2Config {

  public static final String CONFIG_AUTHORIZATION_ENDPOINT = "oauth2.{}.authorization_endpoint";
  public static final String CONFIG_TOKEN_ENDPOINT = "oauth2.{}.token_endpoint";
  public static final String CONFIG_USER_INFO_ENDPOINT = "oauth2.{}.user_info_endpoint";

  public static final String CONFIG_CLIENT_ID = "{}.oauth2.{}.client_id";
  public static final String CONFIG_CLIENT_SECRET = "{}.oauth2.{}.client_secret";
  public static final String CONFIG_SCOPE = "{}.oauth2.{}.scope";
  public static final String CONFIG_LOGIN_CALLBACK_URI = "{}.oauth2.{}.login_callback_uri";
  public static final String CONFIG_LINK_CALLBACK_URI = "{}.oauth2.{}.link_callback_uri";

  public static final String CONFIG_STATE_EXPIRY_LEEWAY_MS = "{}.oauth2.state_expiry_leeway_ms";
  public static final String CONFIG_STATE_EXPIRES_IN_SEC = "{}.oauth2.state_expires_in_sec";
  public static final String CONFIG_STATE_TOKEN_LENGTH = "{}.oauth2.state_token_length";
  public static final String CONFIG_STATE_BOUND_CLIENT_IP = "{}.oauth2.state_bound_client_ip";
  public static final String CONFIG_STATE_BOUND_USER_AGENT = "{}.oauth2.state_bound_user_agent";

  public static final String CONFIG_CODE_VERIFIER_LENGTH = "{}.oauth2.code_verifier_length";
  public static final String CONFIG_SIGN_UP_EXPIRES_IN_SEC = "oauth2.sign_up_expires_in_sec";

  @Inject
  protected AppConfig appConfig;

  @Inject
  protected AppLogger appLogger;

  protected String authorizationEndpoint;
  protected String tokenEndpoint;
  protected String userInfoEndpoint;
  protected int signUpExpiresInSec;

  protected final Map<String, ResolvedValues> resolvedValuesMap = new ConcurrentHashMap<>();

  protected Map<String, String> authorizationParameters;
  protected Map<String, String> userInfoParameters;
  protected HttpClient httpClient;

  public abstract String getProvider();

  protected abstract Map<String, String> initAuthorizationParameters();

  protected abstract Map<String, String> initUserInfoParameters();

  protected abstract void initHttpClientBuilder(HttpClient.Builder builder);

  public abstract void configureTokenRequest(String module, Map<String, Object> params, Map<String, String> headers);

  public abstract void configureUserInfoRequest(String module, String accessToken, Map<String, Object> params,
      Map<String, String> headers);

  public abstract OAuth2UserInfo parseUserInfo(String userInfo);

  public abstract boolean isAccessTokenInvalid(OAuth2HttpException httpException);

  @PostConstruct
  protected void initialize() {
    var provider = getProvider();

    var authorizationEndpointConfig = STR.fmt(CONFIG_AUTHORIZATION_ENDPOINT, provider);
    var tokenEndpointConfig = STR.fmt(CONFIG_TOKEN_ENDPOINT, provider);
    var userInfoEndpointConfig = STR.fmt(CONFIG_USER_INFO_ENDPOINT, provider);

    authorizationEndpoint = appConfig.getString(authorizationEndpointConfig);
    tokenEndpoint = appConfig.getString(tokenEndpointConfig);
    userInfoEndpoint = appConfig.getString(userInfoEndpointConfig);
    signUpExpiresInSec = appConfig.getInt(CONFIG_SIGN_UP_EXPIRES_IN_SEC, 900);

    appLogger.info("{}: {}", authorizationEndpointConfig, authorizationEndpoint);
    appLogger.info("{}: {}", tokenEndpointConfig, tokenEndpoint);
    appLogger.info("{}: {}", userInfoEndpointConfig, userInfoEndpoint);
    appLogger.info("{}: {}", CONFIG_SIGN_UP_EXPIRES_IN_SEC, signUpExpiresInSec);

    Arguments.notNull(authorizationEndpoint, "authorizationEndpoint is required.");
    Arguments.notNull(tokenEndpoint, "tokenEndpoint is required.");
    Arguments.notNull(userInfoEndpoint, "userInfoEndpoint is required.");
    Arguments.isTrue(signUpExpiresInSec >= 900, "signUpExpiresInSec must be at least 15 minutes.");

    var authorizationParameters = initAuthorizationParameters();
    this.authorizationParameters = CollectionUtils.toUnmodifiableMap(authorizationParameters);
    appLogger.info("authorizationParameters: {}", this.authorizationParameters);

    var userInfoParameters = initUserInfoParameters();
    this.userInfoParameters = CollectionUtils.toUnmodifiableMap(userInfoParameters);
    appLogger.info("userInfoParameters: {}", this.userInfoParameters);

    var builder = HttpClient.newBuilder();
    initHttpClientBuilder(builder);
    this.httpClient = builder.build();
  }

  protected final Map<String, TokenGenerator> stateTokenGeneratorMap = new ConcurrentHashMap<>();

  public TokenGenerator getStateTokenGenerator(String module) {
    Arguments.notNull(module);

    return stateTokenGeneratorMap.computeIfAbsent(module,
        mod -> new TokenGenerator(getResolvedValues(mod).stateTokenLength()));
  }

  protected final Map<String, TokenGenerator> codeVerifierGeneratorMap = new ConcurrentHashMap<>();

  public TokenGenerator getCodeVerifierGenerator(String module) {
    Arguments.notNull(module);

    return codeVerifierGeneratorMap.computeIfAbsent(module,
        mod -> new TokenGenerator(getResolvedValues(mod).codeVerifierLength()));
  }

  protected ResolvedValues getResolvedValues(String module) {
    Arguments.notNull(module);

    return resolvedValuesMap.computeIfAbsent(module, mod -> {
      var provider = getProvider();

      var clientIdConfig = STR.fmt(CONFIG_CLIENT_ID, mod, provider);
      var clientSecretConfig = STR.fmt(CONFIG_CLIENT_SECRET, mod, provider);
      var scopeConfig = STR.fmt(CONFIG_SCOPE, mod, provider);
      var loginCallbackUriConfig = STR.fmt(CONFIG_LOGIN_CALLBACK_URI, mod, provider);
      var linkCallbackUriConfig = STR.fmt(CONFIG_LINK_CALLBACK_URI, mod, provider);

      var stateExpiryLeewayMsConfig = STR.fmt(CONFIG_STATE_EXPIRY_LEEWAY_MS, mod);
      var stateExpiresInSecConfig = STR.fmt(CONFIG_STATE_EXPIRES_IN_SEC, mod);
      var stateTokenLengthConfig = STR.fmt(CONFIG_STATE_TOKEN_LENGTH, mod);
      var stateBoundClientIpConfig = STR.fmt(CONFIG_STATE_BOUND_CLIENT_IP, mod);
      var stateBoundUserAgentConfig = STR.fmt(CONFIG_STATE_BOUND_USER_AGENT, mod);
      var codeVerifierLengthConfig = STR.fmt(CONFIG_CODE_VERIFIER_LENGTH, mod);

      var clientId = appConfig.resolve(clientIdConfig);
      var clientSecret = appConfig.resolve(clientSecretConfig);

      var scopeValues = ConfigUtils.toMultilineValues(appConfig.getString(scopeConfig), ',');
      var scope = CollectionUtils.toUnmodifiableSet(LinkedHashSet::new, scopeValues);

      var loginCallbackUri = appConfig.getString(loginCallbackUriConfig);
      var linkCallbackUri = appConfig.getString(linkCallbackUriConfig);

      // 30 seconds
      var stateExpiryLeewayMs = appConfig.getInt(stateExpiryLeewayMsConfig, 30_000);

      // 10 minutes
      var stateExpiresInSec = appConfig.getInt(stateExpiresInSecConfig, 600);

      // 64 length
      var stateTokenLength = appConfig.getInt(stateTokenLengthConfig, 64);
      var stateBoundClientIp = appConfig.getBool(stateBoundClientIpConfig, false);
      var stateBoundUserAgent = appConfig.getBool(stateBoundUserAgentConfig, true);

      // 64 length
      var codeVerifierLength = appConfig.getInt(codeVerifierLengthConfig, 64);

      appLogger.info("{}: {}", clientIdConfig, StringUtils.maskExceptStart(clientId, 4));
      appLogger.info("{}: {}", clientSecretConfig, StringUtils.maskExceptStart(clientSecret, 4));
      appLogger.info("{}: {}", scopeConfig, scope);
      appLogger.info("{}: {}", loginCallbackUriConfig, loginCallbackUri);
      appLogger.info("{}: {}", linkCallbackUriConfig, linkCallbackUri);

      appLogger.info("{}: {}", stateExpiryLeewayMsConfig, stateExpiryLeewayMs);
      appLogger.info("{}: {}", stateExpiresInSecConfig, stateExpiresInSec);
      appLogger.info("{}: {}", stateTokenLengthConfig, stateTokenLength);
      appLogger.info("{}: {}", stateBoundClientIpConfig, stateBoundClientIp);
      appLogger.info("{}: {}", stateBoundUserAgentConfig, stateBoundUserAgent);
      appLogger.info("{}: {}", codeVerifierLengthConfig, codeVerifierLength);

      Arguments.notNull(clientId, "clientId is required.");
      Arguments.isTrue(!scope.isEmpty(), "scope is required.");
      Arguments.notNull(loginCallbackUri, "loginCallbackUri is required.");

      Arguments.isTrue(URLUtils.isRelativeUri(loginCallbackUri), "loginCallbackUri must be a relative URI.");
      if (linkCallbackUri != null) {
        Arguments.isTrue(URLUtils.isRelativeUri(linkCallbackUri), "linkCallbackUri must be a relative URI.");
      }

      Arguments.isTrue(stateExpiryLeewayMs >= 0);
      Arguments.isTrue(stateExpiresInSec > 0);
      Arguments.isTrue(stateTokenLength >= 32);
      Arguments.isTrue(codeVerifierLength >= 43 && codeVerifierLength <= 128);

      return new ResolvedValues(clientId, clientSecret, scope, loginCallbackUri, linkCallbackUri, stateExpiryLeewayMs,
          stateExpiresInSec, stateTokenLength, stateBoundClientIp, stateBoundUserAgent, codeVerifierLength);
    });
  }

  public String getAuthorizationEndpoint() {
    return authorizationEndpoint;
  }

  public String getTokenEndpoint() {
    return tokenEndpoint;
  }

  public String getUserInfoEndpoint() {
    return userInfoEndpoint;
  }

  public String getClientId(String module) {
    return getResolvedValues(module).clientId();
  }

  public String getClientSecret(String module) {
    return getResolvedValues(module).clientSecret();
  }

  public String getLoginCallbackUri(String module) {
    return getResolvedValues(module).loginCallbackUri();
  }

  public String getLinkCallbackUri(String module) {
    return getResolvedValues(module).linkCallbackUri();
  }

  public int getStateExpiryLeewayMs(String module) {
    return getResolvedValues(module).stateExpiryLeewayMs();
  }

  public int getStateExpiresInSec(String module) {
    return getResolvedValues(module).stateExpiresInSec();
  }

  public int getStateTokenLength(String module) {
    return getResolvedValues(module).stateTokenLength();
  }

  public boolean isStateBoundClientIp(String module) {
    return getResolvedValues(module).stateBoundClientIp();
  }

  public boolean isStateBoundUserAgent(String module) {
    return getResolvedValues(module).stateBoundUserAgent();
  }

  public int getCodeVerifierLength(String module) {
    return getResolvedValues(module).codeVerifierLength();
  }

  public Set<String> getScope(String module) {
    return getResolvedValues(module).scope();
  }

  public int getSignUpExpiresInSec() {
    return signUpExpiresInSec;
  }

  public Map<String, String> getAuthorizationParameters() {
    return authorizationParameters;
  }

  public Map<String, String> getUserInfoParameters() {
    return userInfoParameters;
  }

  public HttpClient getHttpClient() {
    return httpClient;
  }

  protected static record ResolvedValues(String clientId, String clientSecret, Set<String> scope,
      String loginCallbackUri, String linkCallbackUri, int stateExpiryLeewayMs, int stateExpiresInSec,
      int stateTokenLength, boolean stateBoundClientIp, boolean stateBoundUserAgent, int codeVerifierLength) {
  }
}
