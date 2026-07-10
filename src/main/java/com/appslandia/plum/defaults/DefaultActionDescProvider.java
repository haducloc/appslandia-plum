// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.defaults;

import com.appslandia.common.cdi.CDIUtils;
import com.appslandia.common.utils.ReflectionUtils;
import com.appslandia.plum.base.ActionDescProvider;
import com.appslandia.plum.base.ControllerBase;
import com.appslandia.plum.base.Removed;

import jakarta.enterprise.context.ApplicationScoped;
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
    CDIUtils.scanBeanClasses(beanManager, ControllerBase.class, ReflectionUtils.EMPTY_ANNOTATIONS, (beanClass) -> {

      if (beanClass.getDeclaredAnnotation(Removed.class) == null) {
        registerController(beanClass);
      }

    });
    super.init();
  }
}
