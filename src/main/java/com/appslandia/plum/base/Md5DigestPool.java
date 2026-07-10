// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.appslandia.common.base.FunctionBlock;
import com.appslandia.common.crypto.CryptoException;
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
public class Md5DigestPool {

  public static final String CONFIG_DIGEST_POOL_SIZE = Md5DigestPool.class.getName() + ".digest_pool_size";

  @Inject
  protected AppConfig appConfig;

  @Inject
  protected AppLogger appLogger;

  protected BlockingQueuePool<MessageDigest> digestPool;

  @PostConstruct
  protected void initialize() {
    var poolSize = appConfig.getInt(CONFIG_DIGEST_POOL_SIZE, 32);
    appLogger.info(STR.fmt("{}: {}", CONFIG_DIGEST_POOL_SIZE, poolSize));

    digestPool = new BlockingQueuePool<>(() -> newDigest(), poolSize, MessageDigest::reset);
  }

  public <R> R execute(FunctionBlock<MessageDigest, R> fxBlock) {
    return digestPool.execute(fxBlock);
  }

  public MessageDigest obtain() {
    return digestPool.obtain();
  }

  public boolean release(MessageDigest impl) {
    return digestPool.release(impl);
  }

  static MessageDigest newDigest() {
    try {
      return MessageDigest.getInstance("MD5");

    } catch (NoSuchAlgorithmException ex) {
      throw new CryptoException(ex.getMessage(), ex);
    }
  }
}
