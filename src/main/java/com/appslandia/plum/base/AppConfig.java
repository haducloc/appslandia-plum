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
import com.appslandia.common.utils.AssertUtils;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class AppConfig extends InitializeObject implements Config {

    public static final String CONFIG_MODULE_NAME = "module.module_name";

    public static final String CONFIG_ENABLE_SESSION = "module.enable_session";
    public static final String CONFIG_ENABLE_DEBUG = "module.enable_debug";
    public static final String CONFIG_RESOURCE_NAMES = "module.resource_names";
    public static final String CONFIG_HEADER_POLICIES = "module.header_policies";

    public static final String CONFIG_DISABLE_GZIP = "module.disable_gzip";
    public static final String CONFIG_ENABLE_CORS = "module.enable_cors";
    public static final String CONFIG_CONTENT_LANG = "module.content_lang";
    public static final String CONFIG_ENABLE_ASYNC = "module.enable_async";

    public static final String CONFIG_ENABLE_AUTHORIZE = "module.enable_authorize";
    public static final String CONFIG_ENABLE_JSON_ERROR = "module.enable_json_error";

    public static final String CONFIG_REMME_COOKIE_NAME = "module.remme_cookie_name";
    public static final String CONFIG_REMME_COOKIE_AGE = "module.remme_cookie_age";
    public static final String CONFIG_REMME_COOKIE_SECURE = "module.remme_cookie_secure";
    public static final String CONFIG_REMME_COOKIE_HTTPONLY = "module.remme_cookie_httponly";
    public static final String CONFIG_REMME_COOKIE_SLIDING = "module.remme_cookie_sliding";

    public static final String CONFIG_PATH_LANG = "module.path_lang";
    public static final String CONFIG_PREF_LANG = "module.pref_lang";

    public static final String CONFIG_VIEW_PATH = "module.view_path";
    public static final String CONFIG_REAUTH_TIMEOUT_MS = "module.reauth_timeout_ms";
    public static final String CONFIG_ASYNC_TIMEOUT_MS = "module.async_timeout_ms";

    public static final String CONFIG_TEST_BROWSER_FEATURES = "module.test_browser_features";
    public static final String CONFIG_X_FORWARDED_PORTS = "module.x_forwarded_ports";
    public static final String CONFIG_DIRECT_ACCESS_JSP = "module.direct_access_jsp";

    protected ConfigMap config;

    private boolean enableSession;
    private boolean enableDebug;
    private boolean enableJsonError;

    private boolean testBrowserFeatures;
    private boolean pathLang;
    private boolean prefLang;

    private String viewPath;
    private long reauthTimeoutMs;
    private long asyncTimeoutMs;

    public AppConfig() {
    }

    public AppConfig(ConfigMap config) {
	this.config = config;
    }

    @Override
    protected void init() throws Exception {
	AssertUtils.assertNotNull(this.config, "config is required.");

	this.config.putIfAbsent(CONFIG_MODULE_NAME, Modules.DEFAULT);

	this.config.putIfAbsent(CONFIG_ENABLE_SESSION, String.valueOf(false));
	this.config.putIfAbsent(CONFIG_ENABLE_DEBUG, String.valueOf(false));

	this.config.putIfAbsent(CONFIG_DISABLE_GZIP, String.valueOf(true));
	this.config.putIfAbsent(CONFIG_ENABLE_CORS, String.valueOf(false));
	this.config.putIfAbsent(CONFIG_CONTENT_LANG, String.valueOf(false));

	this.config.putIfAbsent(CONFIG_ENABLE_ASYNC, String.valueOf(false));
	this.config.putIfAbsent(CONFIG_ASYNC_TIMEOUT_MS, String.valueOf(TimeUnit.MILLISECONDS.convert(30, TimeUnit.SECONDS)));

	this.config.putIfAbsent(CONFIG_ENABLE_AUTHORIZE, String.valueOf(false));
	this.config.putIfAbsent(CONFIG_ENABLE_JSON_ERROR, String.valueOf(false));

	this.config.putIfAbsent(CONFIG_REMME_COOKIE_NAME, "JREMEMBERMEID");
	this.config.putIfAbsent(CONFIG_REMME_COOKIE_AGE, String.valueOf(TimeUnit.SECONDS.convert(14, TimeUnit.DAYS)));
	this.config.putIfAbsent(CONFIG_REMME_COOKIE_SECURE, String.valueOf(false));
	this.config.putIfAbsent(CONFIG_REMME_COOKIE_HTTPONLY, String.valueOf(true));
	this.config.putIfAbsent(CONFIG_REMME_COOKIE_SLIDING, String.valueOf(false));

	this.config.putIfAbsent(CONFIG_PATH_LANG, String.valueOf(false));
	this.config.putIfAbsent(CONFIG_PREF_LANG, String.valueOf(true));

	this.config.putIfAbsent(CONFIG_VIEW_PATH, "/WEB-INF/views");
	this.config.putIfAbsent(CONFIG_REAUTH_TIMEOUT_MS, String.valueOf(TimeUnit.MILLISECONDS.convert(10, TimeUnit.MINUTES)));

	this.config.putIfAbsent(CONFIG_TEST_BROWSER_FEATURES, String.valueOf(false));
	this.config.putIfAbsent(CONFIG_DIRECT_ACCESS_JSP, String.valueOf(false));

	this.enableSession = this.config.getRequiredBool(CONFIG_ENABLE_SESSION);
	this.enableDebug = this.config.getRequiredBool(CONFIG_ENABLE_DEBUG);
	this.enableJsonError = this.config.getRequiredBool(CONFIG_ENABLE_JSON_ERROR);

	this.testBrowserFeatures = this.config.getRequiredBool(CONFIG_TEST_BROWSER_FEATURES);
	this.pathLang = this.config.getRequiredBool(CONFIG_PATH_LANG);
	this.prefLang = this.config.getRequiredBool(CONFIG_PREF_LANG);

	this.viewPath = this.config.getRequiredString(CONFIG_VIEW_PATH);
	this.reauthTimeoutMs = this.config.getRequiredLong(CONFIG_REAUTH_TIMEOUT_MS);
	this.asyncTimeoutMs = this.config.getRequiredLong(CONFIG_ASYNC_TIMEOUT_MS);
    }

    public String getModule() {
	this.initialize();
	return this.config.getString(CONFIG_MODULE_NAME);
    }

    public boolean isEnableSession() {
	this.initialize();
	return this.enableSession;
    }

    public boolean isEnableDebug() {
	this.initialize();
	return this.enableDebug;
    }

    public boolean isEnableJsonError() {
	this.initialize();
	return this.enableJsonError;
    }

    public boolean isTestBrowserFeatures() {
	this.initialize();
	return this.testBrowserFeatures;
    }

    public boolean isPathLang() {
	this.initialize();
	return this.pathLang;
    }

    public boolean isPrefLang() {
	this.initialize();
	return this.prefLang;
    }

    public String getViewPath() {
	this.initialize();
	return this.viewPath;
    }

    public long getReauthTimeoutMs() {
	this.initialize();
	return this.reauthTimeoutMs;
    }

    public long getAsyncTimeoutMs() {
	this.initialize();
	return this.asyncTimeoutMs;
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
    public String getRequiredString(String key) throws IllegalArgumentException {
	this.initialize();
	return this.config.getRequiredString(key);
    }

    @Override
    public String[] getStringArray(String key) {
	this.initialize();
	return this.config.getStringArray(key);
    }

    @Override
    public String getFormatted(String key) throws IllegalArgumentException {
	this.initialize();
	return this.config.getFormatted(key);
    }

    @Override
    public String getRequiredFormatted(String key) throws IllegalArgumentException {
	this.initialize();
	return this.config.getRequiredFormatted(key);
    }

    @Override
    public String getFormatted(String key, Map<String, Object> parameters) throws IllegalArgumentException {
	this.initialize();
	return this.config.getFormatted(key, parameters);
    }

    @Override
    public String getRequiredFormatted(String key, Map<String, Object> parameters) throws IllegalArgumentException {
	this.initialize();
	return this.config.getRequiredFormatted(key, parameters);
    }

    @Override
    public String getFormatted(String key, Object... parameters) throws IllegalArgumentException {
	this.initialize();
	return this.config.getFormatted(key, parameters);
    }

    @Override
    public String getRequiredFormatted(String key, Object... parameters) throws IllegalArgumentException {
	this.initialize();
	return this.config.getRequiredFormatted(key, parameters);
    }

    @Override
    public boolean getBool(String key, boolean defaultValue) {
	this.initialize();
	return this.config.getBool(key, defaultValue);
    }

    @Override
    public boolean getRequiredBool(String key) throws IllegalArgumentException {
	this.initialize();
	return this.config.getRequiredBool(key);
    }

    @Override
    public int getInt(String key, int defaultValue) {
	this.initialize();
	return this.config.getInt(key, defaultValue);
    }

    @Override
    public int getRequiredInt(String key) throws IllegalArgumentException {
	this.initialize();
	return this.config.getRequiredInt(key);
    }

    @Override
    public long getLong(String key, long defaultValue) {
	this.initialize();
	return this.config.getLong(key, defaultValue);
    }

    @Override
    public long getRequiredLong(String key) throws IllegalArgumentException {
	this.initialize();
	return this.config.getRequiredLong(key);
    }

    @Override
    public double getDouble(String key, double defaultValue) {
	this.initialize();
	return this.config.getDouble(key, defaultValue);
    }

    @Override
    public double getRequiredDouble(String key) throws IllegalArgumentException {
	this.initialize();
	return this.config.getRequiredDouble(key);
    }
}
