// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.mocks;

import com.appslandia.common.factory.ObjectFactory;
import com.appslandia.plum.base.ActionDescProvider;
import com.appslandia.plum.base.ControllerBase;

import jakarta.inject.Inject;

/**
 *
 * @author Loc Ha
 *
 */
public class MockActionDescProvider extends ActionDescProvider {

  @Inject
  protected ObjectFactory factory;

  @Override
  protected void init() throws Exception {
    addControllerClasses();

    super.init();
  }

  private void addControllerClasses() {
    var iter = factory.getDefinitionIterator();

    while (iter.hasNext()) {
      Class<?> implClass = iter.next().getImplClass();
      if (implClass == null) {
        continue;
      }
      if (ControllerBase.class.isAssignableFrom(implClass)) {
        registerController(implClass);
      }
    }
  }
}
