// The MIT License (MIT)
// Copyright © 2015 Loc Ha

// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:

// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.

// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

package com.appslandia.plum.base;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.appslandia.common.base.InitializeObject;
import com.appslandia.common.base.Out;
import com.appslandia.common.cdi.CDIUtils;
import com.appslandia.common.utils.ReflectionUtils;

import jakarta.enterprise.inject.spi.CDI;

/**
 *
 * @author Loc Ha
 *
 */
public class ActionScanner extends InitializeObject {

  private List<Class<?>> controllers;

  @Override
  protected void init() throws Exception {
    final List<Class<?>> controllers = new ArrayList<>();

    CDIUtils.scanBeanClasses(CDI.current().getBeanManager(), Object.class, ReflectionUtils.EMPTY_ANNOTATIONS,
        (clazz) -> {
          if (clazz.getDeclaredAnnotation(Controller.class) != null) {
            controllers.add(clazz);
          }
        });

    this.controllers = Collections.unmodifiableList(controllers);
  }

  public List<Class<?>> getControllers() {
    this.initialize();
    return this.controllers;
  }

  public void scanActions(Function<Method, Boolean> matcher, BiFunction<Class<?>, Method, Boolean> consumer) {
    this.initialize();

    for (Class<?> controllerClass : this.controllers) {

      // @Removed
      if (controllerClass.getDeclaredAnnotation(Removed.class) != null) {
        continue;
      }
      for (Method actionMethod : controllerClass.getMethods()) {
        if (!ActionDescUtils.isActionMethod(actionMethod)
            || ((matcher != null) && !Boolean.TRUE.equals(matcher.apply(actionMethod)))) {
          continue;
        }

        var stopped = consumer.apply(controllerClass, actionMethod);
        if (Boolean.TRUE.equals(stopped)) {
          return;
        }
      }
    }
  }

  public boolean hasAction(Function<Method, Boolean> matcher) {
    final var out = new Out<Boolean>();
    scanActions(matcher, (controllerClass, actionMethod) -> {
      out.value = true;
      return true;
    });
    return Boolean.TRUE.equals(out.value);
  }

  public boolean hasAnnotation(Class<? extends Annotation> annotation) {
    return hasAction(m -> m.getDeclaredAnnotation(annotation) != null);
  }
}
