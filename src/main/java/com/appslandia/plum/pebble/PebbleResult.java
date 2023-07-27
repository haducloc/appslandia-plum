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

import java.util.HashMap;
import java.util.Map;

import com.appslandia.plum.base.AppConfig;
import com.appslandia.plum.base.RequestContext;
import com.appslandia.plum.results.ViewResult;
import com.appslandia.plum.utils.ServletUtils;

import io.pebbletemplates.pebble.template.PebbleTemplate;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class PebbleResult extends ViewResult {

    public static final String VARIABLE_REQUEST_CONTEXT = "requestContext";
    public static final String VARIABLE_REQUEST = "request";

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
    protected String getViewDir(AppConfig appConfig) {
	return appConfig.getString("config.pebble_dir", "/WEB-INF/pebble");
    }

    @Override
    protected void doExecute(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext) throws Exception {
	if (this.characterEncoding != null) {
	    response.setCharacterEncoding(this.characterEncoding);
	}

	// Variables
	Map<String, Object> viewModel = (this.model != null) ? new HashMap<>(this.model) : new HashMap<>();

	viewModel.put(VARIABLE_REQUEST, requestContext);
	viewModel.put(VARIABLE_REQUEST, request);

	PebbleTemplateProvider templateProvider = ServletUtils.getAppScoped(request, PebbleTemplateProvider.class);
	PebbleTemplate template = templateProvider.getTemplate(this.resolvedPath);

	template.evaluate(response.getWriter(), viewModel, requestContext.getLanguage().getLocale());
	response.getWriter().flush();
    }

    public static final PebbleResult DEFAULT = new PebbleResult();
}
