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

import java.lang.annotation.Annotation;

import com.appslandia.common.base.MappedID;
import com.appslandia.common.cdi.BeanInstances;
import com.appslandia.common.cdi.CDIFactory;
import com.appslandia.common.cdi.CDIUtils;
import com.appslandia.plum.base.AppLogger;
import com.appslandia.plum.base.IdentityValidator;
import com.appslandia.plum.base.IdentityValidatorProvider;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class DefaultIdentityValidatorProviderFactory implements CDIFactory<IdentityValidatorProvider> {

  @Inject
  protected AppLogger appLogger;

  @Inject
  protected BeanManager beanManager;

  final BeanInstances beanInstances = new BeanInstances();

  @Produces
  @ApplicationScoped
  @Override
  public IdentityValidatorProvider produce() {
    final var impl = new IdentityValidatorProvider();

    CDIUtils.scanReferences(beanManager, IdentityValidator.class, new Annotation[] { Any.Literal.INSTANCE },
        (beanClass, bi) -> {

          var mappedID = beanClass.getDeclaredAnnotation(MappedID.class);
          if (mappedID != null) {

            appLogger.info("Installing IdentityValidator: ${0}", bi.get());
            impl.registerValidator(mappedID.value(), bi.get());
            beanInstances.add(bi);

          } else {
            appLogger.warn("No @MappedID. Skipping: ${0}", bi.get());
            bi.destroy();
          }
        });
    return impl;
  }

  @Override
  public void dispose(@Disposes IdentityValidatorProvider impl) {
  }

  @PreDestroy
  public void destroy() {
    beanInstances.destroy((ex) -> appLogger.error(ex));
  }
}
