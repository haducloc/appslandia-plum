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

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.appslandia.common.base.InitializeException;
import com.appslandia.common.utils.CollectionUtils;
import com.appslandia.common.utils.ObjectUtils;
import com.appslandia.common.utils.ReflectionUtils;

import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.HandlesTypes;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@HandlesTypes(Startup.class)
public class StartupContainerInitializer implements ServletContainerInitializer {

  @Override
  public void onStartup(Set<Class<?>> startupClasses, ServletContext sc) throws ServletException {
    // allClasses
    List<Class<? extends Startup>> allClasses = ObjectUtils.cast(new ArrayList<>(startupClasses));
    Collections.sort(allClasses, (c1, c2) -> {

      StartupConfig config1 = c1.getDeclaredAnnotation(StartupConfig.class);
      StartupConfig config2 = c2.getDeclaredAnnotation(StartupConfig.class);

      Integer p1 = (config1 == null) ? Integer.MAX_VALUE : config1.priority();
      Integer p2 = (config2 == null) ? Integer.MAX_VALUE : config2.priority();
      return p1.compareTo(p2);
    });

    // removedNames
    final Set<String> removedNames = new HashSet<>();
    allClasses.stream().forEach(clazz -> {
      if (Modifier.isAbstract(clazz.getModifiers())) {
        removedNames.add(clazz.getName());
      }
      StartupConfig config = clazz.getDeclaredAnnotation(StartupConfig.class);
      if (config == null) {
        return;
      }
      CollectionUtils.toSet(removedNames, config.removeNames());
      for (Class<? extends Startup> removeClass : config.removeClasses()) {
        removedNames.add(removeClass.getName());
      }
    });

    // Execute startupClasses
    List<Class<? extends Startup>> unmodifiableClasses = Collections.unmodifiableList(allClasses);
    for (Class<? extends Startup> clazz : allClasses) {
      if (removedNames.contains(clazz.getName())) {
        continue;
      }
      try {
        Startup impl = ReflectionUtils.newInstance(clazz);
        impl.onStartup(sc, unmodifiableClasses);

      } catch (Exception ex) {
        throw new InitializeException(ex);
      }
    }
  }
}
