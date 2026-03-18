// The MIT License (MIT)
// Copyright © 2015 Loc Ha

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

import com.appslandia.plum.utils.ServletUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class LanguageController extends ControllerBase {

  @Inject
  protected AppConfig appConfig;

  @Inject
  protected LanguageProvider languageProvider;

  @Inject
  protected PrefCookieHandler prefCookieHandler;

  @PathParams("/{languageId}")
  @HttpGetPost
  public void changeto(RequestWrapper request, HttpServletResponse response, RequestContext requestContext,
      String languageId) throws Exception {
    request.assertNotFound(languageProvider.isMultiLanguages());
    request.assertNotFound(languageProvider.getLanguage(languageId) != null);

    // PrefCookie
    var prefLanguage = request.getPrefCookie().getString(PrefCookie.PREF_LANGUAGE_ID);
    if (!languageId.equals(prefLanguage)) {

      var newPref = request.getPrefCookie().clone();
      newPref.put(PrefCookie.PREF_LANGUAGE_ID, languageId);

      prefCookieHandler.savePrefCookie(request, response, newPref);
    }

    var returnUrl = request.getParameter(ServletUtils.PARAM_RETURN_URL);
    if (returnUrl == null) {
      returnUrl = request.getContextPath() + "/" + languageId;
    }
    response.sendRedirect(appConfig.isEnableSession() ? response.encodeRedirectURL(returnUrl) : returnUrl);
  }
}
