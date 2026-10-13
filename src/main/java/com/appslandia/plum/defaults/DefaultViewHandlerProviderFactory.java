// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.defaults;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Map.Entry;

import com.appslandia.common.cdi.CDIFactory;
import com.appslandia.common.cdi.CDIUtils;
import com.appslandia.common.utils.Asserts;
import com.appslandia.plum.base.AppLogger;
import com.appslandia.plum.base.ViewHandler;
import com.appslandia.plum.base.ViewHandlerProvider;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class DefaultViewHandlerProviderFactory implements CDIFactory<ViewHandlerProvider> {

  @Inject
  protected AppLogger appLogger;

  @Inject
  protected BeanManager beanManager;

  @Produces
  @ApplicationScoped
  @Override
  public ViewHandlerProvider produce() {
    final var impl = new ViewHandlerProvider();

    CDIUtils.<ViewHandler>consumeReferences(beanManager, ViewHandler.class, new Annotation[] { Any.Literal.INSTANCE },
        (bc, br) -> {
          var named = bc.getDeclaredAnnotation(Named.class);
          var inst = br.get();

          if (named != null) {
            Asserts.isTrue(br.isApplicationScoped() || br.isSingleton());

            appLogger.info("Installing ViewHandler {}: {}", named.value(), inst);
            impl.registerViewHandler(named.value(), inst);
          } else {
            appLogger.warn("No @Named. Skipping: {}", inst);
          }
        });

    CDIUtils.consumeSuppliers(beanManager, ViewHandler.class, (Map<String, ViewHandler> value) -> {
      appLogger.info("Installing ViewHandlers: {}", value);

      for (Entry<String, ViewHandler> handler : value.entrySet()) {
        impl.registerViewHandler(handler.getKey(), handler.getValue());
      }
    });

    return impl;
  }

  @Override
  public void dispose(@Disposes ViewHandlerProvider impl) {
  }
}
