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

package com.appslandia.plum.pebble;

import java.util.ArrayList;
import java.util.List;

import com.appslandia.plum.base.AppConfig;
import com.appslandia.plum.base.LanguageProvider;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.loader.Loader;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

/**
 *
 * @author Loc Ha
 *
 */
public abstract class PebbleTemplateProvider {

  protected PebbleEngine pebbleEngine;

  @Inject
  protected AppConfig appConfig;

  @Inject
  protected LanguageProvider languageProvider;

  @Inject
  protected Instance<PebbleExtensionProvider> extensionProvider;

  @PostConstruct
  protected void initialize() {
    var builder = new PebbleEngine.Builder();
    builder.loader(getLoader());

    // builder.autoEscaping(this.appConfig.getBool("pebble.auto_escaping", true));
    builder.autoEscaping(true);
    builder.cacheActive(appConfig.getBool("pebble.cache_active", true));

    builder.strictVariables(appConfig.getBool("pebble.strict_variables", false));
    builder.greedyMatchMethod(appConfig.getBool("pebble.greedy_match_method", false));

    builder.newLineTrimming(appConfig.getBool("pebble.new_line_trimming", true));
    builder.allowOverrideCoreOperators(appConfig.getBool("pebble.allow_override_core_operators", false));

    var escapingStrategy = appConfig.getString("pebble.default_escaping_strategy");
    if (escapingStrategy != null) {
      builder.defaultEscapingStrategy(escapingStrategy);
    }

    builder.defaultLocale(languageProvider.getDefaultLanguage().getLocale());

    // DefaultPebbleExtensionProvider
    builder.extension(new DefaultPebbleExtensionProvider());

    // extensionProviders
    List<PebbleExtensionProvider> extensionProviders = new ArrayList<>();
    extensionProvider.forEach(extensionProvider -> {

      builder.extension(extensionProvider);
      extensionProviders.add(extensionProvider);
    });

    preBuild(builder);
    var engine = builder.build();

    // Destroy extensionProvider
    extensionProviders.forEach(extensionProvider -> this.extensionProvider.destroy(extensionProvider));
    pebbleEngine = engine;
  }

  public PebbleTemplate getTemplate(String name) {
    return pebbleEngine.getTemplate(name);
  }

  protected abstract Loader<?> getLoader();

  protected void preBuild(PebbleEngine.Builder builder) {
  }
}
