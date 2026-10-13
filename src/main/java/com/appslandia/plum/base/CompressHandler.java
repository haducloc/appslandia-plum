// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import com.appslandia.common.base.MemoryStream;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface CompressHandler {

  /**
   * Defines the priority of this handler. Lower numeric values indicate higher priority.
   *
   */
  default int priority() {
    return 1000;
  }

  String getType();

  FinishableResponseWrapper toCompressWrapper(HttpServletResponse response, MemoryStream content);

  void compressResponse(HttpServletRequest request, HttpServletResponse response, MemoryStream content)
      throws Exception;
}
