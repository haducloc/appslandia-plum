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

package com.appslandia.plum.defaults;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.appslandia.common.base.AppLogger;
import com.appslandia.common.cdi.BeanInstance;
import com.appslandia.common.cdi.CDIUtils;
import com.appslandia.plum.base.ControllerProvider;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class DefaultControllerProvider implements ControllerProvider {

  @Inject
  protected AppLogger appLogger;

  @Inject
  protected BeanManager beanManager;

  final Map<Class<?>, BeanInstance<?>> controllers = new ConcurrentHashMap<>();

  @Override
  public Object getController(Class<?> controllerClass) throws Exception {
    BeanInstance<?> instance = this.controllers.computeIfAbsent(controllerClass,
        cls -> CDIUtils.getReference(this.beanManager, cls));

    return instance.get();
  }

  @PreDestroy
  public void dispose() {
    this.appLogger.info("Destroying controller instances...");

    this.controllers.values().forEach(beanInstance -> {
      try {
        beanInstance.destroy();
      } catch (RuntimeException ex) {
        this.appLogger.error(ex);
      }
    });
  }
}
