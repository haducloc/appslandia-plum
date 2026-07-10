// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.defaults;

import java.util.Map;
import java.util.Map.Entry;

import com.appslandia.common.base.GroupFormat;
import com.appslandia.common.cdi.CDIFactory;
import com.appslandia.common.cdi.CDIUtils;
import com.appslandia.common.utils.ObjectUtils;
import com.appslandia.plum.base.GroupFormatProvider;

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
public class DefaultGroupFormatProviderFactory implements CDIFactory<GroupFormatProvider> {

  @Inject
  protected BeanManager beanManager;

  @Produces
  @ApplicationScoped
  @Override
  public GroupFormatProvider produce() {
    final var impl = new GroupFormatProvider();

    CDIUtils.consumeSuppliers(beanManager, GroupFormat.class, (sup) -> {
      Map<String, GroupFormat> fmts = ObjectUtils.cast(sup.get());

      for (Entry<String, GroupFormat> fmt : fmts.entrySet()) {
        impl.registerGroupFormat(fmt.getKey(), fmt.getValue());
      }
    });
    return impl;
  }

  @Override
  public void dispose(@Disposes GroupFormatProvider impl) {
  }
}
