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

package com.appslandia.plum.pebble;

import com.appslandia.plum.base.AppConfig;
import com.appslandia.plum.base.LanguageProvider;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.loader.Servlet5Loader;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public abstract class PebbleTemplateProvider {

    protected PebbleEngine pebbleEngine;

    @Inject
    protected ServletContext servletContext;

    @Inject
    protected AppConfig appConfig;

    @Inject
    protected LanguageProvider languageProvider;

    @PostConstruct
    protected void initialize() {
	PebbleEngine.Builder builder = new PebbleEngine.Builder();
	builder.loader(new Servlet5Loader(this.servletContext));

	builder.autoEscaping(this.appConfig.getBool("pebble.auto_escaping", true));
	builder.cacheActive(this.appConfig.getBool("pebble.cache_active", true));

	builder.strictVariables(this.appConfig.getBool("pebble.strict_variables", false));
	builder.greedyMatchMethod(this.appConfig.getBool("pebble.greedy_match_method", false));

	builder.newLineTrimming(this.appConfig.getBool("pebble.new_line_trimming", true));
	builder.allowOverrideCoreOperators(this.appConfig.getBool("pebble.allow_override_core_operators", false));

	String escapingStrategy = this.appConfig.getString("pebble.default_escaping_strategy");
	if (escapingStrategy != null) {
	    builder.defaultEscapingStrategy(escapingStrategy);
	}

	builder.defaultLocale(this.languageProvider.getDefaultLanguage().getLocale());
	// builder.methodAccessValidator();

	registerExtensions(builder);
	this.pebbleEngine = builder.build();
    }

    public PebbleTemplate getTemplate(String name) {
	return this.pebbleEngine.getTemplate(name);
    }

    protected void registerExtensions(PebbleEngine.Builder builder) {
    }
}