// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.ReflectionUtils;

import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.HandlesTypes;

/**
 *
 * @author Loc Ha
 *
 */
@HandlesTypes({ DynHandlerRegister.class, ControllerBase.class })
public class DynHandlerInitializer implements ServletContainerInitializer {

  @Override
  public void onStartup(Set<Class<?>> types, ServletContext ctx) throws ServletException {
    ctx.log("Running DynHandlerInitializer...");

    Class<?> dynHandlerRegisterImpl = null;
    Set<Class<?>> controllerImpls = new HashSet<>();

    for (Class<?> type : types) {
      if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
        continue;
      }

      // ControllerBase
      if (ControllerBase.class.isAssignableFrom(type)) {
        if (type.getDeclaredAnnotation(Removed.class) == null) {
          controllerImpls.add(type);
        }
      }

      // DynHandlerRegister
      if (DynHandlerRegister.class.isAssignableFrom(type)) {
        dynHandlerRegisterImpl = type;
      }
    }
    Asserts.notNull(dynHandlerRegisterImpl, "No DynHandlerRegister implemented.");

    var dynHandlerRegister = (DynHandlerRegister) ReflectionUtils.newInstance(dynHandlerRegisterImpl);
    dynHandlerRegister.onStartup(ctx, controllerImpls);
  }
}
