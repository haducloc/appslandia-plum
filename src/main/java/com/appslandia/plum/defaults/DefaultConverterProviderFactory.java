// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.defaults;

import java.util.Map;
import java.util.Map.Entry;

import com.appslandia.common.base.MappedID;
import com.appslandia.common.cdi.BeanInstances;
import com.appslandia.common.cdi.CDIFactory;
import com.appslandia.common.cdi.CDIUtils;
import com.appslandia.common.converters.Converter;
import com.appslandia.common.converters.ConverterProvider;
import com.appslandia.common.utils.ObjectUtils;
import com.appslandia.common.utils.ReflectionUtils;
import com.appslandia.plum.base.AppLogger;

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
public class DefaultConverterProviderFactory implements CDIFactory<ConverterProvider> {

  @Inject
  protected AppLogger appLogger;

  @Inject
  protected BeanManager beanManager;

  final BeanInstances beanInstances = new BeanInstances();

  @Produces
  @ApplicationScoped
  @Override
  public ConverterProvider produce() {
    final var impl = new ConverterProvider();

    CDIUtils.scanReferences(beanManager, Converter.class, ReflectionUtils.EMPTY_ANNOTATIONS, (beanClass, inst) -> {
      var mappedID = beanClass.getDeclaredAnnotation(MappedID.class);
      if (mappedID != null) {

        appLogger.info("Installing Converter: ${0}", inst.get());
        impl.registerConverter(mappedID.value(), inst.get());
        beanInstances.add(inst);
      } else {
        appLogger.warn("No @MappedID. Skipping: ${0}", inst.get());
        inst.destroy();
      }
    });

    CDIUtils.consumeSuppliers(beanManager, Converter.class, (sup) -> {
      Map<String, Converter<?>> converters = ObjectUtils.cast(sup.get());

      for (Entry<String, Converter<?>> converter : converters.entrySet()) {
        impl.registerConverter(converter.getKey(), converter.getValue());
      }
    });
    return impl;
  }

  @Override
  public void dispose(@Disposes ConverterProvider impl) {
  }

  @PreDestroy
  public void destroy() {
    beanInstances.destroy((ex) -> appLogger.error(ex));
  }
}
