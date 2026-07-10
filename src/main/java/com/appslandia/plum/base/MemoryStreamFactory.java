// Licensed under the MIT License.
// See LICENSE file in the project root for details.

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
public class MemoryStreamFactory {

  public static final String CONFIG_POOL_SIZE = MemoryStreamFactory.class.getName() + ".pool_size";
  public static final String CONFIG_BLOCK_SIZE = MemoryStreamFactory.class.getName() + ".block_size";
  public static final String CONFIG_CLEAR_BLOCKS = MemoryStreamFactory.class.getName() + ".clear_blocks";

  @Inject
  protected AppConfig appConfig;

  @Inject
  protected AppLogger appLogger;

  private int blockSize;
  private int poolSize;
  private boolean clearBlocks;

  protected BlockingQueuePool<byte[]> blockPool;

  @PostConstruct
  protected void initialize() {
    blockSize = appConfig.getInt(CONFIG_BLOCK_SIZE, 4096);
    poolSize = appConfig.getInt(CONFIG_POOL_SIZE, 4096);
    clearBlocks = appConfig.getBool(CONFIG_CLEAR_BLOCKS, true);

    appLogger.info(STR.fmt("{}: {}", CONFIG_BLOCK_SIZE, blockSize));
    appLogger.info(STR.fmt("{}: {}", CONFIG_POOL_SIZE, poolSize));
    appLogger.info(STR.fmt("{}: {}", CONFIG_CLEAR_BLOCKS, clearBlocks));

    blockPool = new BlockingQueuePool<>(() -> new byte[blockSize], poolSize, block -> {
      if (clearBlocks) {
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
