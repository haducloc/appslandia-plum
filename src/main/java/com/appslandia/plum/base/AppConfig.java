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

import java.util.Map;
import java.util.concurrent.TimeUnit;

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

    public static final String CONFIG_ENABLE_SESSION = "config.enable_session";
    public static final String CONFIG_ENABLE_DEBUG = "config.enable_debug";

    public static final String CONFIG_DISABLE_GZIP = "config.disable_gzip";
    public static final String CONFIG_ENABLE_CORS = "config.enable_cors";

    public static final String CONFIG_ENABLE_AUTHORIZE = "config.enable_authorize";
    public static final String CONFIG_ENABLE_JSON_ERROR = "config.enable_json_error";

    public static final String CONFIG_REMME_COOKIE_NAME = "config.remme_cookie_name";
    public static final String CONFIG_REMME_COOKIE_AGE = "config.remme_cookie_age";
    public static final String CONFIG_REMME_COOKIE_SECURE = "config.remme_cookie_secure";
    public static final String CONFIG_REMME_COOKIE_HTTPONLY = "config.remme_cookie_httponly";
    public static final String CONFIG_REMME_COOKIE_SLIDING = "config.remme_cookie_sliding";

    public static final String CONFIG_REQUIRE_PATH_LANG = "config.require_path_lang";

    public static final String CONFIG_VIEW_PATH = "config.view_path";
    public static final String CONFIG_REAUTH_TIMEOUT_MS = "config.reauth_timeout_ms";
    public static final String CONFIG_ASYNC_TIMEOUT_MS = "config.async_timeout_ms";

    public static final String CONFIG_DIRECT_JSP_ACCESS = "config.direct_jsp_access";
    public static final String CONFIG_PARSE_BROWSER_FEATURES = "config.parse_browser_features";
    public static final String CONFIG_PARSE_PREF_COOKIE = "config.parse_pref_cookie";

    public static final String CONFIG_RESOURCE_NAMES = "config.resource_names";
    public static final String CONFIG_HEADER_POLICIES = "config.header_policies";
    public static final String CONFIG_X_FORWARDED_PORTS = "config.x_forwarded_ports";

    protected ConfigMap config;

    private boolean enableDebug;
    private boolean enableSession;
    private String viewPath;

    public AppConfig() {
    }

    public AppConfig(ConfigMap config) {
	this.config = config;
    }

    @Override
    protected void init() throws Exception {
	Asserts.notNull(this.config, "config is required.");

	this.config.putIfAbsent(CONFIG_DEFAULT_MODULE, Modules.DEFAULT);

	this.config.putIfAbsent(CONFIG_ENABLE_SESSION, String.valueOf(true));
	this.config.putIfAbsent(CONFIG_ENABLE_DEBUG, String.valueOf(false));

	this.config.putIfAbsent(CONFIG_DISABLE_GZIP, String.valueOf(true));
	this.config.putIfAbsent(CONFIG_ENABLE_CORS, String.valueOf(false));

	this.config.putIfAbsent(CONFIG_ENABLE_AUTHORIZE, String.valueOf(false));
	this.config.putIfAbsent(CONFIG_ENABLE_JSON_ERROR, String.valueOf(false));

	this.config.putIfAbsent(CONFIG_REMME_COOKIE_NAME, "JREMEMBERMEID");
	this.config.putIfAbsent(CONFIG_REMME_COOKIE_AGE, String.valueOf(TimeUnit.SECONDS.convert(14, TimeUnit.DAYS)));
	this.config.putIfAbsent(CONFIG_REMME_COOKIE_SECURE, String.valueOf(false));
	this.config.putIfAbsent(CONFIG_REMME_COOKIE_HTTPONLY, String.valueOf(true));
	this.config.putIfAbsent(CONFIG_REMME_COOKIE_SLIDING, String.valueOf(true));

	this.config.putIfAbsent(CONFIG_REQUIRE_PATH_LANG, String.valueOf(false));

	this.config.putIfAbsent(CONFIG_VIEW_PATH, "/WEB-INF/views");
	this.config.putIfAbsent(CONFIG_REAUTH_TIMEOUT_MS, String.valueOf(TimeUnit.MILLISECONDS.convert(10, TimeUnit.MINUTES)));
	this.config.putIfAbsent(CONFIG_ASYNC_TIMEOUT_MS, String.valueOf(TimeUnit.MILLISECONDS.convert(180, TimeUnit.SECONDS)));

	this.config.putIfAbsent(CONFIG_DIRECT_JSP_ACCESS, String.valueOf(false));
	this.config.putIfAbsent(CONFIG_PARSE_BROWSER_FEATURES, String.valueOf(false));
	this.config.putIfAbsent(CONFIG_PARSE_PREF_COOKIE, String.valueOf(true));

	this.enableDebug = this.config.getRequiredBool(CONFIG_ENABLE_DEBUG);
	this.enableSession = this.config.getRequiredBool(CONFIG_ENABLE_SESSION);
	this.viewPath = this.config.getRequiredString(CONFIG_VIEW_PATH);
    }

    public boolean isEnableDebug() {
	this.initialize();
	return this.enableDebug;
    }

    public boolean isEnableSession() {
	this.initialize();
	return this.enableSession;
    }

    public String getViewPath() {
	this.initialize();
	return this.viewPath;
    }

    public StringBuilder getViewPathBase() {
	return new StringBuilder(getViewPath().length() + 80).append(getViewPath());
    }

    @Override
    public String getString(String key) {
	this.initialize();
	return this.config.getString(key);
    }

    @Override
    public String getString(String key, String defaultValue) {
	this.initialize();
	return this.config.getString(key, defaultValue);
    }

    @Override
    public String getRequiredString(String key) {
	this.initialize();
	return this.config.getRequiredString(key);
    }

    @Override
    public String[] getStringArray(String key) {
	this.initialize();
	return this.config.getStringArray(key);
    }

    @Override
    public String getFormatted(String key) {
	this.initialize();
	return this.config.getFormatted(key);
    }

    @Override
    public String getRequiredFormatted(String key) {
	this.initialize();
	return this.config.getRequiredFormatted(key);
    }

    @Override
    public String getFormatted(String key, Map<String, Object> parameters) {
	this.initialize();
	return this.config.getFormatted(key, parameters);
    }

    @Override
    public String getRequiredFormatted(String key, Map<String, Object> parameters) {
	this.initialize();
	return this.config.getRequiredFormatted(key, parameters);
    }

    @Override
    public String getFormatted(String key, Object... parameters) {
	this.initialize();
	return this.config.getFormatted(key, parameters);
    }

    @Override
    public String getRequiredFormatted(String key, Object... parameters) {
	this.initialize();
	return this.config.getRequiredFormatted(key, parameters);
    }

    @Override
    public boolean getBool(String key, boolean defaultValue) {
	this.initialize();
	return this.config.getBool(key, defaultValue);
    }

    @Override
    public boolean getRequiredBool(String key) {
	this.initialize();
	return this.config.getRequiredBool(key);
    }

    @Override
    public int getInt(String key, int defaultValue) {
	this.initialize();
	return this.config.getInt(key, defaultValue);
    }

    @Override
    public int getRequiredInt(String key) {
	this.initialize();
	return this.config.getRequiredInt(key);
    }

    @Override
    public long getLong(String key, long defaultValue) {
	this.initialize();
	return this.config.getLong(key, defaultValue);
    }

    @Override
    public long getRequiredLong(String key) {
	this.initialize();
	return this.config.getRequiredLong(key);
    }

    @Override
    public double getDouble(String key, double defaultValue) {
	this.initialize();
	return this.config.getDouble(key, defaultValue);
    }

    @Override
    public double getRequiredDouble(String key) {
	this.initialize();
	return this.config.getRequiredDouble(key);
    }
}
