// The MIT License (MIT)
// Copyright © 2015 AppsLandia. All rights reserved.

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
import java.util.Locale;
import java.util.zip.GZIPOutputStream;

import com.appslandia.common.base.MemoryStream;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class ContentResponseWrapper extends ResponseWrapper {

  final boolean allowSetHeaders;
  final MemoryStream content;
  final boolean gzipContent;

  private PrintWriter outWriter;
  private ServletOutputStream outStream;

  public ContentResponseWrapper(HttpServletResponse response, boolean allowSetHeaders, boolean gzipContent) {
    super(response);
    this.allowSetHeaders = allowSetHeaders;
    this.content = new MemoryStream();
    this.gzipContent = gzipContent;
  }

  public MemoryStream getContent() {
    return this.content;
  }

  public boolean isAllowSetHeaders() {
    return this.allowSetHeaders;
  }

  public boolean isGzipContent() {
    return this.gzipContent;
  }

  @Override
  public void finishWrapper() throws IOException {
    if (this.outWriter != null) {
      this.outWriter.close();

    } else if (this.outStream != null) {
      this.outStream.close();
    }
  }

  @Override
  public ServletOutputStream getOutputStream() throws IOException {
    if (this.outWriter != null) {
      throw new IllegalStateException("getWriter has been called on this response.");
    }
    if (this.outStream == null) {
      if (this.gzipContent) {
        this.outStream = new GZIPServletOutputStream();

        this.setHeader("Content-Encoding", "gzip");
      } else {
        this.outStream = new ServletOutputStreamImpl();
      }
    }
    return this.outStream;
  }

  @Override
  public PrintWriter getWriter() throws IOException {
    if (this.outStream != null) {
      throw new IllegalStateException("getOutputStream has been called on this response.");
    }
    if (this.outWriter == null) {
      if (this.gzipContent) {
        this.outWriter = new PrintWriter(new BufferedWriter(
            new OutputStreamWriter(new GZIPOutputStream(this.content), this.getCharacterEncoding())));

        this.setHeader("Content-Encoding", "gzip");
      } else {
        this.outWriter = new PrintWriter(
            new BufferedWriter(new OutputStreamWriter(this.content, this.getCharacterEncoding())));
      }
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
    this.outWriter = null;
    this.outStream = null;
    this.content.reset();
  }

  @Override
  public void reset() {
    if (this.allowSetHeaders) {
      super.reset();
    }
    this.resetBuffer();
  }

  @Override
  public void setContentLength(int len) {
    // Ignore
  }

  @Override
  public void setContentLengthLong(long len) {
    // Ignore
  }

  @Override
  public void setContentType(String type) {
    if (this.allowSetHeaders) {
      super.setContentType(type);
    }
  }

  @Override
  public void setHeader(String name, String value) {
    if (this.allowSetHeaders) {
      super.setHeader(name, value);
    }
  }

  @Override
  public void setIntHeader(String name, int value) {
    if (this.allowSetHeaders) {
      super.setIntHeader(name, value);
    }
  }

  @Override
  public void setDateHeader(String name, long date) {
    if (this.allowSetHeaders) {
      super.setDateHeader(name, date);
    }
  }

  @Override
  public void addHeader(String name, String value) {
    if (this.allowSetHeaders) {
      super.addHeader(name, value);
    }
  }

  @Override
  public void addIntHeader(String name, int value) {
    if (this.allowSetHeaders) {
      super.addIntHeader(name, value);
    }
  }

  @Override
  public void addDateHeader(String name, long date) {
    if (this.allowSetHeaders) {
      super.addDateHeader(name, date);
    }
  }

  @Override
  public void setLocale(Locale loc) {
    if (this.allowSetHeaders) {
      super.setLocale(loc);
    }
  }

  @Override
  public void setCharacterEncoding(String charset) {
    if (this.allowSetHeaders) {
      super.setCharacterEncoding(charset);
    }
  }

  @Override
  public void setStatus(int sc) {
    if (this.allowSetHeaders) {
      super.setStatus(sc);
    }
  }

  @Override
  public void sendError(int sc) throws IOException {
    if (this.allowSetHeaders) {
      super.sendError(sc);
    }
  }

  @Override
  public void sendError(int sc, String msg) throws IOException {
    if (this.allowSetHeaders) {
      super.sendError(sc, msg);
    }
  }

  private class ServletOutputStreamImpl extends ServletOutputStream {

    @Override
    public void write(int b) throws IOException {
      content.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
      content.write(b, off, len);
    }

    @Override
    public void write(byte[] b) throws IOException {
      content.write(b);
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

  private class GZIPServletOutputStream extends ServletOutputStream {

    final GZIPOutputStream gos;

    public GZIPServletOutputStream() throws IOException {
      this.gos = new GZIPOutputStream(content);
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
