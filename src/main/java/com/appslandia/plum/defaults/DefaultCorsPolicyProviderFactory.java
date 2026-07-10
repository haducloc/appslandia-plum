// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.defaults;

import java.util.Map;
import java.util.Map.Entry;

import com.appslandia.common.cdi.BeanInstances;
import com.appslandia.common.cdi.CDIFactory;
import com.appslandia.common.cdi.CDIUtils;
import com.appslandia.common.utils.ObjectUtils;
import com.appslandia.plum.base.AppLogger;
import com.appslandia.plum.base.CorsPolicy;
import com.appslandia.plum.base.CorsPolicyProvider;

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
public class DefaultCorsPolicyProviderFactory implements CDIFactory<CorsPolicyProvider> {

  @Inject
  protected AppLogger appLogger;

  @Inject
  protected BeanManager beanManager;

  final BeanInstances beanInstances = new BeanInstances();

  @Produces
  @ApplicationScoped
  @Override
  public CorsPolicyProvider produce() {
    final var impl = new CorsPolicyProvider();

    CDIUtils.consumeSuppliers(beanManager, CorsPolicy.class, (sup) -> {
      Map<String, CorsPolicy> policies = ObjectUtils.cast(sup.get());

      for (Entry<String, CorsPolicy> policy : policies.entrySet()) {
        impl.registerCorsPolicy(policy.getKey(), policy.getValue());
      }
    });

    return impl;
  }

  @Override
  public void dispose(@Disposes CorsPolicyProvider impl) {
  }

  @PreDestroy
  public void destroy() {
    beanInstances.destroy((ex) -> appLogger.error(ex));
  }
}
