// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.mocks;

import com.appslandia.common.factory.ObjectFactory;
import com.appslandia.plum.base.ControllerProvider;

import jakarta.inject.Inject;

/**
 *
 * @author Loc Ha
 *
 */
public class MockControllerProvider implements ControllerProvider {

  @Inject
  protected ObjectFactory factory;

  @Override
  public Object getController(Class<?> controllerClass) throws Exception {
    return factory.getObject(controllerClass);
  }
}
