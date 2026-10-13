// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import com.appslandia.common.base.MemoryStream;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
public abstract class CompressHandlerBase implements CompressHandler {

  @Override
  public void compressResponse(HttpServletRequest request, HttpServletResponse response, MemoryStream content)
      throws Exception {
    response.setHeader("Content-Encoding", getType());

    if (!HttpMethod.HEAD.equals(request.getMethod())) {
      writeCompressedResponse(request, response, content);
    }
  }

  protected abstract void writeCompressedResponse(HttpServletRequest request, HttpServletResponse response,
      MemoryStream content) throws Exception;
}
