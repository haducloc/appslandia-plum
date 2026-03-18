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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import com.appslandia.common.base.InitializingObject;
import com.appslandia.common.base.Language;
import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.Asserts;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.servlet.http.HttpServletRequest;

/**
 *
 * @author Loc Ha
 *
 */
public class LanguageProvider extends InitializingObject {

  private Map<String, Language> languageMap = new LinkedHashMap<>();
  private Language defaultLanguage;

  @Override
  protected void init() throws Exception {
    Arguments.hasEntries(languageMap, "No language provided.");

    if (defaultLanguage == null) {
      defaultLanguage = languageMap.values().iterator().next();
    }
    languageMap = Collections.unmodifiableMap(languageMap);
  }

  public Language getDefaultLanguage() {
    initialize();
    return defaultLanguage;
  }

  public Language getLanguage(String languageId) {
    initialize();
    return languageMap.get(languageId.toLowerCase(Locale.ENGLISH));
  }

  public Language getBestLanguage(HttpServletRequest request) {
    initialize();

    var matchedLang = ServletUtils.getBestLanguage(request, languageMap.keySet());
    if (matchedLang != null) {
      return languageMap.get(matchedLang);
    }
    return defaultLanguage;
  }

  public boolean isMultiLanguages() {
    initialize();
    return languageMap.size() > 1;
  }

  public Collection<Language> getLanguages() {
    initialize();
    return languageMap.values();
  }

  public LanguageProvider addLanguage(Language impl) {
    assertNotInitialized();
    languageMap.put(impl.getId(), impl);
    return this;
  }

  public LanguageProvider addDefault(Language impl) {
    Asserts.isNull(defaultLanguage);
    defaultLanguage = impl;
    return addLanguage(impl);
  }
}
