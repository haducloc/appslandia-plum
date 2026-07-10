// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.defaults;

import java.util.Map;
import java.util.Map.Entry;

import com.appslandia.common.cdi.CDIFactory;
import com.appslandia.common.cdi.CDIUtils;
import com.appslandia.common.utils.ObjectUtils;
import com.appslandia.plum.base.HtmlSymbol;
import com.appslandia.plum.base.HtmlSymbolProvider;

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
public class DefaultHtmlSymbolProviderFactory implements CDIFactory<HtmlSymbolProvider> {

  @Inject
  protected BeanManager beanManager;

  @Produces
  @ApplicationScoped
  @Override
  public HtmlSymbolProvider produce() {
    final var impl = new HtmlSymbolProvider();

    CDIUtils.consumeSuppliers(beanManager, HtmlSymbol.class, (sup) -> {
      Map<String, String> symbols = ObjectUtils.cast(sup.get());

      for (Entry<String, String> symbol : symbols.entrySet()) {
        impl.registerHtmlSymbol(symbol.getKey(), symbol.getValue());
      }
    });
    return impl;
  }

  @Override
  public void dispose(@Disposes HtmlSymbolProvider impl) {
  }
}
