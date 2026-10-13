// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.defaults;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Map.Entry;

import com.appslandia.common.cdi.CDIFactory;
import com.appslandia.common.cdi.CDIUtils;
import com.appslandia.common.converters.Converter;
import com.appslandia.common.converters.ConverterProvider;
import com.appslandia.common.utils.Asserts;
import com.appslandia.plum.base.AppLogger;

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
public class DefaultConverterProviderFactory implements CDIFactory<ConverterProvider> {

  @Inject
  protected AppLogger appLogger;

  @Inject
  protected BeanManager beanManager;

  @Produces
  @ApplicationScoped
  @Override
  public ConverterProvider produce() {
    final var impl = new ConverterProvider();

    CDIUtils.<Converter<?>>consumeReferences(beanManager, Converter.class, new Annotation[] { Any.Literal.INSTANCE },
        (bc, br) -> {
          var named = bc.getDeclaredAnnotation(Named.class);
          var inst = br.get();

          if (named != null) {
            Asserts.isTrue(br.isApplicationScoped() || br.isSingleton());

            appLogger.info("Installing Converter {}: {}", named.value(), inst);
            impl.registerConverter(named.value(), inst);
          } else {
            appLogger.warn("No @Named. Skipping: {}", inst);
          }
        });

    CDIUtils.consumeSuppliers(beanManager, Converter.class, (Map<String, Converter<?>> value) -> {
      appLogger.info("Installing Converters: {}", value);

      for (Entry<String, Converter<?>> converter : value.entrySet()) {
        impl.registerConverter(converter.getKey(), converter.getValue());
      }
    });

    return impl;
  }

  @Override
  public void dispose(@Disposes ConverterProvider impl) {
  }
}
