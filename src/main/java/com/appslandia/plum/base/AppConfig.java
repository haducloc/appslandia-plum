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

package com.appslandia.plum.base;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import com.appslandia.common.base.BoolFormatException;
import com.appslandia.common.base.Config;
import com.appslandia.common.base.InitializeObject;
import com.appslandia.common.base.SimpleConfig;
import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.CollectionUtils;

/**
 *
 * @author Loc Ha
 *
 */
public class AppConfig extends InitializeObject implements Config {

  public static final String CONFIG_DEFAULT_MODULE = "config.default_module";
  public static final String CONFIG_ENABLE_DEBUG = "config.enable_debug";
  public static final String CONFIG_ENABLE_SESSION = "config.enable_session";

  public static final String CONFIG_ENABLE_JSON_PRETTY = "config.enable_json_pretty";
  public static final String CONFIG_DISABLE_ENCODING = "config.disable_encoding";

  public static final String CONFIG_VIEW_DIR = "config.view_dir";
  public static final String CONFIG_VIEW_SUFFIXES = "config.view_suffixes";
  public static final String CONFIG_REAUTH_TIMEOUT_MS = "config.reauth_timeout_ms";

  public static final String REMEMBER_ME_COOKIE_NAME = "remember_me.cookie_name";
  public static final String REMEMBER_ME_COOKIE_AGE = "remember_me.cookie_age";
  public static final String REMEMBER_ME_COOKIE_SECURE = "remember_me.cookie_secure";
  public static final String REMEMBER_ME_COOKIE_HTTPONLY = "remember_me.cookie_httponly";
  public static final String REMEMBER_ME_COOKIE_SLIDING_EXP = "remember_me.cookie_sliding_exp";

  public static final String CONFIG_ENABLE_HEADER_POLICIES = "config.enable_header_policies";
  public static final String CONFIG_RESOURCE_NAMES = "config.resource_names";
  public static final String CONFIG_X_FORWARDED_PORTS = "config.x_forwarded_ports";

  public static final String HEADER_STRICT_TRANSPORT_SECURITY = "header.strict_transport_security";
  public static final String HEADER_X_CONTENT_TYPE_OPTIONS = "header.x_content_type_options";
  public static final String HEADER_X_FRAME_OPTIONS = "header.x_frame_options";
  public static final String HEADER_X_XSS_PROTECTION = "header.x_xss_protection";
  public static final String HEADER_CONTENT_SECURITY_POLICY = "header.content_security_policy";
  public static final String HEADER_REFERRER_POLICY = "header.referrer_policy";

  public static final String CONFIG_CSP_NONCE_SIZE = "config.csp_nonce_size";
  public static final String HEADER_CSP_REPORT_ONLY = "header.csp_report_only";
  public static final String HEADER_REPORT_TO = "header.report_to";
  public static final String HEADER_REPORTING_ENDPOINTS = "header.reporting_endpoints";

  public static final String HEADER_CROSS_ORIGIN_EMBEDDER_POLICY = "header.cross_origin_embedder_policy";
  public static final String HEADER_CROSS_ORIGIN_OPENER_POLICY = "header.cross_origin_opener_policy";
  public static final String HEADER_CROSS_ORIGIN_RESOURCE_POLICY = "header.cross_origin_resource_policy";

  public static final String HEADER_PERMISSIONS_POLICY = "header.permissions_policy";

  protected SimpleConfig config;

  private boolean enableDebug;
  private boolean enableSession;
  private int cspNonceSize;
  private boolean cspRptOnly;

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
    config.putIfAbsent(CONFIG_ENABLE_DEBUG, String.valueOf(false));
    config.putIfAbsent(CONFIG_ENABLE_SESSION, String.valueOf(true));

    config.putIfAbsent(CONFIG_ENABLE_JSON_PRETTY, String.valueOf(true));
    config.putIfAbsent(CONFIG_DISABLE_ENCODING, String.valueOf(true));

    config.putIfAbsent(CONFIG_VIEW_DIR, "/WEB-INF/views");
    config.putIfAbsent(CONFIG_VIEW_SUFFIXES, ".jsp,.jspx,.xhtml,.peb");
    config.putIfAbsent(CONFIG_REAUTH_TIMEOUT_MS, String.valueOf(TimeUnit.MILLISECONDS.convert(15, TimeUnit.MINUTES)));

