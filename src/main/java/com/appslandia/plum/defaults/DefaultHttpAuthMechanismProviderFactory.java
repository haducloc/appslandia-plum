// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.defaults;

import java.lang.annotation.Annotation;

import com.appslandia.common.base.MappedID;
import com.appslandia.common.cdi.BeanInstances;
import com.appslandia.common.cdi.CDIFactory;
import com.appslandia.common.cdi.CDIUtils;
import com.appslandia.plum.base.AppLogger;
import com.appslandia.plum.base.HttpAuthMechanismBase;
import com.appslandia.plum.base.HttpAuthMechanismProvider;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;
import jakarta.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class DefaultHttpAuthMechanismProviderFactory implements CDIFactory<HttpAuthMechanismProvider> {

  @Inject
  protected AppLogger appLogger;

  @Inject
  protected BeanManager beanManager;

  final BeanInstances beanInstances = new BeanInstances();

  @Produces
  @ApplicationScoped
  @Override
  public HttpAuthMechanismProvider produce() {
    final var impl = new HttpAuthMechanismProvider();

    CDIUtils.scanReferences(beanManager, HttpAuthenticationMechanism.class, new Annotation[] { Any.Literal.INSTANCE },
        (beanClass, inst) -> {
          if (inst.get() instanceof HttpAuthMechanismBase auth) {

            var mappedID = beanClass.getDeclaredAnnotation(MappedID.class);
            if (mappedID != null) {

              appLogger.info("Installing HttpAuthMechanismBase: ${0}", auth);
              impl.registerMechanism(mappedID.value(), auth);
              beanInstances.add(inst);

            } else {
              appLogger.warn("No @MappedID. Skipping: ${0}", auth);
              inst.destroy();
            }
          } else {
            appLogger.warn("Not HttpAuthMechanismBase. Skipping: ${0}", inst.get());
            inst.destroy();
          }
        });
    return impl;
  }

  @Override
  public void dispose(@Disposes HttpAuthMechanismProvider impl) {
  }

  @PreDestroy
  public void destroy() {
    beanInstances.destroy((ex) -> appLogger.error(ex));
  }
}
