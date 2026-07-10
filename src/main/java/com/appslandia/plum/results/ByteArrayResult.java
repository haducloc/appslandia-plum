// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.results;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
public class ByteArrayResult extends FilenameResult {

  private byte[] content;

  public ByteArrayResult(byte[] content, String fileName, String contentType) {
    this(content, fileName, contentType, false);
  }

  public ByteArrayResult(byte[] content, String fileName, String contentType, boolean inline) {
    super(fileName, contentType, inline);
    this.content = content;
  }

  @Override
  protected void writeContent(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentLength(content.length);
    response.getOutputStream().write(content);
    response.getOutputStream().flush();
  }
}
