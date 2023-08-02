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

package com.appslandia.plum.pebble;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.appslandia.common.base.MapAccessor;
import com.appslandia.common.utils.ArrayUtils;
import com.appslandia.plum.base.AppConfig;
import com.appslandia.plum.utils.ServletUtils;

import io.pebbletemplates.pebble.template.PebbleTemplate;
import jakarta.el.ELProcessor;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class PebbleUtils {

    public static final String VARIABLE_REQUEST = "request";
    public static final String VARIABLE_RESPONSE = "response";
    public static final String VARIABLE_REQUEST_CONTEXT = "ctx";
    public static final String VARIABLE_EL_PROCESSOR = "elProcessor";

    public static String getPebbleDir(ServletContext servletContext) {
	AppConfig appConfig = ServletUtils.getAppScoped(servletContext, AppConfig.class);
	return appConfig.getString("pebble.template_dir", "/WEB-INF/pebble");
    }

    public static void executePebble(HttpServletRequest request, HttpServletResponse response, Writer out, String pebblePath, Map<String, Object> model, Locale locale)
	    throws IOException {
	// Variables
	Map<String, Object> variables = (model != null) ? new HashMap<>(model) : new HashMap<>();
	registerRequestBasedVariables(request, variables);

	variables.put(VARIABLE_REQUEST, request);
	variables.put(VARIABLE_RESPONSE, response);
	variables.put(VARIABLE_REQUEST_CONTEXT, ServletUtils.getRequestContext(request));

	// ELProcessor
	ELProcessor elProcessor = new ELProcessor();
	for (Map.Entry<String, Object> variable : variables.entrySet()) {
	    elProcessor.defineBean(variable.getKey(), variable.getValue());
	}
	variables.put(VARIABLE_EL_PROCESSOR, elProcessor);

	// PebbleTemplateProvider
	PebbleTemplateProvider templateProvider = ServletUtils.getAppScoped(request.getServletContext(), PebbleTemplateProvider.class);
	PebbleTemplate template = templateProvider.getTemplate(pebblePath);

	// Evaluate template
	template.evaluate(out, variables, locale);
	out.flush();
    }

    public static void registerRequestBasedVariables(HttpServletRequest request, Map<String, Object> variables) {
	// Maps
	variables.put("requestScope", new MapAccessor<String, Object>() {

	    @Override
	    public Object get(Object key) {
		return request.getAttribute((String) key);
	    }
	});

	variables.put("sessionScope", new MapAccessor<String, Object>() {

	    @Override
	    public Object get(Object key) {
		HttpSession session = request.getSession(false);
		return (session != null) ? session.getAttribute((String) key) : null;
	    }
	});

	variables.put("applicationScope", new MapAccessor<String, Object>() {

	    @Override
	    public Object get(Object key) {
		return request.getServletContext().getAttribute((String) key);
	    }
	});

	variables.put("param", new MapAccessor<String, String>() {

	    @Override
	    public String get(Object key) {
		return request.getParameter((String) key);
	    }
	});

	variables.put("paramValues", new MapAccessor<String, String[]>() {

	    @Override
	    public String[] get(Object key) {
		return request.getParameterValues((String) key);
	    }
	});

	variables.put("header", new MapAccessor<String, String>() {

	    @Override
	    public String get(Object key) {
		return request.getHeader((String) key);
	    }
	});

	variables.put("headerValues", new MapAccessor<String, String[]>() {

	    @Override
	    public String[] get(Object key) {
		return ArrayUtils.toArray(request.getHeaders((String) key), String.class);
	    }
	});

	variables.put("initParam", new MapAccessor<String, String>() {

	    @Override
	    public String get(Object key) {
		return request.getServletContext().getInitParameter((String) key);
	    }
	});

	variables.put("cookie", new MapAccessor<String, Cookie>() {

	    @Override
	    public Cookie get(Object key) {
		if (request.getCookies() == null) {
		    return null;
		}
		return Arrays.stream(request.getCookies()).filter(c -> c.getName().equalsIgnoreCase((String) key)).findFirst().orElse(null);
	    }
	});
    }
}
