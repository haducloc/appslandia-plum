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

import com.appslandia.common.utils.Asserts;
import com.appslandia.plum.base.ActionResult;
import com.appslandia.plum.base.AppConfig;
import com.appslandia.plum.base.Controller;
import com.appslandia.plum.base.HttpGetPost;
import com.appslandia.plum.base.LanguageProvider;
import com.appslandia.plum.base.RequestContext;
import com.appslandia.plum.base.RequestWrapper;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@ApplicationScoped
@Controller("changeto")
public class LanguageChangeController {

  @Inject
  protected AppConfig appConfig;

  @Inject
  protected LanguageProvider languageProvider;

  @HttpGetPost
  public ActionResult index(String languageId, RequestWrapper request, HttpServletResponse response) throws Exception {
    Asserts.isTrue(this.languageProvider.isMultiLanguages());

    request.assertNotNull(languageId);
    request.assertNotNull(this.languageProvider.getLanguage(languageId));

    final String redirectUrl = request.getParameter("redirectUrl");

    if (redirectUrl != null) {
      ServletUtils.sendRedirect(response,
          this.appConfig.isEnableSession() ? response.encodeRedirectURL(redirectUrl) : redirectUrl);
      return ActionResult.EMPTY;

    } else {
      return new ActionResult() {

        @Override
        public void execute(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext)
            throws Exception {

          // URL
          StringBuilder url = new StringBuilder();
          url.append(request.getServletContext().getContextPath());

          // Language
          url.append('/').append(languageId);
          url.append('/');

          response.sendRedirect(
              LanguageChangeController.this.appConfig.isEnableSession() ? response.encodeRedirectURL(url.toString())
                  : url.toString());
        }
      };
    }
  }
}