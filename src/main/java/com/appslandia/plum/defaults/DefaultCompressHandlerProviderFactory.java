// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.defaults;

import com.appslandia.common.cdi.CDIFactory;
import com.appslandia.common.cdi.CDIUtils;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.ReflectionUtils;
import com.appslandia.plum.base.AppLogger;
import com.appslandia.plum.base.CompressHandler;
import com.appslandia.plum.base.CompressHandlerProvider;

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

  @Produces
  @ApplicationScoped
  @Override
  public CompressHandlerProvider produce() {
    final var impl = new CompressHandlerProvider();

    CDIUtils.<CompressHandler>consumeReferences(beanManager, CompressHandler.class, ReflectionUtils.EMPTY_ANNOTATIONS,
        (bc, br) -> {
          var inst = br.get();

          appLogger.info("Installing CompressHandler: {}", inst);
          Asserts.isTrue(br.isApplicationScoped() || br.isSingleton());

          impl.registerHandler(inst);
        });
    return impl;
  }

  @Override
  public void dispose(@Disposes CompressHandlerProvider impl) {
  }
}
