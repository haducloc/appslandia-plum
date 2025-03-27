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

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.appslandia.common.base.FormatProvider;
import com.appslandia.common.base.Language;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class FormatProviderManager {

  public static final String CONFIG_POOL_SIZE = FormatProviderManager.class.getName() + ".pool_size";
  public static final String CONFIG_POOL_DISABLED = FormatProviderManager.class.getName() + ".pool_disabled";

  final ConcurrentMap<Language, BlockingQueue<FormatProviderHolder>> pools = new ConcurrentHashMap<>();

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
      return this.formatProviderFactory.produce(language);
    }
    BlockingQueue<FormatProviderHolder> pool = getPool(language);

    // Non-blocking poll
    FormatProviderHolder holder = pool.poll();

    if (holder == null || holder.value == null) {
      return this.formatProviderFactory.produce(language);
    }
    return holder.value;
  }

  public void put(Language language, FormatProvider formatProvider) {
    if (this.getPoolDisabled()) {
      return;
    }
    BlockingQueue<FormatProviderHolder> pool = getPool(language);

    // Non-blocking offer
    pool.offer(new FormatProviderHolder(formatProvider));
  }

  protected BlockingQueue<FormatProviderHolder> getPool(Language language) {
    return this.pools.computeIfAbsent(language, lang -> {
      int poolSize = getPoolSize();

      BlockingQueue<FormatProviderHolder> pool = new ArrayBlockingQueue<>(poolSize);
      for (int i = 0; i < poolSize; i++) {
        pool.offer(new FormatProviderHolder());
      }
      return pool;
    });
  }

  static class FormatProviderHolder {
    final FormatProvider value;

    public FormatProviderHolder() {
      this(null);
    }

    public FormatProviderHolder(FormatProvider value) {
      this.value = value;
    }
  }
}
