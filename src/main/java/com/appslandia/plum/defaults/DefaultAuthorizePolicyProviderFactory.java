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

import java.util.Map;

import com.appslandia.common.base.AppLogger;
import com.appslandia.common.base.MappedID;
import com.appslandia.common.cdi.BeanInstances;
import com.appslandia.common.cdi.CDIFactory;
import com.appslandia.common.cdi.CDIUtils;
import com.appslandia.common.utils.ObjectUtils;
import com.appslandia.common.utils.ReflectionUtils;
import com.appslandia.plum.base.AuthorizePolicy;
import com.appslandia.plum.base.AuthorizePolicyProvider;

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
public class DefaultAuthorizePolicyProviderFactory implements CDIFactory<AuthorizePolicyProvider> {

  @Inject
  protected AppLogger appLogger;

  @Inject
  protected BeanManager beanManager;

  final BeanInstances beanInstances = new BeanInstances();

  @Produces
  @ApplicationScoped
  @Override
  public AuthorizePolicyProvider produce() {
    final var impl = new AuthorizePolicyProvider();

    CDIUtils.scanReferences(this.beanManager, AuthorizePolicy.class, ReflectionUtils.EMPTY_ANNOTATIONS,
        (beanClass, bi) -> {
          var mappedID = beanClass.getDeclaredAnnotation(MappedID.class);
          if (mappedID != null) {

            this.appLogger.info("Installing AuthorizePolicy: {0}", bi.get());
            impl.registerAuthorizePolicy(mappedID.value(), bi.get());
            this.beanInstances.add(bi);
          } else {
            this.appLogger.warn("No @MappedID. Skipping: {0}", bi.get());
            bi.destroy();
          }
        });

    CDIUtils.consumeSuppliers(this.beanManager, AuthorizePolicy.class, (s) -> {
      Map<String, AuthorizePolicy> policies = ObjectUtils.cast(s.get());

      for (Map.Entry<String, AuthorizePolicy> policy : policies.entrySet()) {
        impl.registerAuthorizePolicy(policy.getKey(), policy.getValue());
      }
    });
    return impl;
  }

  @Override
  public void dispose(@Disposes AuthorizePolicyProvider impl) {
  }

  @PreDestroy
  public void destroy() {
    this.beanInstances.destroy((ex) -> this.appLogger.error(ex));
  }
}
