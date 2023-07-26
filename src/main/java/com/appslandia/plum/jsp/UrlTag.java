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

package com.appslandia.plum.jsp;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.URLEncoding;
import com.appslandia.common.utils.URLUtils;
import com.appslandia.common.utils.XmlEscaper;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.DynamicAttributes;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@Tag(name = "url")
public class UrlTag extends TagBase implements DynamicAttributes {

    protected String baseUri;
    protected Map<String, Object> _params;
    protected boolean fmtUri;
    protected boolean escXml;

    @Override
    public void setDynamicAttribute(String uri, String name, Object value) throws JspException {
	if (this._params == null) {
	    this._params = new LinkedHashMap<>();
	}
	this._params.put(name, value);
    }

    @Override
    public void doTag() throws JspException, IOException {
	Asserts.notNull(this._params, "No parameter provided.");
	StringBuilder url = null;

	if (this.fmtUri) {
	    url = formatUri(this.baseUri, this._params);
	} else {
	    url = new StringBuilder(this.baseUri.length() + this._params.size() * 16);
	    url.append(this.baseUri);
	}

	// Query String
	boolean firstParam = true;
	for (Map.Entry<String, Object> param : this._params.entrySet()) {
	    if (!firstParam) {
		url.append('&');
	    } else {
		url.append('?');
		firstParam = false;
	    }
	    URLUtils.addQueryParam(url, param.getKey(), param.getValue());
	}
	if (this.escXml) {
	    XmlEscaper.escapeXml(this.pageContext.getOut(), url.toString());
	} else {
	    this.pageContext.getOut().write(url.toString());
	}
    }

    @Attribute(required = true, rtexprvalue = true)
    public void setBaseUri(String baseUri) {
	this.baseUri = baseUri;
    }

    @Attribute(required = false, rtexprvalue = false)
    public void setFmtUri(boolean fmtUri) {
	this.fmtUri = fmtUri;
    }

    @Attribute(required = false, rtexprvalue = false)
    public void setEscXml(boolean escXml) {
	this.escXml = escXml;
    }

    private static final Pattern PATH_PARAMS_PATTERN = Pattern.compile("\\{[^}]+?}");

    static StringBuilder formatUri(String baseUri, Map<String, Object> parameters) {
	Matcher matcher = PATH_PARAMS_PATTERN.matcher(baseUri);
	StringBuilder sb = new StringBuilder(baseUri.length() + ((parameters != null) ? 16 * parameters.size() : 0));

	int prevEnd = 0;
	while (matcher.find()) {
	    // Non parameter
	    if (prevEnd == 0) {
		sb.append(baseUri.substring(0, matcher.start()));
	    } else {
		sb.append(baseUri.substring(prevEnd, matcher.start()));
	    }

	    // {parameter}
	    String parameterGroup = matcher.group();
	    String parameterName = parameterGroup.substring(1, parameterGroup.length() - 1);

	    Object parameterVal = parameters.get(parameterName);
	    Asserts.notNull(parameterVal);

	    sb.append(URLEncoding.encodePath(parameterVal.toString()));
	    parameters.remove(parameterName);

	    prevEnd = matcher.end();
	}
	if (prevEnd < baseUri.length()) {
	    sb.append(baseUri.substring(prevEnd));
	}
	return sb;
    }
}
