// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

/**
 *
 * @author Loc Ha
 *
 */
public interface ControllerProvider {

  Object getController(Class<?> controllerClass) throws Exception;
}
