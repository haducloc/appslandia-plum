// Licensed under the MIT License.
// See LICENSE file in the project root for details.

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

    CDIUtils.scanReferences(beanManager, IdentityStore.class, ReflectionUtils.EMPTY_ANNOTATIONS, (beanClass, inst) -> {
      if (inst.get() instanceof IdentityStoreBase store) {

        appLogger.info("Installing IdentityStoreBase: ${0}", store);
        stores.add(store);
        beanInstances.add(inst);
      } else {
        appLogger.warn("Not IdentityStoreBase. Skipping: ${0}", inst.get());
        inst.destroy();
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
