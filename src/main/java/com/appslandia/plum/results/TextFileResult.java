// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.results;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
public abstract class TextFileResult extends FilenameResult {

  protected String contentEncoding;

  public TextFileResult(String fileName, String contentType) {
    this(fileName, contentType, StandardCharsets.UTF_8.name(), false);
  }

  public TextFileResult(String fileName, String contentType, String contentEncoding) {
    this(fileName, contentType, contentEncoding, false);
  }

  public TextFileResult(String fileName, String contentType, String contentEncoding, boolean inline) {
    super(fileName, contentType, inline);
    this.contentEncoding = contentEncoding;
  }

  protected BufferedWriter createBufferedWriter(OutputStream os, String charset) throws IOException {
    return new BufferedWriter(new OutputStreamWriter(os, charset));
  }

  @Override
  protected void writeContent(HttpServletRequest request, HttpServletResponse response) throws Exception {
    if (contentEncoding != null) {
      response.setCharacterEncoding(contentEncoding);
    }
    var out = createBufferedWriter(response.getOutputStream(), response.getCharacterEncoding());

    writeContent(out);
    out.flush();
  }

  protected abstract void writeContent(BufferedWriter out) throws Exception;
}
