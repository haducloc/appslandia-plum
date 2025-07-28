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

import com.appslandia.common.base.FunctionBlock;
import com.appslandia.common.base.InitializeException;
import com.appslandia.common.threading.BlockingQueuePool;

import jakarta.annotation.PostConstruct;
import jakarta.el.ELProcessor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class ElProcessorPool {

  public static final String CONFIG_POOL_SIZE = ElProcessorPool.class.getName() + ".pool_size";

  @Inject
  protected AppConfig appConfig;

  private BlockingQueuePool<ELProcessor> elPool;

  @PostConstruct
  protected void initialize() {
    var poolSize = this.appConfig.getInt(CONFIG_POOL_SIZE, 32);
    this.elPool = new BlockingQueuePool<ELProcessor>(() -> initELProcessor(), poolSize);
  }

  public <R> R execute(FunctionBlock<ELProcessor, R> fxBlock) throws Exception {
    return this.elPool.execute(fxBlock);
  }

  public ELProcessor obtain() {
    return this.elPool.obtain();
  }

  public boolean release(ELProcessor impl) {
    return this.elPool.release(impl);
  }

  static ELProcessor initELProcessor() {
    try {
      return new ELProcessor();
    } catch (Exception ex) {
      throw new InitializeException("Failed to create an instance of jakarta.el.ELProcessor.", ex);
    }
  }
}