// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.regex.Pattern;

import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.CaseUtils;
import com.appslandia.common.utils.StringUtils;

/**
 *
 * @author Loc Ha
 *
 */
public class ActionDescUtils {

  private static final Pattern ROUTE_CONTROLLER_PATTERN = Pattern.compile("^[a-z\\d]+(?:-[a-z\\d]+)*$");

  private static final Pattern ROUTE_ACTION_PATTERN = Pattern.compile("^[a-z\\d]+(?:-[a-z\\d]+)*(?:\\.[a-z\\d]+)?$");

  public static boolean isActionMethod(Method method) {
    if ((method.getDeclaringClass() == Object.class) || !Modifier.isPublic(method.getModifiers())
        || Modifier.isStatic(method.getModifiers())) {
      return false;
    }

    var httpMethods = ActionDescProvider.parseHttpMethods(method);
    return !httpMethods.isEmpty();
  }

  public static boolean isJsonValueResult(Method actionMethod) {
    return (void.class != actionMethod.getReturnType())
        && !ActionResult.class.isAssignableFrom(actionMethod.getReturnType());
  }

  public static String getAction(Method actionMethod) {
    String action;
    var actionAnn = actionMethod.getDeclaredAnnotation(Action.class);

    if (actionAnn != null) {
      action = StringUtils.trimToNull(actionAnn.value());
      Asserts.notNull(action, "Invalid @Action value for method {}.", actionMethod);

      Asserts.isTrue(ROUTE_ACTION_PATTERN.matcher(action).matches(),
          "Invalid @Action value '{}'. Action routes must be kebab-case.", action);
    } else {
      action = CaseUtils.toKebabCase(actionMethod.getName());
    }
    return action;
  }

  public static String getController(Class<?> clazz) {
    String controller;
    var controllerAnn = clazz.getDeclaredAnnotation(Controller.class);

    if (controllerAnn != null) {
      controller = StringUtils.trimToNull(controllerAnn.value());
      Asserts.notNull(controller, "Invalid @Controller value for class {}.", clazz);

      Asserts.isTrue(ROUTE_CONTROLLER_PATTERN.matcher(controller).matches(),
          "Invalid @Controller value '{}'. Controller names must be kebab-case.", controller);
    } else {
      var className = clazz.getSimpleName();
      Asserts.isTrue(className.endsWith("Controller"), "Invalid controller class name '{}'.", className);

      controller = StringUtils.trimToNull(className.substring(0, className.length() - 10));
      Asserts.notNull(controller, "Invalid controller class name '{}'.", className);

      controller = CaseUtils.toKebabCase(controller);
    }
    return controller;
  }

  public static String toMvcRoute(String controllerOrAction) {
    return CaseUtils.toKebabCase(controllerOrAction);
  }
}
