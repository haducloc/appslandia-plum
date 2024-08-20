// The MIT License (MIT)
// Copyright © 2015 AppsLandia. All rights reserved.

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

import com.appslandia.common.base.Out;
import com.appslandia.common.cdi.CDIUtils;
import com.appslandia.common.utils.ReflectionUtils;

import jakarta.enterprise.inject.spi.CDI;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class ActionScanner {

  final List<Class<?>> controllerClasses = new ArrayList<>();

  private ActionScanner() {
  }

  public List<Class<?>> getControllerClasses() {
    return Collections.unmodifiableList(this.controllerClasses);
  }

  public void scanActions(Function<Method, Boolean> matcher, BiFunction<Class<?>, Method, Boolean> consumer) {
    for (Class<?> controllerClass : this.controllerClasses) {

      // @Removed
      if (controllerClass.getDeclaredAnnotation(Removed.class) != null) {
        continue;
      }
      for (Method actionMethod : controllerClass.getMethods()) {
        if (!ActionDescUtils.isActionMethod(actionMethod)) {
          continue;
        }
        if ((matcher != null) && !Boolean.TRUE.equals(matcher.apply(actionMethod))) {
          continue;
        }
        if (Boolean.TRUE.equals(consumer.apply(controllerClass, actionMethod))) {
          return;
        }
      }
    }
  }

  public boolean hasAction(Function<Method, Boolean> matcher) {
    final Out<Boolean> out = new Out<>();
    scanActions(matcher, (controllerClass, actionMethod) -> {
      out.value = true;
      return true;
    });
    return Boolean.TRUE.equals(out.value);
  }

  public boolean hasAnnotation(Class<? extends Annotation> annotation) {
    return hasAction(m -> m.getDeclaredAnnotation(annotation) != null);
  }

  public static ActionScanner getInstance() {
    final ActionScanner scanner = new ActionScanner();

    CDIUtils.scanBeanClasses(CDI.current().getBeanManager(), Object.class, ReflectionUtils.EMPTY_ANNOTATIONS,
        Controller.class, (c, implClass) -> {
          scanner.controllerClasses.add(implClass);
        });
    return scanner;
  }
}
