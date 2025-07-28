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
    if (this.contentEncoding != null) {
      response.setCharacterEncoding(this.contentEncoding);
    }

    var out = createBufferedWriter(response.getOutputStream(), response.getCharacterEncoding());

    writeContent(out);
    out.flush();
  }

  protected abstract void writeContent(BufferedWriter out) throws Exception;
}
