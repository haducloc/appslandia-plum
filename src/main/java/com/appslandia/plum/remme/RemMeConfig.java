// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.remme;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.appslandia.common.base.TokenGenerator;
import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.STR;
import com.appslandia.plum.base.AppConfig;
import com.appslandia.plum.base.AppLogger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class RemMeConfig {

  public static final String CONFIG_REMME_COOKIE_NAME = "{}.remember_me.cookie_name";
  public static final String CONFIG_REMME_COOKIE_AGE = "{}.remember_me.cookie_age";
  public static final String CONFIG_REMME_COOKIE_SECURE = "{}.remember_me.cookie_secure";
  public static final String CONFIG_REMME_COOKIE_HTTPONLY = "{}.remember_me.cookie_httponly";
  public static final String CONFIG_REMME_COOKIE_SLIDING_EXPIRY = "{}.remember_me.cookie_sliding_expiry";

  public static final String CONFIG_REMME_BOUND_CLIENT_IP = "{}.remember_me.bound_client_ip";
  public static final String CONFIG_REMME_BOUND_USER_AGENT = "{}.remember_me.bound_user_agent";
  public static final String CONFIG_REMME_EXPIRY_LEEWAY_MS = "{}.remember_me.expiry_leeway_ms";
  public static final String CONFIG_REMME_TOKEN_LENGTH = "{}.remember_me.token_length";

  @Inject
  protected AppConfig appConfig;

  @Inject
  protected AppLogger appLogger;

  protected final Map<String, TokenGenerator> tokenGeneratorMap = new ConcurrentHashMap<>();

  public TokenGenerator getTokenGenerator(String module) {
    Arguments.notNull(module);

    return tokenGeneratorMap.computeIfAbsent(module, mod -> new TokenGenerator(getResolvedValues(mod).tokenLength()));
  }

  protected final Map<String, ResolvedValues> resolvedValuesMap = new ConcurrentHashMap<>();

  protected ResolvedValues getResolvedValues(String module) {
    Arguments.notNull(module);

    return resolvedValuesMap.computeIfAbsent(module, mod -> {

      var cookieNameConfig = STR.fmt(CONFIG_REMME_COOKIE_NAME, mod);
      var cookieAgeConfig = STR.fmt(CONFIG_REMME_COOKIE_AGE, mod);
      var cookieSecureConfig = STR.fmt(CONFIG_REMME_COOKIE_SECURE, mod);
      var cookieHttpOnlyConfig = STR.fmt(CONFIG_REMME_COOKIE_HTTPONLY, mod);
      var cookieSlidingExpiryConfig = STR.fmt(CONFIG_REMME_COOKIE_SLIDING_EXPIRY, mod);

      var boundClientIpConfig = STR.fmt(CONFIG_REMME_BOUND_CLIENT_IP, mod);
      var boundUserAgentConfig = STR.fmt(CONFIG_REMME_BOUND_USER_AGENT, mod);
      var expiryLeewayMsConfig = STR.fmt(CONFIG_REMME_EXPIRY_LEEWAY_MS, mod);
      var tokenLengthConfig = STR.fmt(CONFIG_REMME_TOKEN_LENGTH, mod);

      var cookieName = appConfig.getString(cookieNameConfig, mod + ".remme");

      // 14 days
      var cookieAge = appConfig.getInt(cookieAgeConfig, (int) TimeUnit.SECONDS.convert(14, TimeUnit.DAYS));
      var cookieSecure = appConfig.getBool(cookieSecureConfig, false);
      var cookieHttpOnly = appConfig.getBool(cookieHttpOnlyConfig, true);
      var cookieSlidingExpiry = appConfig.getBool(cookieSlidingExpiryConfig, true);

      var tokenBoundClientIp = appConfig.getBool(boundClientIpConfig, false);
      var tokenBoundUserAgent = appConfig.getBool(boundUserAgentConfig, true);

      // 30 seconds
      var tokenExpiryLeewayMs = appConfig.getInt(expiryLeewayMsConfig, 30_000);

      // 64 length
      var tokenLength = appConfig.getInt(tokenLengthConfig, 64);

      appLogger.info("{}: {}", cookieNameConfig, cookieName);
      appLogger.info("{}: {}", cookieAgeConfig, cookieAge);
      appLogger.info("{}: {}", cookieSecureConfig, cookieSecure);
      appLogger.info("{}: {}", cookieHttpOnlyConfig, cookieHttpOnly);
      appLogger.info("{}: {}", cookieSlidingExpiryConfig, cookieSlidingExpiry);

      appLogger.info("{}: {}", boundClientIpConfig, tokenBoundClientIp);
      appLogger.info("{}: {}", boundUserAgentConfig, tokenBoundUserAgent);
      appLogger.info("{}: {}", expiryLeewayMsConfig, tokenExpiryLeewayMs);
      appLogger.info("{}: {}", tokenLengthConfig, tokenLength);

      Arguments.isTrue(cookieAge > 0);
      Arguments.isTrue(tokenExpiryLeewayMs >= 0);
      Arguments.isTrue(tokenLength >= 32);

      return new ResolvedValues(cookieName, cookieAge, cookieSecure, cookieHttpOnly, cookieSlidingExpiry,
          tokenBoundClientIp, tokenBoundUserAgent, tokenExpiryLeewayMs, tokenLength);
    });
  }

  public String getRemMeCookieName(String module) {
    return getResolvedValues(module).cookieName();
  }

  public int getRemMeCookieAge(String module) {
    return getResolvedValues(module).cookieAge();
  }

  public boolean isRemMeCookieSecure(String module) {
    return getResolvedValues(module).cookieSecure();
  }

  public boolean isRemMeCookieHttpOnly(String module) {
    return getResolvedValues(module).cookieHttpOnly();
  }

  public boolean isRemMeCookieSlidingExpiry(String module) {
    return getResolvedValues(module).cookieSlidingExpiry();
  }

  public boolean isRemMeBoundClientIp(String module) {
    return getResolvedValues(module).tokenBoundClientIp();
  }

  public boolean isRemMeBoundUserAgent(String module) {
    return getResolvedValues(module).tokenBoundUserAgent();
  }

  public int getRemMeExpiryLeewayMs(String module) {
    return getResolvedValues(module).tokenExpiryLeewayMs();
  }

  public int getRemMeTokenLength(String module) {
    return getResolvedValues(module).tokenLength();
  }

  protected static record ResolvedValues(String cookieName, int cookieAge, boolean cookieSecure, boolean cookieHttpOnly,
      boolean cookieSlidingExpiry, boolean tokenBoundClientIp, boolean tokenBoundUserAgent, int tokenExpiryLeewayMs,
      int tokenLength) {
  }
}
