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

import com.appslandia.common.base.AppLogger;
import com.appslandia.common.cdi.BeanInstances;
import com.appslandia.common.cdi.CDIUtils;
import com.appslandia.common.cdi.EagerLiteral;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Destroyed;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@ApplicationScoped
public class EagerBeanInitializer {

  @Inject
  protected AppLogger appLogger;

  @Inject
  protected BeanManager beanManager;

  final BeanInstances beanInstances = new BeanInstances();

  public void contextInitialized(@Observes @Initialized(ApplicationScoped.class) ServletContext sc) {
    this.appLogger.info("Initializing eager beans...");

    CDIUtils.scanReferences(this.beanManager, Object.class, new Annotation[] { EagerLiteral.IMPL }, null,
        (ann, inst) -> {

          this.appLogger.info("Initializing eager bean: " + inst.get());
          this.beanInstances.add(inst);
        });

    this.appLogger.info("Finished initializing eager beans.");
  }

  public void contextDestroyed(@Observes @Destroyed(ApplicationScoped.class) ServletContext sc) {
    this.beanInstances.destroy((ex) -> this.appLogger.error(ex));
  }
}
