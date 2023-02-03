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

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.appslandia.common.base.StringWriter;
import com.appslandia.common.utils.URLEncoding;
import com.appslandia.common.utils.URLUtils;
import com.appslandia.plum.utils.ServletUtils;
import com.appslandia.plum.utils.XmlEscaper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@ApplicationScoped
public class ActionParser {

    @Inject
    protected AppConfig appConfig;

    @Inject
    protected ActionDescProvider actionDescProvider;

    @Inject
    protected LanguageProvider languageProvider;

    public ActionDesc parse(List<String> pathItems, Map<String, String> pathParamMap) {
	// /
	// /language
	if (pathItems.size() == 0) {
	    return this.actionDescProvider.getHomeDesc();
	}
	ActionDesc actionDesc = null;
	String controller = pathItems.get(0);

	// /controller
	if (pathItems.size() == 1) {
	    actionDesc = this.actionDescProvider.getActionDesc(controller, ServletUtils.ACTION_INDEX);
	    if ((actionDesc == null) || (actionDesc.getChildAction() != null)) {
		return null;
	    }
	    if (actionDesc.getPathParams().size() > 0) {
		return null;
	    }
	    return actionDesc;
	}
	// /controller/action(/parameter)*
	String action = pathItems.get(1);
	actionDesc = this.actionDescProvider.getActionDesc(controller, action);
	if ((actionDesc == null) || (actionDesc.getChildAction() != null)) {
	    return null;
	}

	// Remove controller/action
	pathItems.remove(0);
	pathItems.remove(0);

	List<PathParam> pathParams = actionDesc.getPathParams();
	if (pathItems.size() != pathParams.size()) {
	    return null;
	}

	// pathParamMap
	for (int i = 0; i < pathParams.size(); i++) {
	    PathParam pathParm = pathParams.get(i);
	    if (pathParm.getParamName() != null) {
		pathParamMap.put(pathParm.getParamName(), URLEncoding.decodePath(pathItems.get(i)));
	    } else {
		if (!parseSubParams(pathItems.get(i), pathParm.getSubParams(), pathParamMap)) {
		    return null;
		}
	    }
	}
	return actionDesc;
    }

    public String toActionUrl(HttpServletRequest request, String controller, String action, Map<String, Object> parameters, boolean absoluteUrl) throws IllegalArgumentException {
	ActionDesc actionDesc = this.actionDescProvider.getActionDesc(controller, action);
	if ((actionDesc == null) || (actionDesc.getChildAction() != null)) {
	    throw new IllegalArgumentException("Action is required (controller=" + controller + ", action=" + action + ")");
	}

	RequestContext requestContext = ServletUtils.getRequestContext(request);
	StringBuilder url = null;

	if (absoluteUrl) {
	    url = ServletUtils.absUrlBase(request);
	} else {
	    url = ServletUtils.newUrlBuilder();
	}

	// Context path
	url.append(request.getServletContext().getContextPath());

	// Language
	if (requestContext.isPathLanguage() || this.appConfig.getRequiredBool(AppConfig.CONFIG_REQUIRE_PATH_LANG)) {
	    url.append('/').append(requestContext.getLanguageId());
	}

	// Controller
	url.append('/').append(controller);

	// Path Parameters
	if (actionDesc.getPathParams().size() > 0) {
	    url.append('/').append(action);

	    if (parameters != null) {
		addPathParams(url, parameters, actionDesc.getPathParams());
	    } else {
		throw new IllegalArgumentException("Path parameters are required.");
	    }
	} else {
	    // Index
	    if (!ServletUtils.ACTION_INDEX.equalsIgnoreCase(action)) {
		url.append('/').append(action);
	    }
	}
	url.append('/');

	// Query Parameters
	if ((parameters != null) && (parameters.size() > actionDesc.getPathParamCount())) {
	    url.append('?');
	    addQueryParams(url, parameters, actionDesc.getPathParams());
	}
	return url.toString();
    }

    // pathItem: parameter(-parameter)+
    public static boolean parseSubParams(String pathItem, List<PathParam> subParams, Map<String, String> pathParamMap) {
	int nextIdx = 0;
	int startIdx = 0;
	while (true) {
	    // Last parameter
	    if (nextIdx == subParams.size() - 1) {
		if (startIdx >= pathItem.length()) {
		    return false;
		}
		String value = pathItem.substring(startIdx);
		if (value.isEmpty()) {
		    return false;
		}
		pathParamMap.put(subParams.get(nextIdx).getParamName(), URLEncoding.decodePath(value));
		return true;
	    }
	    // Not last parameter
	    int endIdx = pathItem.indexOf('-', startIdx);
	    if (endIdx < 0) {
		return false;
	    }
	    String value = pathItem.substring(startIdx, endIdx);
	    if (value.isEmpty()) {
		return false;
	    }
	    pathParamMap.put(subParams.get(nextIdx).getParamName(), URLEncoding.decodePath(value));

	    startIdx = endIdx + 1;
	    nextIdx++;
	}
    }

    public static void addPathParams(StringBuilder url, Map<String, Object> parameters, List<PathParam> pathParams) {
	for (PathParam pathParam : pathParams) {
	    if (pathParam.getParamName() != null) {
		Object value = parameters.get(pathParam.getParamName());
		if (value == null) {
		    throw new IllegalArgumentException("Path parameter is required (name=" + pathParam.getParamName() + ")");
		}
		url.append('/').append(URLEncoding.encodePath(value.toString()));
		continue;
	    }
	    // Sub Parameters
	    boolean isFirstSub = true;
	    for (PathParam subParam : pathParam.getSubParams()) {
		Object value = parameters.get(subParam.getParamName());
		if (value == null) {
		    throw new IllegalArgumentException("Path parameter is required (name=" + subParam.getParamName() + ")");
		}
		if (isFirstSub) {
		    url.append('/').append(URLEncoding.encodePath(value.toString()));
		    isFirstSub = false;
		} else {
		    url.append('-').append(URLEncoding.encodePath(value.toString()));
		}
	    }
	}
    }

    public static void addQueryParams(StringBuilder url, Map<String, Object> parameters, List<PathParam> pathParams) {
	boolean isFirstParam = true;

	for (Map.Entry<String, Object> param : parameters.entrySet()) {
	    if (pathParams.stream().anyMatch(p -> p.hasPathParam(param.getKey()))) {
		continue;
	    }
	    if (isFirstParam) {
		isFirstParam = false;
	    } else {
		url.append('&');
	    }
	    URLUtils.addQueryParam(url, param.getKey(), param.getValue());
	}
    }

    @SuppressWarnings("resource")
    public String toActionLink(HttpServletRequest request, String controller, String action, Map<String, Object> parameters, Map<String, Object> attributes, String labelText)
	    throws IllegalArgumentException {
	StringWriter out = new StringWriter(255);

	out.write("<a href=\"");
	out.write(toActionUrl(request, controller, action, parameters, false));
	out.write("\"");

	// Attributes
	if (attributes != null) {
	    for (Entry<String, Object> attr : attributes.entrySet()) {
		out.write(' ');
		out.write(attr.getKey());
		out.write("=\"");

		if (attr.getValue() != null) {
		    out.write(attr.getValue().toString());
		}
		out.write('"');
	    }
	}

	out.write('>');
	out.write(XmlEscaper.escapeXmlContent(labelText));
	out.write("</a>");

	return out.toString();
    }
}
