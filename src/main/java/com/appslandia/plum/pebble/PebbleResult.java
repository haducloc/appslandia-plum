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

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.appslandia.common.base.MapAccessor;
import com.appslandia.plum.base.AppConfig;
import com.appslandia.plum.base.RequestContext;
import com.appslandia.plum.results.ViewResult;
import com.appslandia.plum.utils.ServletUtils;

import io.pebbletemplates.pebble.template.PebbleTemplate;
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
public class PebbleResult extends ViewResult {

    private String characterEncoding;

    public PebbleResult() {
	super();
    }

    public PebbleResult(String path) {
	super(path);
    }

    public PebbleResult(String path, Map<String, Object> model) {
	super(path, model);
    }

    public PebbleResult characterEncoding(String encoding) {
	this.characterEncoding = encoding;
	return this;
    }

    @Override
    public String getSuffix() {
	return ".peb";
    }

    @Override
    protected String getViewDir(ServletContext servletContext) {
	return PebbleUtils.getPebbleDir(servletContext);
    }

    @Override
    protected void doExecute(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext) throws Exception {
	AppConfig appConfig = ServletUtils.getAppScoped(request.getServletContext(), AppConfig.class);

	if (this.characterEncoding != null) {
	    response.setCharacterEncoding(this.characterEncoding);
	} else {
	    response.setCharacterEncoding(appConfig.getString("pebble.character_encoding", StandardCharsets.UTF_8.name()));
	}

	// Variables
	Map<String, Object> variables = (this.model != null) ? new HashMap<>(this.model) : new HashMap<>();
	registerVariables(request, variables);

	variables.put("request", request);
	variables.put("ctx", requestContext);

	// PebbleTemplateProvider
	PebbleTemplateProvider templateProvider = ServletUtils.getAppScoped(request.getServletContext(), PebbleTemplateProvider.class);
	PebbleTemplate template = templateProvider.getTemplate(this.resolvedPath);

	template.evaluate(response.getWriter(), variables, requestContext.getLanguage().getLocale());
	response.getWriter().flush();
    }

    public static final PebbleResult DEFAULT = new PebbleResult();

    protected void registerVariables(HttpServletRequest request, Map<String, Object> variables) {
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
		return PebbleUtils.getHeaderValues(request, (String) key);
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
