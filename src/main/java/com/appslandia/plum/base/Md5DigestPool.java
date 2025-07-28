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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.appslandia.common.base.FunctionBlock;
import com.appslandia.common.crypto.CryptoException;
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
public class Md5DigestPool {

  public static final String CONFIG_POOL_SIZE = Md5DigestPool.class.getName() + ".pool_size";

  @Inject
  protected AppConfig appConfig;

  protected BlockingQueuePool<MessageDigest> md5Pool;

  @PostConstruct
  protected void initialize() {
    var poolSize = this.appConfig.getInt(CONFIG_POOL_SIZE, 32);
    this.md5Pool = new BlockingQueuePool<>(() -> md5(), md5 -> md5.reset(), poolSize);
  }

  public <R> R execute(FunctionBlock<MessageDigest, R> fxBlock) throws Exception {
    return this.md5Pool.execute(fxBlock);
  }

  public MessageDigest obtain() {
    return this.md5Pool.obtain();
  }

  public boolean release(MessageDigest impl) {
    return this.md5Pool.release(impl);
  }

  static MessageDigest md5() {
    try {
      return MessageDigest.getInstance("MD5");

    } catch (NoSuchAlgorithmException ex) {
      throw new CryptoException(ex.getMessage(), ex);
    }
  }
}