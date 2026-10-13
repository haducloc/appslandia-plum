// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.defaults;

import java.util.Set;

import com.appslandia.common.base.CodeGroup;
import com.appslandia.common.cdi.CDIFactory;
import com.appslandia.common.cdi.CDIUtils;
import com.appslandia.plum.base.AppLogger;
import com.appslandia.plum.base.CodeGroupProvider;

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
public class DefaultCodeGroupProviderFactory implements CDIFactory<CodeGroupProvider> {

  @Inject
  protected AppLogger appLogger;

  @Inject
  protected BeanManager beanManager;

  @Produces
  @ApplicationScoped
  @Override
  public CodeGroupProvider produce() {
    final var impl = new CodeGroupProvider();

    CDIUtils.consumeSuppliers(beanManager, CodeGroup.class, (Set<Class<?>> value) -> {
      appLogger.info("Installing CodeGroup: {}", value);

      value.stream().forEach(clazz -> impl.addCodeClass(clazz));
    });
    return impl;
  }

  @Override
  public void dispose(@Disposes CodeGroupProvider impl) {
  }
}
