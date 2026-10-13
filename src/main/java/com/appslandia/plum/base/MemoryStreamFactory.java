// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.util.Arrays;
import java.util.Iterator;

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
public class MemoryStreamFactory {

  public static final String CONFIG_POOL_SIZE = "memory_stream.block_pool_size";
  public static final String CONFIG_BLOCK_SIZE = "memory_stream.block_size";
  public static final String CONFIG_RESET_BLOCKS = "memory_stream.reset_blocks";

  @Inject
  protected AppConfig appConfig;

  @Inject
  protected AppLogger appLogger;

  private int blockSize;
  private int poolSize;
  private boolean resetBlocks;

  protected BlockingQueuePool<byte[]> blockPool;

  @PostConstruct
  protected void initialize() {
    blockSize = appConfig.getInt(CONFIG_BLOCK_SIZE, 4096);
    poolSize = appConfig.getInt(CONFIG_POOL_SIZE, 4096);
    resetBlocks = appConfig.getBool(CONFIG_RESET_BLOCKS, true);

    appLogger.info("{}: {}", CONFIG_BLOCK_SIZE, blockSize);
    appLogger.info("{}: {}", CONFIG_POOL_SIZE, poolSize);
    appLogger.info("{}: {}", CONFIG_RESET_BLOCKS, resetBlocks);

    blockPool = new BlockingQueuePool<>(() -> new byte[blockSize], poolSize, block -> {
      if (resetBlocks) {
        Arrays.fill(block, (byte) 0);
      }
    });
  }

  public Iterator<byte[]> iterator() {
    return blockPool.iterator();
  }

  public MemoryStream newMemoryStream() {
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
