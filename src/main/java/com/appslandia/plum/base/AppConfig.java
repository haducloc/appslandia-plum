// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import com.appslandia.common.base.BoolFormatException;
import com.appslandia.common.base.Config;
import com.appslandia.common.base.InitializingObject;
import com.appslandia.common.base.SimpleConfig;
import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.CollectionUtils;

/**
 *
 * @author Loc Ha
 *
 */
public class AppConfig extends InitializingObject implements Config {

  public static final String CONFIG_DEFAULT_MODULE = "config.default_module";
  public static final String CONFIG_ENABLE_MODULES = "config.enable_modules";

  public static final String CONFIG_ENABLE_DEBUG = "config.enable_debug";
  public static final String CONFIG_ENABLE_SESSION = "config.enable_session";
  public static final String CONFIG_ENABLE_JSON_PRETTY = "config.enable_json_pretty";

  public static final String CONFIG_VIEW_DIR = "config.view_dir";
  public static final String CONFIG_VIEW_SUFFIXES = "config.view_suffixes";
  public static final String CONFIG_RESOURCE_NAMES = "config.resource_names";

  public static final String REMEMBER_ME_COOKIE_NAME = "remember_me.cookie_name";
  public static final String REMEMBER_ME_COOKIE_AGE = "remember_me.cookie_age";
  public static final String REMEMBER_ME_COOKIE_SECURE = "remember_me.cookie_secure";
  public static final String REMEMBER_ME_COOKIE_HTTPONLY = "remember_me.cookie_httponly";
  public static final String REMEMBER_ME_COOKIE_SLIDING_EXP = "remember_me.cookie_sliding_exp";

  public static final String CONFIG_NONCE_SIZE = "config.nonce_size";
  public static final String CONFIG_COMPRESS_THRESHOLD = "config.compress_threshold";
  public static final String CONFIG_REAUTH_TIMEOUT_SEC = "config.reauth_timeout_sec";
  public static final String CONFIG_CSP_REPORT_ONLY = "config.csp_report_only";

  protected SimpleConfig config;

  private Set<String> modules;
  private boolean enableDebug;
  private boolean enableSession;

  private String viewDir;
  private List<String> viewSuffixes;

  public AppConfig() {
  }

  public AppConfig(SimpleConfig config) {
    this.config = config;
  }

  @Override
  protected void init() throws Exception {
    var config = Arguments.notNull(this.config, "config is required.");

    config.putIfAbsent(CONFIG_DEFAULT_MODULE, Modules.DEFAULT);
    var defaultModule = config.getString(CONFIG_DEFAULT_MODULE);
    config.putIfAbsent(CONFIG_ENABLE_MODULES, defaultModule);

    config.putIfAbsent(CONFIG_ENABLE_DEBUG, String.valueOf(false));
    config.putIfAbsent(CONFIG_ENABLE_SESSION, String.valueOf(true));
    config.putIfAbsent(CONFIG_ENABLE_JSON_PRETTY, String.valueOf(true));

    config.putIfAbsent(CONFIG_VIEW_DIR, "/WEB-INF/views");
    config.putIfAbsent(CONFIG_VIEW_SUFFIXES, ".jsp,.xhtml,.peb");

    config.putIfAbsent(REMEMBER_ME_COOKIE_NAME, "__remember_me");
    config.putIfAbsent(REMEMBER_ME_COOKIE_AGE, String.valueOf(TimeUnit.SECONDS.convert(14, TimeUnit.DAYS)));
    config.putIfAbsent(REMEMBER_ME_COOKIE_SECURE, String.valueOf(false));
    config.putIfAbsent(REMEMBER_ME_COOKIE_HTTPONLY, String.valueOf(true));
    config.putIfAbsent(REMEMBER_ME_COOKIE_SLIDING_EXP, String.valueOf(true));

    config.putIfAbsent(CONFIG_NONCE_SIZE, String.valueOf(16));
    config.putIfAbsent(CONFIG_COMPRESS_THRESHOLD, String.valueOf(4096));
    config.putIfAbsent(CONFIG_REAUTH_TIMEOUT_SEC, String.valueOf(TimeUnit.SECONDS.convert(10, TimeUnit.MINUTES)));
    config.putIfAbsent(CONFIG_CSP_REPORT_ONLY, String.valueOf(true));

    enableDebug = config.getBool(CONFIG_ENABLE_DEBUG);
    enableSession = config.getBool(CONFIG_ENABLE_SESSION);

    viewDir = config.getString(CONFIG_VIEW_DIR);
    viewSuffixes = CollectionUtils.toUnmodifiableList(config.getStringArray(CONFIG_VIEW_SUFFIXES));

    modules = CollectionUtils.toUnmodifiableSet(config.getStringArray(CONFIG_ENABLE_MODULES));
    Asserts.isTrue(modules.contains(defaultModule), "Default module '{}' is not enabled.", defaultModule);
  }

