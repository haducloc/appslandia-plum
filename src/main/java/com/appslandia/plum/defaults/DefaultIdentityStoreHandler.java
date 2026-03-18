// The MIT License (MIT)
// Copyright © 2015 Loc Ha

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

import java.util.ArrayList;
import java.util.List;

import com.appslandia.common.base.DestroyingException;
import com.appslandia.common.base.DestroyingSupport;
import com.appslandia.common.cdi.BeanInstances;
import com.appslandia.common.cdi.CDIUtils;
import com.appslandia.common.utils.ReflectionUtils;
import com.appslandia.plum.base.AppLogger;
import com.appslandia.plum.base.IdentityStoreBase;
import com.appslandia.plum.base.IdentityStoreHandlerBase;

import jakarta.annotation.PreDestroy;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptor;
import jakarta.security.enterprise.identitystore.IdentityStore;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
@Alternative
@Priority(Interceptor.Priority.LIBRARY_BEFORE + 150)
public class DefaultIdentityStoreHandler extends IdentityStoreHandlerBase implements DestroyingSupport {

  @Inject
  protected AppLogger appLogger;

  @Inject
  protected BeanManager beanManager;

  final BeanInstances beanInstances = new BeanInstances();

  @Override
  protected void init() throws Exception {
    final List<IdentityStoreBase> stores = new ArrayList<>();

    CDIUtils.scanReferences(beanManager, IdentityStore.class, ReflectionUtils.EMPTY_ANNOTATIONS, (beanClass, bi) -> {
      if (bi.get() instanceof IdentityStoreBase store) {

        appLogger.info("Installing IdentityStoreBase: ${0}", store);
        stores.add(store);
        beanInstances.add(bi);
      } else {
        appLogger.warn("Not IdentityStoreBase. Skipping: ${0}", bi.get());
        bi.destroy();
      }
    });

    identityStores.addAll(stores);
    super.init();
  }

  @Override
  @PreDestroy
  public void destroy() throws DestroyingException {
    beanInstances.destroy((ex) -> appLogger.error(ex));
  }
}
