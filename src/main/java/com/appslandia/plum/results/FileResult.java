// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.results;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
public class FileResult extends FilenameResult {

  private File content;

  public FileResult(File content, String fileName, String contentType) {
    this(content, fileName, contentType, false);
  }

  public FileResult(File content, String fileName, String contentType, boolean inline) {
    super(fileName, contentType, inline);
    this.content = content;
  }

  @Override
  protected void writeContent(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentLengthLong(content.length());

    Files.copy(content.toPath(), response.getOutputStream());
    response.getOutputStream().flush();
  }
}