  public boolean isEnableDebug() {
    initialize();
    return enableDebug;
  }

  public boolean isEnableSession() {
    initialize();
    return enableSession;
  }

  public String getViewDir() {
    initialize();
    return viewDir;
  }

  public List<String> getViewSuffixes() {
    initialize();
    return viewSuffixes;
  }

  public Set<String> getModules() {
    initialize();
    return modules;
  }

  public StringBuilder getViewBase() {
    return new StringBuilder(getViewDir().length() + 80).append(getViewDir());
  }

  public String getViewPath(String subPath) {
    return getViewBase().append(subPath).toString();
  }

  @Override
  public Iterator<String> getKeys() {
    initialize();
    return config.getKeys();
  }

  @Override
  public String getString(String key) {
    initialize();
    return config.getString(key);
  }

  @Override
  public String getString(String key, String ifNull) {
    initialize();
    return config.getString(key, ifNull);
  }

  @Override
  public String getStringReq(String key) {
    initialize();
    return config.getStringReq(key);
  }

  @Override
  public String[] getStringArray(String key) {
    initialize();
    return config.getStringArray(key);
  }

  @Override
  public String[] getStringArray(String key, String ifNullValues) {
    initialize();
    return config.getStringArray(key, ifNullValues);
  }

  @Override
  public boolean getBool(String key) throws BoolFormatException {
    initialize();
    return config.getBool(key);
  }

  @Override
  public boolean getBool(String key, boolean ifNullOrInvalid) {
    initialize();
    return config.getBool(key, ifNullOrInvalid);
  }

  @Override
  public int getInt(String key) throws NumberFormatException {
    initialize();
    return config.getInt(key);
  }

  @Override
  public int getInt(String key, int ifNullOrInvalid) {
    initialize();
    return config.getInt(key, ifNullOrInvalid);
  }

  @Override
  public long getLong(String key) throws NumberFormatException {
    initialize();
    return config.getLong(key);
  }

  @Override
  public long getLong(String key, long ifNullOrInvalid) {
    initialize();
    return config.getLong(key, ifNullOrInvalid);
  }

  @Override
  public double getDouble(String key) throws NumberFormatException {
    initialize();
    return config.getDouble(key);
  }

  @Override
  public double getDouble(String key, double ifNullOrInvalid) {
    initialize();
    return config.getDouble(key, ifNullOrInvalid);
  }

  @Override
  public BigDecimal getDecimalReq(String key) throws NumberFormatException {
    initialize();
    return config.getDecimalReq(key);
  }

  @Override
  public BigDecimal getDecimal(String key, double ifNullOrInvalid) {
    initialize();
    return config.getDecimal(key, ifNullOrInvalid);
  }

  @Override
  public <T> T getValue(String key, Function<String, T> converter) {
    initialize();
    return config.getValue(key, converter);
  }

  @Override
  public String resolve(String key) {
    initialize();
    return config.resolve(key);
  }

  @Override
  public String resolve(String key, Map<String, Object> parameters) {
    initialize();
    return config.resolve(key, parameters);
  }

  @Override
  public String resolve(String key, Object... parameters) {
    initialize();
    return config.resolve(key, parameters);
  }
}
