// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.defaults;

import com.appslandia.common.cdi.BeanInstances;
import com.appslandia.common.cdi.CDIFactory;
import com.appslandia.common.cdi.CDIUtils;
import com.appslandia.common.utils.ReflectionUtils;
import com.appslandia.plum.base.AppLogger;
import com.appslandia.plum.base.CompressHandler;
import com.appslandia.plum.base.CompressHandlerProvider;

import jakarta.annotation.PreDestroy;
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
public class DefaultCompressHandlerProviderFactory implements CDIFactory<CompressHandlerProvider> {

  @Inject
  protected AppLogger appLogger;

  @Inject
  protected BeanManager beanManager;

  final BeanInstances beanInstances = new BeanInstances();

  @Produces
  @ApplicationScoped
  @Override
  public CompressHandlerProvider produce() {
    final var impl = new CompressHandlerProvider();

    CDIUtils.scanReferences(beanManager, CompressHandler.class, ReflectionUtils.EMPTY_ANNOTATIONS,
        (beanClass, inst) -> {
          appLogger.info("Installing CompressHandler: ${0}", inst.get());

          impl.registerHandler(inst.get());
          beanInstances.add(inst);
        });
    return impl;
  }

  @Override
  public void dispose(@Disposes CompressHandlerProvider impl) {
  }

  @PreDestroy
  public void destroy() {
    beanInstances.destroy((ex) -> appLogger.error(ex));
  }
}
