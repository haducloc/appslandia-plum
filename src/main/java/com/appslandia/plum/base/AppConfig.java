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
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.appslandia.common.base.BoolFormatException;
import com.appslandia.common.base.Config;
import com.appslandia.common.base.ConfigMap;
import com.appslandia.common.base.InitializeObject;
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
  public static final String CONFIG_ENABLE_AUTHORIZE = "config.enable_authorize";
  public static final String CONFIG_ENABLE_CORS = "config.enable_cors";

  public static final String CONFIG_DISABLE_GZIP = "config.disable_gzip";
  public static final String CONFIG_ENABLE_PREF_COOKIE = "config.enable_pref_cookie";

  public static final String CONFIG_ENABLE_JSON_ERROR = "config.enable_json_error";
  public static final String CONFIG_ENABLE_JSON_PRETTY = "config.enable_json_pretty";

  public static final String CONFIG_ENABLE_PATH_LANG = "config.enable_path_lang";
  public static final String CONFIG_ENABLE_HEADER_POLICIES = "config.enable_header_policies";

  public static final String CONFIG_JSP_DIR = "config.jsp_dir";
  public static final String CONFIG_ACCESS_JSP_DIRECTLY = "config.access_jsp_directly";

  public static final String CONFIG_RESOURCE_NAMES = "config.resource_names";
  public static final String CONFIG_X_FORWARDED_PORTS = "config.x_forwarded_ports";

  public static final String CONFIG_INPUT_TYPE_BROWSER_FEATURE = "config.input_type_browser_feature";
  public static final String CONFIG_REAUTH_TIMEOUT_MS = "config.reauth_timeout_ms";

  public static final String REMEMBER_ME_COOKIE_NAME = "remember_me.cookie_name";
  public static final String REMEMBER_ME_COOKIE_AGE = "remember_me.cookie_age";
  public static final String REMEMBER_ME_COOKIE_SECURE = "remember_me.cookie_secure";
  public static final String REMEMBER_ME_COOKIE_HTTPONLY = "remember_me.cookie_httponly";
  public static final String REMEMBER_ME_COOKIE_SLIDING = "remember_me.cookie_sliding";

  public static final String HEADER_POLICIES_STRICT_TRANSPORT_SECURITY = "header_policies.strict_transport_security";
  public static final String HEADER_POLICIES_X_CONTENT_TYPE_OPTIONS = "header_policies.x_content_type_options";
  public static final String HEADER_POLICIES_X_FRAME_OPTIONS = "header_policies.x_frame_options";
  public static final String HEADER_POLICIES_X_XSS_PROTECTION = "header_policies.x_xss_protection";
  public static final String HEADER_POLICIES_CONTENT_SECURITY_POLICY = "header_policies.content_security_policy";
  public static final String HEADER_POLICIES_CONTENT_SECURITY_POLICY_REPORT_ONLY = "header_policies.content_security_policy_report_only";
  public static final String HEADER_POLICIES_REPORT_TO = "header_policies.report_to";
  public static final String HEADER_POLICIES_REFERRER_POLICY = "header_policies.referrer_policy";

  protected ConfigMap config;

  private boolean enableDebug;
  private boolean enableSession;
  private String jspDir;

  public AppConfig() {
  }

  public AppConfig(ConfigMap config) {
    this.config = config;
  }

  @Override
  protected void init() throws Exception {
    Asserts.notNull(this.config, "config is required.");

    this.config.putIfAbsent(CONFIG_DEFAULT_MODULE, Modules.DEFAULT);
    this.config.putIfAbsent(CONFIG_ENABLE_DEBUG, String.valueOf(false));

    this.config.putIfAbsent(CONFIG_ENABLE_SESSION, String.valueOf(true));
    this.config.putIfAbsent(CONFIG_ENABLE_AUTHORIZE, String.valueOf(false));
    this.config.putIfAbsent(CONFIG_ENABLE_CORS, String.valueOf(false));

    this.config.putIfAbsent(CONFIG_DISABLE_GZIP, String.valueOf(true));
    this.config.putIfAbsent(CONFIG_ENABLE_PREF_COOKIE, String.valueOf(true));

    this.config.putIfAbsent(CONFIG_ENABLE_JSON_ERROR, String.valueOf(false));
    this.config.putIfAbsent(CONFIG_ENABLE_JSON_PRETTY, String.valueOf(true));

    this.config.putIfAbsent(CONFIG_ENABLE_PATH_LANG, String.valueOf(false));

    this.config.putIfAbsent(CONFIG_JSP_DIR, "/WEB-INF/jsp");
    this.config.putIfAbsent(CONFIG_ACCESS_JSP_DIRECTLY, String.valueOf(false));

    this.config.putIfAbsent(CONFIG_INPUT_TYPE_BROWSER_FEATURE, String.valueOf(false));

    this.config.putIfAbsent(CONFIG_REAUTH_TIMEOUT_MS,
        String.valueOf(TimeUnit.MILLISECONDS.convert(10, TimeUnit.MINUTES)));

    this.config.putIfAbsent(REMEMBER_ME_COOKIE_NAME, "JREMEMBERMEID");
    this.config.putIfAbsent(REMEMBER_ME_COOKIE_AGE, String.valueOf(TimeUnit.SECONDS.convert(30, TimeUnit.DAYS)));
    this.config.putIfAbsent(REMEMBER_ME_COOKIE_SECURE, String.valueOf(false));
    this.config.putIfAbsent(REMEMBER_ME_COOKIE_HTTPONLY, String.valueOf(true));
    this.config.putIfAbsent(REMEMBER_ME_COOKIE_SLIDING, String.valueOf(true));

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
  public String getString(String key) {
    this.initialize();
    return this.config.getString(key);
  }

  @Override
  public String getString(String key, String defaultValIfInvalid) {
    this.initialize();
    return this.config.getString(key, defaultValIfInvalid);
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
  public boolean getBool(String key, boolean defaultValIfInvalid) {
    this.initialize();
    return this.config.getBool(key, defaultValIfInvalid);
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
  public int getInt(String key, int defaultValIfInvalid) {
    this.initialize();
    return this.config.getInt(key, defaultValIfInvalid);
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
  public long getLong(String key, long defaultValIfInvalid) {
    this.initialize();
    return this.config.getLong(key, defaultValIfInvalid);
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
  public double getDouble(String key, double defaultValIfInvalid) {
    this.initialize();
    return this.config.getDouble(key, defaultValIfInvalid);
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
  public BigDecimal getDecimal(String key, double defaultValIfInvalid) {
    this.initialize();
    return this.config.getDecimal(key, defaultValIfInvalid);
  }

  @Override
  public BigDecimal getDecimal(String key) throws NumberFormatException {
    this.initialize();
    return this.config.getDecimal(key);
  }

  @Override
  public BigDecimal getDecimalReq(String key) throws NumberFormatException {
    this.initialize();
    return this.config.getDecimalReq(key);
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
