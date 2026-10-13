// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.math.BigDecimal;
import java.time.Duration;
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

/**
 *
 * @author Loc Ha
 *
 */
public class AppConfig extends InitializingObject implements Config {

  public static final String CONFIG_ENABLE_MODULES = "config.enable_modules";

  public static final String CONFIG_ENABLE_DEBUG = "config.enable_debug";
  public static final String CONFIG_ENABLE_JSON_PRETTY = "config.enable_json_pretty";

  public static final String CONFIG_VIEW_DIR = "config.view_dir";
  public static final String CONFIG_VIEW_SUFFIXES = "config.view_suffixes";

  public static final String CONFIG_NONCE_SIZE = "config.nonce_size";
  public static final String CONFIG_COMPRESS_THRESHOLD = "config.compress_threshold";
  public static final String CONFIG_REAUTH_TIMEOUT_SEC = "config.reauth_timeout_sec";
  public static final String CONFIG_CSP_REPORT_ONLY = "config.csp_report_only";

  protected SimpleConfig config;

  private Set<String> enableModules;
  private boolean enableDebug;

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

    config.putIfAbsent(CONFIG_ENABLE_MODULES, "default");

    config.putIfAbsent(CONFIG_ENABLE_DEBUG, String.valueOf(false));
    config.putIfAbsent(CONFIG_ENABLE_JSON_PRETTY, String.valueOf(true));

    config.putIfAbsent(CONFIG_VIEW_DIR, "/WEB-INF/views");
    config.putIfAbsent(CONFIG_VIEW_SUFFIXES, ".jsp,.xhtml,.peb");

    config.putIfAbsent(CONFIG_NONCE_SIZE, String.valueOf(16));
    config.putIfAbsent(CONFIG_COMPRESS_THRESHOLD, String.valueOf(4096));
    config.putIfAbsent(CONFIG_REAUTH_TIMEOUT_SEC, String.valueOf(TimeUnit.SECONDS.convert(10, TimeUnit.MINUTES)));
    config.putIfAbsent(CONFIG_CSP_REPORT_ONLY, String.valueOf(true));

    enableDebug = config.getBool(CONFIG_ENABLE_DEBUG);
    enableModules = Set.of(config.getStringArrayReq(CONFIG_ENABLE_MODULES));

