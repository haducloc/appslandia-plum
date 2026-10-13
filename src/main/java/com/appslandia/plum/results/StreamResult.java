// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.results;

import java.io.IOException;
import java.io.InputStream;

import com.appslandia.common.utils.IOUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
public class StreamResult extends FilenameResult {

  private InputStream content;

  public StreamResult(InputStream content, String fileName, String contentType) {
    this(content, fileName, contentType, false);
  }

  public StreamResult(InputStream content, String fileName, String contentType, boolean inline) {
    super(fileName, contentType, inline);
    this.content = content;
  }

  @Override
  protected void writeContent(HttpServletRequest request, HttpServletResponse response) throws IOException {
    IOUtils.copy(content, response.getOutputStream());
    response.getOutputStream().flush();
  }
}
