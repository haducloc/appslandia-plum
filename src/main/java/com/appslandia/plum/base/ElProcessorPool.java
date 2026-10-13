// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import com.appslandia.common.base.FunctionBlock;
import com.appslandia.common.base.InitializingException;
import com.appslandia.common.threading.BlockingQueuePool;
import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.ResettableELProcessor;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class ElProcessorPool {

  @Inject
  protected AppConfig appConfig;

  private BlockingQueuePool<ResettableELProcessor> elPool;

  @PostConstruct
  protected void initialize() {
    var poolSize = appConfig.getInt("config.el_processor_pool_size", 32);
    elPool = new BlockingQueuePool<>(ElProcessorPool::initELProcessor, poolSize, el -> el.reset());
  }

  public <R> R execute(FunctionBlock<ResettableELProcessor, R> fxBlock) {
    Arguments.notNull(fxBlock);

    ResettableELProcessor el = null;
    try {
      el = elPool.obtain();
      return fxBlock.run(el);

    } finally {
      if (el != null) {
        elPool.release(el);
      }
    }
  }

  static ResettableELProcessor initELProcessor() {
    try {
      return new ResettableELProcessor();
    } catch (Exception ex) {
      throw new InitializingException(
          "Failed to create an instance of com.appslandia.common.utils.ResettableELProcessor.", ex);
    }
  }
}
