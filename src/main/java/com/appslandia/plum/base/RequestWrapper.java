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

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.appslandia.common.utils.ArrayUtils;
import com.appslandia.common.utils.Asserts;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class RequestWrapper extends HttpServletRequestWrapper {

    final Map<String, String[]> mergedParamMap;

    public RequestWrapper(HttpServletRequest request, Map<String, String> pathParamMap) {
	super(request);
	this.mergedParamMap = !pathParamMap.isEmpty() ? this.mergeParameters(pathParamMap) : null;
    }

    @Override
    public HttpSession getSession() {
	return getSession(true);
    }

    @Override
    public HttpSession getSession(boolean create) {
	AppConfig config = ServletUtils.getAppScoped(this.getServletContext(), AppConfig.class);
	Asserts.isTrue(config.isEnableSession(), "Http session is disabled.");

	return super.getSession(create);
    }

    @Override
    public String getParameter(String name) {
	if (this.mergedParamMap == null) {
	    return super.getParameter(name);
	}
	String[] values = this.mergedParamMap.get(name);
	return (values != null) ? values[0] : null;
    }

    @Override
    public String[] getParameterValues(String name) {
	if (this.mergedParamMap == null) {
	    return super.getParameterValues(name);
	}
	String[] values = this.mergedParamMap.get(name);
	return (values != null) ? ArrayUtils.copy(values) : null;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
	if (this.mergedParamMap == null) {
	    return super.getParameterMap();
	}
	return this.mergedParamMap;
    }

    @Override
    public Enumeration<String> getParameterNames() {
	if (this.mergedParamMap == null) {
	    return super.getParameterNames();
	}
	return Collections.enumeration(this.mergedParamMap.keySet());
    }

    protected Map<String, String[]> mergeParameters(Map<String, String> pathParamMap) {
	final Map<String, String[]> merged = new HashMap<>();

	for (Entry<String, String> param : pathParamMap.entrySet()) {
	    merged.put(param.getKey(), new String[] { param.getValue() });
	}
	for (Entry<String, String[]> param : this.getRequest().getParameterMap().entrySet()) {
	    String[] values = merged.get(param.getKey());
	    if (values == null) {
		merged.put(param.getKey(), param.getValue());
	    } else {
		merged.put(param.getKey(), ArrayUtils.append(values, param.getValue()));
	    }
	}
	return Collections.unmodifiableMap(merged);
    }

    @Override
    public String toString() {
	return "[" + getClass().getSimpleName() + "] " + getRequest();
    }
}
