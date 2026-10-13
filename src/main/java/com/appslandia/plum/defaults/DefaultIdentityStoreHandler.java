// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.defaults;

import java.util.ArrayList;
import java.util.List;

import com.appslandia.common.cdi.CDIUtils;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.ReflectionUtils;
import com.appslandia.plum.base.AppLogger;
import com.appslandia.plum.base.IdentityStoreBase;
import com.appslandia.plum.base.IdentityStoreHandlerBase;

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
public class DefaultIdentityStoreHandler extends IdentityStoreHandlerBase {

  @Inject
  protected AppLogger appLogger;

  @Inject
  protected BeanManager beanManager;

  @Override
  protected void init() throws Exception {
    final List<IdentityStoreBase> stores = new ArrayList<>();

    CDIUtils.consumeReferences(beanManager, IdentityStore.class, ReflectionUtils.EMPTY_ANNOTATIONS, (bc, br) -> {
      var inst = br.get();

      if (inst instanceof IdentityStoreBase store) {
        Asserts.isTrue(br.isApplicationScoped() || br.isSingleton());

        appLogger.info("Installing IdentityStoreBase: {}", store);
        stores.add(store);
      } else {
        appLogger.warn("Not IdentityStoreBase. Skipping: {}", inst);
      }
    });

    identityStores.addAll(stores);
    super.init();
  }
}