    viewDir = config.getStringReq(CONFIG_VIEW_DIR);
    viewSuffixes = List.of(config.getStringArrayReq(CONFIG_VIEW_SUFFIXES));
  }

  public boolean isEnableDebug() {
    initialize();
    return enableDebug;
  }

  public String getViewDir() {
    initialize();
    return viewDir;
  }

  public List<String> getViewSuffixes() {
    initialize();
    return viewSuffixes;
  }

  public Set<String> getEnableModules() {
    initialize();
    return enableModules;
  }

  public boolean isApiModule(String module) {
    return config.getBool(module + ".api_module", false);
  }

  public String getLoginUri(String module) {
    return config.getString(module + ".login_uri");
  }

  public String getHomeUri(String module) {
    return config.getString(module + ".home_uri");
  }

  public String getRegisterUri(String module) {
    return config.getString(module + ".register_uri");
  }

  public String getProfileUri(String module) {
    return config.getString(module + ".profile_uri");
  }

  public StringBuilder getViewBase() {
    return new StringBuilder(getViewDir().length() + 80).append(getViewDir());
  }

  public String getViewPath(String subPath) {
    return getViewBase().append(subPath).toString();
  }

  // Keys

  @Override
  public Iterator<String> getKeys() {
    initialize();
    return config.getKeys();
  }

  // String

  @Override
  public String getString(String key) {
    initialize();
    return config.getString(key);
  }

  @Override
  public String getStringReq(String key) {
    initialize();
    return config.getStringReq(key);
  }

  @Override
  public String getString(String key, String ifNull) {
    initialize();
    return config.getString(key, ifNull);
  }

  // String Array

  @Override
  public String[] getStringArrayReq(String key) {
    initialize();
    return config.getStringArrayReq(key);
  }

  @Override
  public String[] getStringArray(String key) {
    initialize();
    return config.getStringArray(key);
  }

  // Boolean

  @Override
  public boolean getBool(String key) throws BoolFormatException {
    initialize();
    return config.getBool(key);
  }

  @Override
  public Boolean getBoolOpt(String key) throws BoolFormatException {
    initialize();
    return config.getBoolOpt(key);
  }

  @Override
  public boolean getBool(String key, boolean ifNullOrInvalid) {
    initialize();
    return config.getBool(key, ifNullOrInvalid);
  }

  // Integer

  @Override
  public int getInt(String key) throws NumberFormatException {
    initialize();
    return config.getInt(key);
  }

  @Override
  public Integer getIntOpt(String key) throws NumberFormatException {
    initialize();
    return config.getIntOpt(key);
  }

  @Override
  public int getInt(String key, int ifNullOrInvalid) {
    initialize();
    return config.getInt(key, ifNullOrInvalid);
  }

  // Long

  @Override
  public long getLong(String key) throws NumberFormatException {
    initialize();
    return config.getLong(key);
  }

  @Override
  public Long getLongOpt(String key) throws NumberFormatException {
    initialize();
    return config.getLongOpt(key);
  }

  @Override
  public long getLong(String key, long ifNullOrInvalid) {
    initialize();
    return config.getLong(key, ifNullOrInvalid);
  }

  // Double

  @Override
  public double getDouble(String key) throws NumberFormatException {
    initialize();
    return config.getDouble(key);
  }

  @Override
  public Double getDoubleOpt(String key) throws NumberFormatException {
    initialize();
    return config.getDoubleOpt(key);
  }

  @Override
  public double getDouble(String key, double ifNullOrInvalid) {
    initialize();
    return config.getDouble(key, ifNullOrInvalid);
  }

  // BigDecimal

  @Override
  public BigDecimal getDecimalReq(String key) throws NumberFormatException {
    initialize();
    return config.getDecimalReq(key);
  }

  @Override
  public BigDecimal getDecimal(String key) throws NumberFormatException {
    initialize();
    return config.getDecimal(key);
  }

  @Override
  public BigDecimal getDecimal(String key, BigDecimal ifNullOrInvalid) {
    initialize();
    return config.getDecimal(key, ifNullOrInvalid);
  }

  // Duration

  @Override
  public Duration getDurationReq(String key) {
    initialize();
    return config.getDurationReq(key);
  }

  @Override
  public Duration getDuration(String key) {
    initialize();
    return config.getDuration(key);
  }

  @Override
  public Duration getDuration(String key, Duration ifNullOrInvalid) {
    initialize();
    return config.getDuration(key, ifNullOrInvalid);
  }

  // Enum

  @Override
  public <E extends Enum<E>> E getEnumReq(String key, Class<E> type) {
    initialize();
    return config.getEnumReq(key, type);
  }

  @Override
  public <E extends Enum<E>> E getEnum(String key, Class<E> type) {
    initialize();
    return config.getEnum(key, type);
  }

  @Override
  public <E extends Enum<E>> E getEnum(String key, Class<E> type, E ifNullOrInvalid) {
    initialize();
    return config.getEnum(key, type, ifNullOrInvalid);
  }

  // Value

  @Override
  public <T> T getValueReq(String key, Function<String, T> converter) {
    initialize();
    return config.getValueReq(key, converter);
  }

  @Override
  public <T> T getValue(String key, Function<String, T> converter) {
    initialize();
    return config.getValue(key, converter);
  }

  @Override
  public <T> T getValue(String key, T ifNullOrInvalid, Function<String, T> converter) {
    initialize();
    return config.getValue(key, ifNullOrInvalid, converter);
  }

  // Resolve

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
