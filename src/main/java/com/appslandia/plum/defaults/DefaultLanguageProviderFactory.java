// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.defaults;

import com.appslandia.common.base.Out;
import com.appslandia.common.cdi.CDIFactory;
import com.appslandia.common.cdi.CDIUtils;
import com.appslandia.common.utils.Asserts;
import com.appslandia.plum.base.LanguageConfig;
import com.appslandia.plum.base.LanguageProvider;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class DefaultLanguageProviderFactory implements CDIFactory<LanguageProvider> {

  @Inject
  protected BeanManager beanManager;

  @Produces
  @ApplicationScoped
  @Override
  public LanguageProvider produce() {
    final var impl = new LanguageProvider();

    var config = new Out<>(false);
    CDIUtils.consumeReference(beanManager, LanguageConfig.class, (sup) -> {

      config.value = true;
      impl.registerLanguages(sup.getLanguages());
    });

    Asserts.isTrue(config.value, "No LanguageConfig implementation found.");
    return impl;
  }

  @Override
  public void dispose(@Disposes LanguageProvider impl) {
  }
}
