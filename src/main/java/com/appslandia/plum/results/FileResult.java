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
    response.setContentLengthLong(this.content.length());

    Files.copy(this.content.toPath(), response.getOutputStream());
    response.getOutputStream().flush();
  }
}
