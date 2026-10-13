// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.defaults;

import java.lang.annotation.Annotation;

import com.appslandia.common.cdi.CDIFactory;
import com.appslandia.common.cdi.CDIUtils;
import com.appslandia.common.utils.Asserts;
import com.appslandia.plum.base.AppLogger;
import com.appslandia.plum.base.HttpAuthMechanismBase;
import com.appslandia.plum.base.HttpAuthMechanismProvider;

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

  @Produces
  @ApplicationScoped
  @Override
  public HttpAuthMechanismProvider produce() {
    final var impl = new HttpAuthMechanismProvider();

    CDIUtils.consumeReferences(beanManager, HttpAuthenticationMechanism.class,
        new Annotation[] { Any.Literal.INSTANCE }, (bc, br) -> {
          var inst = br.get();

          if (inst instanceof HttpAuthMechanismBase mechanism) {
            Asserts.isTrue(br.isApplicationScoped() || br.isSingleton());

            appLogger.info("Installing HttpAuthMechanismBase {}: {}", mechanism.getModule(), mechanism);
            impl.registerMechanism(mechanism.getModule(), mechanism);

          } else {
            appLogger.warn("Not HttpAuthMechanismBase. Skipping: {}", inst);
          }
        });

    return impl;
  }

  @Override
  public void dispose(@Disposes HttpAuthMechanismProvider impl) {
  }
}
