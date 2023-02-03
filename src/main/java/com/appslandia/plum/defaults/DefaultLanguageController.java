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

package com.appslandia.plum.defaults;

import com.appslandia.plum.base.ActionResult;
import com.appslandia.plum.base.AppConfig;
import com.appslandia.plum.base.Controller;
import com.appslandia.plum.base.HttpGet;
import com.appslandia.plum.base.HttpGetPost;
import com.appslandia.plum.base.LanguageProvider;
import com.appslandia.plum.base.PrefCookie;
import com.appslandia.plum.base.PrefCookieHandler;
import com.appslandia.plum.base.RequestAccessor;
import com.appslandia.plum.results.RedirectResult;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@ApplicationScoped
@Controller("language")
public class DefaultLanguageController {

    @Inject
    protected AppConfig appConfig;

    @Inject
    protected LanguageProvider languageProvider;

    @Inject
    protected PrefCookieHandler prefCookieHandler;

    @HttpGetPost
    public ActionResult changeto(String languageId, RequestAccessor request, HttpServletResponse response) throws Exception {
	request.assertNotNull(languageId);
	request.assertNotNull(this.languageProvider.getLanguage(languageId));

	this.prefCookieHandler.savePrefCookie(request, response, (prefCookie) -> {
	    prefCookie.set(PrefCookie.PARAM_LANGUAGE, languageId);
	});

	final String redirectUrl = request.getParameter("redirectUrl");

	if (redirectUrl != null) {
	    ServletUtils.sendRedirect(response, redirectUrl);
	    return ActionResult.EMPTY;

	} else {
	    return RedirectResult.ROOT;
	}
    }

    @HttpGet
    public void reset(RequestAccessor request, HttpServletResponse response) throws Exception {
	this.prefCookieHandler.savePrefCookie(request, response, (prefCookie) -> {
	    prefCookie.remove(PrefCookie.PARAM_LANGUAGE);
	});
    }
}
