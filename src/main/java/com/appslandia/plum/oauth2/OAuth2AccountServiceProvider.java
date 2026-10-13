// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.oauth2;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.appslandia.common.cdi.CDIUtils;
import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.Asserts;
import com.appslandia.plum.base.AppLogger;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class OAuth2AccountServiceProvider {

  protected Map<String, OAuth2AccountService> serviceMap = new HashMap<>();

  @Inject
  protected AppLogger appLogger;

  @Inject
  protected BeanManager beanManager;

  @PostConstruct
  protected void initialize() {
    CDIUtils.<OAuth2AccountService>consumeReferences(beanManager, OAuth2AccountService.class,
        new Annotation[] { Any.Literal.INSTANCE }, (bc, br) -> {
          var named = bc.getDeclaredAnnotation(Named.class);
          var inst = br.get();

          if (named != null) {
            Asserts.isTrue(br.isApplicationScoped() || br.isSingleton());

            appLogger.info("Installing OAuth2AccountService {}: {}", named.value(), inst);

            serviceMap.put(named.value(), inst);
          } else {
            appLogger.warn("No @Named. Skipping: {}", inst);
          }
        });

    serviceMap = Collections.unmodifiableMap(serviceMap);
  }

  public OAuth2AccountService getAccountService(String module) {
    Arguments.notNull(module);

    var impl = serviceMap.get(module);
    return Arguments.notNull(impl, "No OAuth2AccountService is registered for module '{}'.", module);
  }
}
