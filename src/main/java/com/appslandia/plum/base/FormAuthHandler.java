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

import java.util.HashMap;
import java.util.Map;

import com.appslandia.common.utils.ObjectUtils;
import com.appslandia.common.utils.URLEncoding;
import com.appslandia.common.utils.URLUtils;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.inject.Inject;
import jakarta.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import jakarta.security.enterprise.credential.Credential;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public abstract class FormAuthHandler implements AuthHandler {

    @Inject
    protected AppConfig appConfig;

    @Override
    public String getAuthMethod() {
	return "Form";
    }

    @Override
    public Credential parseCredential(HttpServletRequest request) {
	return null;
    }

    @Override
    public boolean isRememberMe(HttpMessageContext httpMessageContext) {
	if (!httpMessageContext.isAuthenticationRequest()) {
	    return false;
	}
	return httpMessageContext.getAuthParameters().isRememberMe();
    }

    @Override
    public void askAuthenticate(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext) throws Exception {
	String returnUrl = getUriAndQuery(request);

	StringBuilder url = ServletUtils.getLoginUrl(request);
	url.append('?').append(ServletUtils.PARAM_RETURN_URL).append('=').append(URLEncoding.encodeParam(returnUrl));

	// Append tempDataId
	String tempDataId = request.getParameter(TempDataManager.PARAM_TEMP_DATA_ID);
	if (tempDataId != null) {
	    url.append('&').append(TempDataManager.PARAM_TEMP_DATA_ID).append('=').append(tempDataId);
	}
	response.sendRedirect(this.appConfig.isEnableSession() ? response.encodeRedirectURL(url.toString()) : url.toString());
    }

    static String getUriAndQuery(HttpServletRequest request) {
	if (request.getParameter(TempDataManager.PARAM_TEMP_DATA_ID) == null) {
	    return ServletUtils.appendUriQuery(request, ServletUtils.newUrlBuilder()).toString();
	}

	// Remove tempDataId
	Map<String, String[]> copyParams = new HashMap<>(request.getParameterMap());
	copyParams.remove(TempDataManager.PARAM_TEMP_DATA_ID);

	if (copyParams.isEmpty()) {
	    return request.getRequestURI();
	}
	return request.getRequestURI() + "?" + URLUtils.toQueryParams(ObjectUtils.cast(copyParams));
    }
}
