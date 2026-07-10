// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.appslandia.common.base.InitializingObject;
import com.appslandia.common.base.Language;
import com.appslandia.common.utils.Arguments;

import jakarta.servlet.http.HttpServletRequest;

/**
 *
 * @author Loc Ha
 *
 */
public class LanguageProvider extends InitializingObject {

  private Map<String, Language> languageMap = new LinkedHashMap<>();
  private List<Language> languages;

  @Override
  protected void init() throws Exception {
    Arguments.hasEntries(languageMap, "No language provided.");

    languages = languageMap.values().stream().toList();
    languageMap = Collections.unmodifiableMap(languageMap);
  }

  public Language getLanguage(String languageId) {
    initialize();
    return languageMap.get(languageId.toLowerCase(Locale.ENGLISH));
  }

  public Language resolveLanguage(HttpServletRequest request) {
    initialize();

    var resolved = resolveLanguage(request, this.languages);
    if (resolved != null) {
      return resolved;
    }
    return getDefaultLanguage();
  }

  public Language getDefaultLanguage() {
    initialize();
    return this.languages.get(0);
  }

  public boolean isMultiLanguages() {
    initialize();
    return languages.size() > 1;
  }

  public List<Language> getLanguages() {
    initialize();
    return this.languages;
  }

  public LanguageProvider registerLanguages(Language[] languages) {
    assertNotInitialized();

    for (Language language : languages) {
      languageMap.put(language.getId(), language);
    }
    return this;
  }

  static Language resolveLanguage(HttpServletRequest request, List<Language> sLanguages) {
    var rLocales = Collections.list(request.getLocales());

    // Exact locale match (e.g. en-GB -> en-GB)
    for (var rLocale : rLocales) {
      for (var sLanguage : sLanguages) {
        var sLocale = sLanguage.getLocale();

        if (!sLocale.getCountry().isEmpty() && rLocale.getLanguage().equals(sLocale.getLanguage())
            && rLocale.getCountry().equals(sLocale.getCountry())) {
          return sLanguage;
        }
      }
    }

    // Generic language fallback (e.g. en-GB -> en)
    for (var rLocale : rLocales) {
      for (var sLanguage : sLanguages) {
        var sLocale = sLanguage.getLocale();

        if (sLocale.getCountry().isEmpty() && rLocale.getLanguage().equals(sLocale.getLanguage())) {
          return sLanguage;
        }
      }
    }

    // Same-language regional fallback (e.g. en-AU -> en-US)
    for (var rLocale : rLocales) {
      for (var sLanguage : sLanguages) {
        var sLocale = sLanguage.getLocale();

        if (!sLocale.getCountry().isEmpty() && rLocale.getLanguage().equals(sLocale.getLanguage())) {
          return sLanguage;
        }
      }
    }

    return null;
  }
}
