// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.defaults;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.appslandia.common.cdi.BeanInstance;
import com.appslandia.common.cdi.CDIUtils;
import com.appslandia.plum.base.AppLogger;
import com.appslandia.plum.base.ControllerProvider;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class DefaultControllerProvider implements ControllerProvider {

  @Inject
  protected AppLogger appLogger;

  @Inject
  protected BeanManager beanManager;

  final Map<Class<?>, BeanInstance<?>> controllers = new ConcurrentHashMap<>();

  @Override
  public Object getController(Class<?> controllerClass) throws Exception {
    BeanInstance<?> instance = controllers.computeIfAbsent(controllerClass,
        cls -> CDIUtils.getReference(beanManager, cls));

    return instance.get();
  }

  @PreDestroy
  public void dispose() {
    appLogger.info("Destroying controller instances...");

    controllers.values().forEach(beanInstance -> {
      try {
        beanInstance.destroy();
      } catch (RuntimeException ex) {
        appLogger.error(ex);
      }
    });
  }
}
