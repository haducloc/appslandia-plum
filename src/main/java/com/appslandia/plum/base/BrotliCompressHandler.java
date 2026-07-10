// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import com.aayushatharva.brotli4j.Brotli4jLoader;
import com.aayushatharva.brotli4j.encoder.BrotliOutputStream;
import com.aayushatharva.brotli4j.encoder.Encoder;
import com.appslandia.common.base.MemoryStream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class BrotliCompressHandler extends CompressHandlerBase {

  static {
    Brotli4jLoader.ensureAvailability();
  }

  @Inject
  protected AppConfig appConfig;

  @Override
  public String getType() {
    return "br";
  }

  @Override
  public int priority() {
    return 100;
  }

  @Override
  public FinishableResponseWrapper toCompressWrapper(HttpServletResponse response, MemoryStream content) {
    var compressThreshold = appConfig.getInt(AppConfig.CONFIG_COMPRESS_THRESHOLD);
    return new BrotliResponseWrapper(response, compressThreshold, content);
  }

  @Override
  protected void writeCompressedResponse(HttpServletRequest request, HttpServletResponse response, MemoryStream content)
      throws Exception {
    var parameters = new Encoder.Parameters().setQuality(4);
    try (var out = new BrotliOutputStream(response.getOutputStream(), parameters)) {
      content.writeTo(out);
    }
  }
}
