// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.defaults;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.appslandia.common.cdi.CDIUtils;
import com.appslandia.common.utils.Asserts;
import com.appslandia.plum.base.ControllerProvider;

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
  protected BeanManager beanManager;

  final Map<Class<?>, Object> controllers = new ConcurrentHashMap<>();

  @Override
  public Object getController(Class<?> controllerClass) throws Exception {
    return controllers.computeIfAbsent(controllerClass, cls -> {

      var br = CDIUtils.getReference(beanManager, cls);
      Asserts.isTrue(br.isApplicationScoped());

      return br.get();

    });
  }
}
