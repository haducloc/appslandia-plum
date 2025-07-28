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

import com.appslandia.common.base.MemoryStream;
import com.appslandia.common.threading.BlockingQueuePool;

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

  public static final String CONFIG_BLOCK_SIZE = ServletContentUtil.class.getName() + ".block_size";

  public static final String CONFIG_POOL_SIZE = ServletContentUtil.class.getName() + ".pool_size";

  @Inject
  protected AppConfig appConfig;

  protected BlockingQueuePool<byte[]> blockPool;

  @PostConstruct
  protected void initialize() {
    var poolSize = getPoolSize();
    var blockSize = getBlockSize();

    this.blockPool = new BlockingQueuePool<>(() -> new byte[blockSize], b -> Arrays.fill(b, (byte) 0), poolSize);
  }

  protected int getBlockSize() {
    return this.appConfig.getInt(CONFIG_BLOCK_SIZE, 2048);
  }

  protected int getPoolSize() {
    return this.appConfig.getInt(CONFIG_POOL_SIZE, 1024);
  }

  public MemoryStream newServletContent() {
    var blockSize = getBlockSize();

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
