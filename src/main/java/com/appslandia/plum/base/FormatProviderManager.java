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

import java.util.HashMap;
import java.util.Map;

import com.appslandia.common.base.FormatProvider;
import com.appslandia.common.base.Language;
import com.appslandia.common.base.SimplePool;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@ApplicationScoped
public class FormatProviderManager {

  public static final String CONFIG_POOL_SIZE = FormatProviderManager.class.getName() + ".pool_size";
  public static final String CONFIG_POOL_DISABLED = FormatProviderManager.class.getName() + ".pool_disabled";

  final Map<Language, SimplePool<FormatProvider>> pools = new HashMap<>();
  final Object mutex = new Object();

  @Inject
  protected AppConfig appConfig;

  @Inject
  protected FormatProviderFactory formatProviderFactory;

  protected boolean getPoolDisabled() {
    return this.appConfig.getBool(CONFIG_POOL_DISABLED, false);
  }

  protected int getPoolSize() {
    return this.appConfig.getInt(CONFIG_POOL_SIZE, 50);
  }

  public FormatProvider get(Language language) {
    if (this.getPoolDisabled()) {
      this.formatProviderFactory.produce(language);
    }
    FormatProvider formatProvider = getPool(language).get();
    return (formatProvider != null) ? formatProvider : this.formatProviderFactory.produce(language);
  }

  public void put(Language language, FormatProvider formatProvider) {
    if (this.getPoolDisabled()) {
      return;
    }
    getPool(language).put(formatProvider);
  }

  protected SimplePool<FormatProvider> getPool(Language language) {
    SimplePool<FormatProvider> pool = this.pools.get(language);
    if (pool == null) {
      synchronized (this.mutex) {
        if ((pool = this.pools.get(language)) == null) {
          pool = new SimplePool<>(getPoolSize());
          this.pools.put(language, pool);
        }
      }
    }
    return pool;
  }
}
