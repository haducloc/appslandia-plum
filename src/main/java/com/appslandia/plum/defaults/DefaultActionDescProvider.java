// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.defaults;

import com.appslandia.common.utils.ReflectionUtils;
import com.appslandia.plum.base.ActionDescProvider;
import com.appslandia.plum.base.ControllerBase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class DefaultActionDescProvider extends ActionDescProvider {

  @Inject
  protected BeanManager beanManager;

  @Override
  protected void init() throws Exception {
    var controllerBeans = beanManager.getBeans(ControllerBase.class, ReflectionUtils.EMPTY_ANNOTATIONS);
    for (Bean<?> bean : controllerBeans) {

      registerController(bean.getBeanClass());
    }
    super.init();
  }
}
