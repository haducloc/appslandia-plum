// Licensed under the MIT License.
// See LICENSE file in the project root for details.

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
  public void changeto(HttpRequestFacade request, HttpServletResponse response, RequestContext requestContext,
      String languageId) throws Exception {

    var validUse = languageProvider.isMultiLanguages() && languageProvider.getLanguage(languageId) != null;
    request.assertNotFound(validUse, "/language/changeto is not found.");

    // PrefCookie
    var prefLanguage = request.getPrefCookie().getString(PrefCookie.PREF_LANGUAGE_ID);
    if (!languageId.equals(prefLanguage)) {

      var pref = request.getPrefCookie().copyWith(map -> {
        map.put(PrefCookie.PREF_LANGUAGE_ID, languageId);
      });

      prefCookieHandler.savePrefCookie(request, response, pref);
    }

    var returnUrl = request.getParameter(ServletUtils.PARAM_RETURN_URL);
    if (returnUrl == null) {
      returnUrl = request.getContextPath() + "/" + languageId;
    }

    var targetUrl = appConfig.isEnableSession() ? response.encodeRedirectURL(returnUrl) : returnUrl;
    response.sendRedirect(targetUrl);
  }
}
