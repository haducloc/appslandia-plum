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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.appslandia.common.base.InitializeObject;
import com.appslandia.common.base.Language;
import com.appslandia.common.utils.AssertUtils;
import com.appslandia.plum.utils.ServletUtils;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class LanguageProvider extends InitializeObject {

	private Map<String, Language> languageMap = new LinkedHashMap<>();

	private String languageIds;
	private Language defaultLanguage;

	@Override
	protected void init() throws Exception {
		AssertUtils.assertTrue(!this.languageMap.isEmpty(), "No language provided.");

		if (this.defaultLanguage == null) {
			this.defaultLanguage = this.languageMap.values().iterator().next();
		}

		this.languageIds = String.join(", ", this.languageMap.values().stream().map(l -> l.getId()).collect(Collectors.toList()));
		this.languageMap = Collections.unmodifiableMap(this.languageMap);
	}

	public Language getDefaultLanguage() {
		this.initialize();
		return this.defaultLanguage;
	}

	public Language getLanguage(String languageId) {
		this.initialize();
		AssertUtils.assertNotNull(languageId);
		return this.languageMap.get(languageId.toLowerCase(Locale.ENGLISH));
	}

	public Language getLanguage(HttpServletRequest request) {
		this.initialize();

		String matchedLang = ServletUtils.parseLanguage(request, languageMap.keySet());
		if (matchedLang != null) {
			return this.languageMap.get(matchedLang);
		}
		return this.defaultLanguage;
	}

	public String getLanguageIds() {
		this.initialize();
		return this.languageIds;
	}

	public Collection<Language> getLanguages() {
		this.initialize();
		return this.languageMap.values();
	}

	public LanguageProvider addLanguage(Language impl) {
		this.assertNotInitialized();
		AssertUtils.assertTrue(!this.languageMap.containsKey(impl.getId()), "language already exists (language=" + impl.getId() + ")");
		this.languageMap.put(impl.getId(), impl);
		return this;
	}

	public LanguageProvider addDefault(Language impl) {
		AssertUtils.assertNull(this.defaultLanguage);
		this.defaultLanguage = impl;
		return addLanguage(impl);
	}
}
