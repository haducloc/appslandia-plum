// The MIT License (MIT)
// Copyright © 2015 AppsLandia. All rights reserved.

// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:

// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.

// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

package com.appslandia.plum.defaults;

import java.util.Map;
import java.util.Map.Entry;

import com.appslandia.common.base.AppLogger;
import com.appslandia.common.base.GroupFormat;
import com.appslandia.common.cdi.BeanInstances;
import com.appslandia.common.cdi.CDIFactory;
import com.appslandia.common.cdi.CDIUtils;
import com.appslandia.common.utils.ObjectUtils;
import com.appslandia.common.utils.ReflectionUtils;
import com.appslandia.plum.base.GroupFormatProvider;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@ApplicationScoped
public class DefaultGroupFormatProviderFactory implements CDIFactory<GroupFormatProvider> {

  @Inject
  protected AppLogger appLogger;

  @Inject
  protected BeanManager beanManager;

  final BeanInstances beanInstances = new BeanInstances();

  @Produces
  @ApplicationScoped
  @Override
  public GroupFormatProvider produce() {
    final GroupFormatProvider impl = new GroupFormatProvider();

    // @Supplier(GroupFormat.class)
    CDIUtils.scanSuppliers(this.beanManager, ReflectionUtils.EMPTY_ANNOTATIONS, GroupFormat.class, (bi) -> {
      Map<String, GroupFormat> m = ObjectUtils.cast(bi.get().get());

      for (Entry<String, GroupFormat> entry : m.entrySet()) {
        impl.addGroupFormat(entry.getKey(), entry.getValue());
      }
      beanInstances.add(bi);
    });
    return impl;
  }

  @Override
  public void dispose(@Disposes GroupFormatProvider impl) {
  }

  @PreDestroy
  public void destroy() {
    this.beanInstances.destroy((ex) -> this.appLogger.error(ex));
  }
}
