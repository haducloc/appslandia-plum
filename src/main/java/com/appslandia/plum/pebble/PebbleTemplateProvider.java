// Licensed under the MIT License.
// See LICENSE file in the project root for details.

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
