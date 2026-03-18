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

import java.util.Arrays;
import java.util.Iterator;

import com.appslandia.common.base.MemoryStream;
import com.appslandia.common.threading.BlockingQueuePool;
import com.appslandia.common.utils.STR;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class ServletContentUtil {

  public static final String CONFIG_POOL_SIZE = ServletContentUtil.class.getName() + ".pool_size";

  public static final String CONFIG_BLOCK_SIZE = ServletContentUtil.class.getName() + ".block_size";

  @Inject
  protected AppConfig appConfig;

  @Inject
  protected AppLogger appLogger;

  private int blockSize;
  private int poolSize;

  protected BlockingQueuePool<byte[]> blockPool;

  @PostConstruct
  protected void initialize() {
    blockSize = appConfig.getInt(CONFIG_BLOCK_SIZE, 2048);
    poolSize = appConfig.getInt(CONFIG_POOL_SIZE, 1024);

    appLogger.info(STR.fmt("{}: {}", CONFIG_BLOCK_SIZE, blockSize));
    appLogger.info(STR.fmt("{}: {}", CONFIG_POOL_SIZE, poolSize));

    blockPool = new BlockingQueuePool<>(() -> new byte[blockSize], b -> Arrays.fill(b, (byte) 0), poolSize);
  }

  public Iterator<byte[]> iterator() {
    return blockPool.iterator();
  }

  public MemoryStream newServletContent() {

    return new MemoryStream(blockSize) {
      private static final long serialVersionUID = 1L;

      @Override
      protected byte[] obtainBlock(int blockSize) {
        return blockPool.obtain();
      }

      @Override
      protected void releaseBlock(byte[] block) {
        blockPool.release(block);
      }
    };
  }
}
