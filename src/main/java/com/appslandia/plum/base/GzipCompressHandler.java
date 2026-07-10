// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.util.zip.GZIPOutputStream;

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
public class GzipCompressHandler extends CompressHandlerBase {

  @Inject
  protected AppConfig appConfig;

  @Override
  public String getType() {
    return "gzip";
  }

  @Override
  public int priority() {
    return 200;
  }

  @Override
  public FinishableResponseWrapper toCompressWrapper(HttpServletResponse response, MemoryStream content) {
    var compressThreshold = appConfig.getInt(AppConfig.CONFIG_COMPRESS_THRESHOLD);
    return new GzipResponseWrapper(response, compressThreshold, content);
  }

  @Override
  protected void writeCompressedResponse(HttpServletRequest request, HttpServletResponse response, MemoryStream content)
      throws Exception {
    try (var out = new GZIPOutputStream(response.getOutputStream())) {
      content.writeTo(out);
    }
  }
}
