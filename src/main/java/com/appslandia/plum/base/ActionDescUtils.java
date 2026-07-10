// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 *
 * @author Loc Ha
 *
 */
public class ActionDescUtils {

  public static boolean isActionMethod(Method method) {
    if ((method.getDeclaringClass() == Object.class) || !Modifier.isPublic(method.getModifiers())
        || Modifier.isStatic(method.getModifiers()) || (method.getDeclaredAnnotation(Removed.class) != null)) {
      return false;
    }
    var httpMethods = ActionDescProvider.parseHttpMethods(method);
    return !httpMethods.isEmpty();
  }

  public static boolean isExplicitValueResult(Method actionMethod) {
    if ((void.class != actionMethod.getReturnType())
        && !ActionResult.class.isAssignableFrom(actionMethod.getReturnType())) {
      return true;
    }
    return false;
  }
}