    config.putIfAbsent(REMEMBER_ME_COOKIE_NAME, "__remember_me");
    config.putIfAbsent(REMEMBER_ME_COOKIE_AGE, String.valueOf(TimeUnit.SECONDS.convert(90, TimeUnit.DAYS)));
    config.putIfAbsent(REMEMBER_ME_COOKIE_SECURE, String.valueOf(false));
    config.putIfAbsent(REMEMBER_ME_COOKIE_HTTPONLY, String.valueOf(true));
    config.putIfAbsent(REMEMBER_ME_COOKIE_SLIDING_EXP, String.valueOf(true));

    config.putIfAbsent(CONFIG_CSP_NONCE_SIZE, String.valueOf(16));
    config.putIfAbsent(HEADER_CSP_REPORT_ONLY, String.valueOf(true));

    this.enableDebug = config.getBool(CONFIG_ENABLE_DEBUG);
    this.enableSession = config.getBool(CONFIG_ENABLE_SESSION);

    this.cspNonceSize = config.getInt(CONFIG_CSP_NONCE_SIZE);
    this.cspRptOnly = config.getBool(HEADER_CSP_REPORT_ONLY);

    this.viewDir = config.getString(CONFIG_VIEW_DIR);
    this.viewSuffixes = CollectionUtils.unmodifiableList(config.getStringArray(CONFIG_VIEW_SUFFIXES));
  }

  public boolean isEnableDebug() {
    this.initialize();
    return this.enableDebug;
  }

  public boolean isEnableSession() {
    this.initialize();
    return this.enableSession;
  }

  public int getCspNonceSize() {
    this.initialize();
    return this.cspNonceSize;
  }

  public boolean isCspRptOnly() {
    this.initialize();
    return this.cspRptOnly;
  }

  public String getViewDir() {
    this.initialize();
    return this.viewDir;
  }

  public List<String> getViewSuffixes() {
    this.initialize();
    return this.viewSuffixes;
  }

  public StringBuilder getViewBase() {
    return new StringBuilder(getViewDir().length() + 80).append(getViewDir());
  }

  public String getViewPath(String subPath) {
    return getViewBase().append(subPath).toString();
  }

  @Override
  public Iterator<String> getKeys() {
    this.initialize();
    return this.config.getKeys();
  }

  @Override
  public String getString(String key) {
    this.initialize();
    return this.config.getString(key);
  }

  @Override
  public String getString(String key, String ifNull) {
    this.initialize();
    return this.config.getString(key, ifNull);
  }

  @Override
  public String getStringReq(String key) {
    this.initialize();
    return this.config.getStringReq(key);
  }

  @Override
  public String[] getStringArray(String key) {
    this.initialize();
    return this.config.getStringArray(key);
  }

  @Override
  public String[] getStringArray(String key, String ifNullValues) {
    this.initialize();
    return this.config.getStringArray(key, ifNullValues);
  }

  @Override
  public boolean getBool(String key) throws BoolFormatException {
    this.initialize();
    return this.config.getBool(key);
  }

  @Override
  public boolean getBool(String key, boolean ifNullOrInvalid) {
    this.initialize();
    return this.config.getBool(key, ifNullOrInvalid);
  }

  @Override
  public int getInt(String key) throws NumberFormatException {
    this.initialize();
    return this.config.getInt(key);
  }

  @Override
  public int getInt(String key, int ifNullOrInvalid) {
    this.initialize();
    return this.config.getInt(key, ifNullOrInvalid);
  }

  @Override
  public long getLong(String key) throws NumberFormatException {
    this.initialize();
    return this.config.getLong(key);
  }

  @Override
  public long getLong(String key, long ifNullOrInvalid) {
    this.initialize();
    return this.config.getLong(key, ifNullOrInvalid);
  }

  @Override
  public double getDouble(String key) throws NumberFormatException {
    this.initialize();
    return this.config.getDouble(key);
  }

  @Override
  public double getDouble(String key, double ifNullOrInvalid) {
    this.initialize();
    return this.config.getDouble(key, ifNullOrInvalid);
  }

  @Override
  public BigDecimal getDecimalReq(String key) throws NumberFormatException {
    this.initialize();
    return this.config.getDecimalReq(key);
  }

  @Override
  public BigDecimal getDecimal(String key, double ifNullOrInvalid) {
    this.initialize();
    return this.config.getDecimal(key, ifNullOrInvalid);
  }

  @Override
  public <T> T getValue(String key, Function<String, T> converter) {
    this.initialize();
    return this.config.getValue(key, converter);
  }

  @Override
  public String resolve(String key) {
    this.initialize();
    return this.config.resolve(key);
  }

  @Override
  public String resolve(String key, Map<String, Object> parameters) {
    this.initialize();
    return this.config.resolve(key, parameters);
  }

  @Override
  public String resolve(String key, Object... parameters) {
    this.initialize();
    return this.config.resolve(key, parameters);
  }
}
