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

import com.appslandia.common.base.Language;
import com.appslandia.common.base.Out;
import com.appslandia.common.cdi.CDIFactory;
import com.appslandia.common.cdi.CDIUtils;
import com.appslandia.common.utils.Asserts;
import com.appslandia.plum.base.LanguageConfig;
import com.appslandia.plum.base.LanguageProvider;

import jakarta.enterprise.context.ApplicationScoped;
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
public class DefaultLanguageProviderFactory implements CDIFactory<LanguageProvider> {

  @Inject
  protected BeanManager beanManager;

  @Produces
  @ApplicationScoped
  @Override
  public LanguageProvider produce() {
    final var impl = new LanguageProvider();

    var config = new Out<>(false);
    CDIUtils.consumeReference(beanManager, LanguageConfig.class, (supplier) -> {

      config.value = true;
      for (Language language : supplier.get()) {
        impl.addLanguage(language);
      }
    });

    Asserts.isTrue(config.value, "No LanguageConfig implementation found.");
    return impl;
  }

  @Override
  public void dispose(@Disposes LanguageProvider impl) {
  }
}
