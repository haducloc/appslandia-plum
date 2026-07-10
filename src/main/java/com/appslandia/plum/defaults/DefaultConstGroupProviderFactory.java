// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.defaults;

import java.util.Collection;

import com.appslandia.common.cdi.CDIFactory;
import com.appslandia.common.cdi.CDIUtils;
import com.appslandia.common.utils.ObjectUtils;
import com.appslandia.plum.base.ConstGroup;
import com.appslandia.plum.base.ConstGroupProvider;

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
public class DefaultConstGroupProviderFactory implements CDIFactory<ConstGroupProvider> {

  @Inject
  protected BeanManager beanManager;

  @Produces
  @ApplicationScoped
  @Override
  public ConstGroupProvider produce() {
    final var impl = new ConstGroupProvider();

    CDIUtils.consumeSuppliers(beanManager, ConstGroup.class, (sup) -> {
      Collection<Class<?>> classes = ObjectUtils.cast(sup.get());

      for (Class<?> constClass : classes) {
        impl.addConstClass(constClass);
      }
    });
    return impl;
  }

  @Override
  public void dispose(@Disposes ConstGroupProvider impl) {
  }
}
