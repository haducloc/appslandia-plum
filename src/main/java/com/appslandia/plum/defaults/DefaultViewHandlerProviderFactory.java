// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.defaults;

import java.util.Map;
import java.util.Map.Entry;

import com.appslandia.common.base.MappedID;
import com.appslandia.common.cdi.BeanInstances;
import com.appslandia.common.cdi.CDIFactory;
import com.appslandia.common.cdi.CDIUtils;
import com.appslandia.common.utils.ObjectUtils;
import com.appslandia.common.utils.ReflectionUtils;
import com.appslandia.plum.base.AppLogger;
import com.appslandia.plum.base.ViewHandler;
import com.appslandia.plum.base.ViewHandlerProvider;

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
public class DefaultViewHandlerProviderFactory implements CDIFactory<ViewHandlerProvider> {

  @Inject
  protected AppLogger appLogger;

  @Inject
  protected BeanManager beanManager;

  final BeanInstances beanInstances = new BeanInstances();

  @Produces
  @ApplicationScoped
  @Override
  public ViewHandlerProvider produce() {
    final var impl = new ViewHandlerProvider();

    CDIUtils.scanReferences(beanManager, ViewHandler.class, ReflectionUtils.EMPTY_ANNOTATIONS, (beanClass, inst) -> {
      var mappedID = beanClass.getDeclaredAnnotation(MappedID.class);
      if (mappedID != null) {

        appLogger.info("Installing ViewHandler: ${0}", inst.get());
        impl.registerViewHandler(mappedID.value(), inst.get());
        beanInstances.add(inst);
      } else {
        appLogger.warn("No @MappedID. Skipping: ${0}", inst.get());
        inst.destroy();
      }
    });

    CDIUtils.consumeSuppliers(beanManager, ViewHandler.class, (sup) -> {
      Map<String, ViewHandler> handlers = ObjectUtils.cast(sup.get());

      for (Entry<String, ViewHandler> handler : handlers.entrySet()) {
        impl.registerViewHandler(handler.getKey(), handler.getValue());
      }
    });

    return impl;
  }

  @Override
  public void dispose(@Disposes ViewHandlerProvider impl) {
  }

  @PreDestroy
  public void destroy() {
    beanInstances.destroy((ex) -> appLogger.error(ex));
  }
}
