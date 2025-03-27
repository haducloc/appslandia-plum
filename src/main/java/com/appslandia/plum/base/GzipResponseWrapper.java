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

package com.appslandia.plum.base;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.zip.GZIPOutputStream;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

/**
 *
 * @author Loc Ha
 *
 */
public class GzipResponseWrapper extends HttpServletResponseWrapper {

  private GZIPServletOutputStream outStream;
  private PrintWriter outWriter;
  private Boolean usedWriter;

  public GzipResponseWrapper(HttpServletResponse response) {
    super(response);
    response.setHeader("Content-Encoding", "gzip");
  }

  public void finishWrapper() throws IOException {
    if (this.outWriter != null) {
      this.outWriter.flush();
    }
    if (this.outStream != null) {
      this.outStream.finish();
    }
  }

  @Override
  public ServletOutputStream getOutputStream() throws IOException {
    if (Boolean.TRUE.equals(this.usedWriter)) {
      throw new IllegalStateException("getWriter has been called on this response.");
    }
    if (this.outStream == null) {
      this.outStream = new GZIPServletOutputStream(super.getOutputStream());
      this.usedWriter = Boolean.FALSE;
    }
    return this.outStream;
  }

  @Override
  public PrintWriter getWriter() throws IOException {
    if (Boolean.FALSE.equals(this.usedWriter)) {
      throw new IllegalStateException("getOutputStream has been called on this response.");
    }
    if (this.outWriter == null) {
      this.outStream = new GZIPServletOutputStream(super.getOutputStream());
      this.outWriter = new PrintWriter(
          new BufferedWriter(new OutputStreamWriter(this.outStream, this.getCharacterEncoding())));
      this.usedWriter = Boolean.TRUE;
    }
    return this.outWriter;
  }

  @Override
  public void flushBuffer() throws IOException {
    if (this.outWriter != null) {
      this.outWriter.flush();

    } else if (this.outStream != null) {
      this.outStream.flush();
    }
  }

  @Override
  public void resetBuffer() {
    super.resetBuffer();

    this.outWriter = null;
    this.outStream = null;
    this.usedWriter = null;
  }

  @Override
  public void reset() {
    super.reset();

    this.outWriter = null;
    this.outStream = null;
    this.usedWriter = null;
  }

  @Override
  public void setContentLength(int len) {
    // Ignore
  }

  @Override
  public void setContentLengthLong(long len) {
    // Ignore
  }

  static final int GZIP_BUFFER_SIZE = 4096;

  private static class GZIPServletOutputStream extends ServletOutputStream {

    final GZIPOutputStream gos;

    public GZIPServletOutputStream(ServletOutputStream os) throws IOException {
      this.gos = new GZIPOutputStream(os, GZIP_BUFFER_SIZE, false);
    }

    @Override
    public void write(int b) throws IOException {
      this.gos.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
      this.gos.write(b, off, len);
    }

    @Override
    public void write(byte[] b) throws IOException {
      this.gos.write(b);
    }

    @Override
    public void flush() throws IOException {
      this.gos.flush();
    }

    @Override
    public void close() throws IOException {
      this.gos.close();
    }

    public void finish() throws IOException {
      this.gos.finish();
    }

    @Override
    public boolean isReady() {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {
      throw new UnsupportedOperationException();
    }
  }
}
