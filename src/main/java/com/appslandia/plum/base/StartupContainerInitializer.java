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

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.appslandia.common.base.InitializeException;
import com.appslandia.common.utils.ObjectUtils;
import com.appslandia.common.utils.STR;

import jakarta.annotation.Priority;
import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.HandlesTypes;

/**
 *
 * @author Loc Ha
 *
 */
@HandlesTypes(Startup.class)
public class StartupContainerInitializer implements ServletContainerInitializer {

  @Override
  public void onStartup(Set<Class<?>> startupClasses, ServletContext sc) throws ServletException {
    List<Class<? extends Startup>> classes = ObjectUtils.cast(new ArrayList<>(startupClasses));
    Collections.sort(classes, (c1, c2) -> {

      var priority1 = c1.getDeclaredAnnotation(Priority.class);
      var priority2 = c2.getDeclaredAnnotation(Priority.class);

      Integer p1 = (priority1 == null) ? Integer.MAX_VALUE : priority1.value();
      Integer p2 = (priority2 == null) ? Integer.MAX_VALUE : priority2.value();
      return p1.compareTo(p2);
    });

    for (Class<? extends Startup> clazz : classes) {
      try {
        var ctor = getPubEmptyCtor(clazz);
        if (ctor == null) {
          continue;
        }
        sc.log(STR.fmt("Installing startup class '{}'.", clazz.getName()));

        Startup impl = ctor.newInstance();
        impl.onStartup(sc);

      } catch (Exception ex) {
        throw new InitializeException(ex);
      }
    }
  }

  static Constructor<? extends Startup> getPubEmptyCtor(Class<? extends Startup> clazz) throws Exception {
    var modifiers = clazz.getModifiers();
    if (Modifier.isAbstract(modifiers)) {
      return null;
    }

    Constructor<? extends Startup> ctor = clazz.getDeclaredConstructor();
    if (Modifier.isPublic(ctor.getModifiers())) {
      return ctor;
    }
    return null;
  }
}
