// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.appslandia.common.base.BaseEncoder;
import com.appslandia.common.crypto.CryptoException;
import com.appslandia.common.threading.BlockingQueuePool;
import com.appslandia.common.utils.STR;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class HttpSessionIdDigester {

  public static final String CONFIG_DIGEST_ALGORITHM = HttpSessionIdDigester.class.getName() + ".digest_algorithm";
  public static final String CONFIG_DIGEST_POOL_SIZE = HttpSessionIdDigester.class.getName() + ".digest_pool_size";

  @Inject
  protected AppConfig appConfig;

  @Inject
  protected AppLogger appLogger;

  protected BlockingQueuePool<MessageDigest> digestPool;

  @PostConstruct
  protected void initialize() {
    var algorithm = appConfig.getString(CONFIG_DIGEST_ALGORITHM, "SHA-256");
    var poolSize = appConfig.getInt(CONFIG_DIGEST_POOL_SIZE, 32);

    appLogger.info(STR.fmt("{}: {}", CONFIG_DIGEST_ALGORITHM, algorithm));
    appLogger.info(STR.fmt("{}: {}", CONFIG_DIGEST_POOL_SIZE, poolSize));

    digestPool = new BlockingQueuePool<>(() -> newDigest(algorithm), poolSize, MessageDigest::reset);
  }

  public String getSessionIdDigest(HttpServletRequest request) {
    var session = request.getSession(false);
    if (session == null) {
      return null;
    }

    var sessionId = session.getId();
    try {
      return digestPool.execute(md -> {

        var digest = md.digest(sessionId.getBytes(StandardCharsets.UTF_8));
        return BaseEncoder.BASE64_URL_NP.encode(digest);
      });

    } catch (Exception ex) {
      throw new CryptoException(ex.getMessage(), ex);
    }
  }

  static MessageDigest newDigest(String algorithm) {
    try {
      return MessageDigest.getInstance(algorithm);

    } catch (NoSuchAlgorithmException ex) {
      throw new CryptoException(ex.getMessage(), ex);
    }
  }
}
