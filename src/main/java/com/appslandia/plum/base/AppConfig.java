// The MIT License (MIT)
// Copyright © 2015 AppsLandia. All rights reserved.

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
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.appslandia.common.base.BoolFormatException;
import com.appslandia.common.base.Config;
import com.appslandia.common.base.InitializeObject;
import com.appslandia.common.base.SimpleConfig;
import com.appslandia.common.utils.Asserts;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class AppConfig extends InitializeObject implements Config {

  public static final String CONFIG_DEFAULT_MODULE = "config.default_module";

  public static final String CONFIG_ENABLE_DEBUG = "config.enable_debug";
  public static final String CONFIG_ENABLE_SESSION = "config.enable_session";

  public static final String CONFIG_NONCE_SIZE = "config.nouce_size";
  public static final String CONFIG_DISABLE_COMPRESSION = "config.disable_compression";

  public static final String CONFIG_ENABLE_PREF_COOKIE = "config.enable_pref_cookie";
  public static final String CONFIG_ENABLE_JSON_PRETTY = "config.enable_json_pretty";
  public static final String CONFIG_ENABLE_PATH_LANG = "config.enable_path_lang";
  public static final String CONFIG_ENABLE_HEADER_POLICIES = "config.enable_header_policies";

  public static final String CONFIG_JSP_DIR = "config.jsp_dir";
  public static final String CONFIG_ACCESS_JSP_DIRECTLY = "config.access_jsp_directly";

  public static final String CONFIG_RESOURCE_NAMES = "config.resource_names";
  public static final String CONFIG_X_FORWARDED_PORTS = "config.x_forwarded_ports";
  public static final String CONFIG_REAUTH_TIMEOUT_MS = "config.reauth_timeout_ms";

  public static final String REMEMBER_ME_COOKIE_NAME = "remember_me.cookie_name";
  public static final String REMEMBER_ME_COOKIE_AGE = "remember_me.cookie_age";
  public static final String REMEMBER_ME_COOKIE_SECURE = "remember_me.cookie_secure";
  public static final String REMEMBER_ME_COOKIE_HTTPONLY = "remember_me.cookie_httponly";
  public static final String REMEMBER_ME_COOKIE_SLIDING = "remember_me.cookie_sliding";

  public static final String HEADER_STRICT_TRANSPORT_SECURITY = "header.strict_transport_security";
  public static final String HEADER_X_CONTENT_TYPE_OPTIONS = "header.x_content_type_options";
  public static final String HEADER_X_FRAME_OPTIONS = "header.x_frame_options";
  public static final String HEADER_X_XSS_PROTECTION = "header.x_xss_protection";
  public static final String HEADER_CONTENT_SECURITY_POLICY = "header.content_security_policy";
  public static final String HEADER_REFERRER_POLICY = "header.referrer_policy";

  public static final String HEADER_REPORT_TO = "header.report_to";
  public static final String HEADER_REPORTING_ENDPOINTS = "header.reporting_endpoints";
  public static final String HEADER_CSP_REPORT_ONLY = "header.csp_report_only";
  public static final String HEADER_VARY = "header.vary";

  public static final String HEADER_CROSS_ORIGIN_EMBEDDER_POLICY = "header.cross_origin_embedder_policy";
  public static final String HEADER_CROSS_ORIGIN_OPENER_POLICY = "header.cross_origin_opener_policy";
  public static final String HEADER_CROSS_ORIGIN_RESOURCE_POLICY = "header.cross_origin_resource_policy";

  public static final String HEADER_PERMISSIONS_POLICY = "header.permissions_policy";

  protected SimpleConfig config;

  private boolean enableDebug;
  private boolean enableSession;
  private String jspDir;

  public AppConfig() {
  }

  public AppConfig(SimpleConfig config) {
    this.config = config;
  }

  @Override
  protected void init() throws Exception {
    Asserts.notNull(this.config, "config is required.");

    this.config.putIfAbsent(CONFIG_DEFAULT_MODULE, Modules.DEFAULT);
    this.config.putIfAbsent(CONFIG_ENABLE_DEBUG, String.valueOf(false));

    this.config.putIfAbsent(CONFIG_ENABLE_SESSION, String.valueOf(true));
    this.config.putIfAbsent(CONFIG_NONCE_SIZE, String.valueOf(16));

    this.config.putIfAbsent(CONFIG_DISABLE_COMPRESSION, String.valueOf(true));

    this.config.putIfAbsent(CONFIG_ENABLE_PREF_COOKIE, String.valueOf(true));
    this.config.putIfAbsent(CONFIG_ENABLE_JSON_PRETTY, String.valueOf(true));
    this.config.putIfAbsent(CONFIG_ENABLE_PATH_LANG, String.valueOf(false));

    this.config.putIfAbsent(CONFIG_JSP_DIR, "/WEB-INF/jsp");
    this.config.putIfAbsent(CONFIG_ACCESS_JSP_DIRECTLY, String.valueOf(false));

    this.config.putIfAbsent(CONFIG_REAUTH_TIMEOUT_MS,
        String.valueOf(TimeUnit.MILLISECONDS.convert(10, TimeUnit.MINUTES)));

    this.config.putIfAbsent(REMEMBER_ME_COOKIE_NAME, "JREMEMBERMEID");
    this.config.putIfAbsent(REMEMBER_ME_COOKIE_AGE, String.valueOf(TimeUnit.SECONDS.convert(30, TimeUnit.DAYS)));
    this.config.putIfAbsent(REMEMBER_ME_COOKIE_SECURE, String.valueOf(false));
    this.config.putIfAbsent(REMEMBER_ME_COOKIE_HTTPONLY, String.valueOf(true));
    this.config.putIfAbsent(REMEMBER_ME_COOKIE_SLIDING, String.valueOf(true));

    this.config.putIfAbsent(HEADER_CSP_REPORT_ONLY, String.valueOf(true));

    this.enableDebug = this.config.getBool(CONFIG_ENABLE_DEBUG);
    this.enableSession = this.config.getBool(CONFIG_ENABLE_SESSION);
    this.jspDir = this.config.getString(CONFIG_JSP_DIR);
  }

  public boolean isEnableDebug() {
    this.initialize();
    return this.enableDebug;
  }

  public boolean isEnableSession() {
    this.initialize();
    return this.enableSession;
  }

  public String getJspDir() {
    this.initialize();
    return this.jspDir;
  }

  public StringBuilder getJspBase() {
    return new StringBuilder(getJspDir().length() + 80).append(getJspDir());
  }

  public String getJspPath(String path) {
    return getJspBase().append(path).toString();
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
  public boolean getBool(String key, boolean ifNullOrInvalid) {
    this.initialize();
    return this.config.getBool(key, ifNullOrInvalid);
  }

  @Override
  public boolean getBool(String key) throws BoolFormatException {
    this.initialize();
    return this.config.getBool(key);
  }

  @Override
  public Boolean getBoolOpt(String key) throws BoolFormatException {
    this.initialize();
    return this.config.getBoolOpt(key);
  }

  @Override
  public int getInt(String key, int ifNullOrInvalid) {
    this.initialize();
    return this.config.getInt(key, ifNullOrInvalid);
  }

  @Override
  public int getInt(String key) throws NumberFormatException {
    this.initialize();
    return this.config.getInt(key);
  }

  @Override
  public Integer getIntOpt(String key) throws NumberFormatException {
    this.initialize();
    return this.config.getIntOpt(key);
  }

  @Override
  public long getLong(String key, long ifNullOrInvalid) {
    this.initialize();
    return this.config.getLong(key, ifNullOrInvalid);
  }

  @Override
  public long getLong(String key) throws NumberFormatException {
    this.initialize();
    return this.config.getLong(key);
  }

  @Override
  public Long getLongOpt(String key) throws NumberFormatException {
    this.initialize();
    return this.config.getLongOpt(key);
  }

  @Override
  public double getDouble(String key, double ifNullOrInvalid) {
    this.initialize();
    return this.config.getDouble(key, ifNullOrInvalid);
  }

  @Override
  public double getDouble(String key) throws NumberFormatException {
    this.initialize();
    return this.config.getDouble(key);
  }

  @Override
  public Double getDoubleOpt(String key) throws NumberFormatException {
    this.initialize();
    return this.config.getDoubleOpt(key);
  }

  @Override
  public BigDecimal getDecimal(String key, double ifNullOrInvalid) {
    this.initialize();
    return this.config.getDecimal(key, ifNullOrInvalid);
  }

  @Override
  public BigDecimal getDecimalReq(String key) throws NumberFormatException {
    this.initialize();
    return this.config.getDecimalReq(key);
  }

  @Override
  public BigDecimal getDecimalOpt(String key) throws NumberFormatException {
    this.initialize();
    return this.config.getDecimalOpt(key);
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
