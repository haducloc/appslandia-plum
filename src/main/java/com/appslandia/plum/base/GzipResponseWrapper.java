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

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.zip.GZIPOutputStream;

import com.appslandia.common.base.MemoryStream;

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

  final HttpServletResponse originalResponse;
  final int threshold;
  final MemoryStream buffer;

  private ThresholdGzipServletOutputStream outStream;
  private PrintWriter outWriter;
  private Boolean usedWriter;
  private boolean finished;

  public GzipResponseWrapper(HttpServletResponse response, int threshold) {
    this(response, threshold, new MemoryStream());
  }

  public GzipResponseWrapper(HttpServletResponse response, int threshold, MemoryStream buffer) {
    super(response);
    originalResponse = response;
    this.threshold = threshold;
    this.buffer = buffer;
  }

  @Override
  public ServletOutputStream getOutputStream() throws IOException {
    if (Boolean.TRUE.equals(usedWriter)) {
      throw new IllegalStateException("getWriter has been called on this response.");
    }
    if (outStream == null) {
      outStream = new ThresholdGzipServletOutputStream();
      usedWriter = Boolean.FALSE;
    }
    return outStream;
  }

  @Override
  public PrintWriter getWriter() throws IOException {
    if (Boolean.FALSE.equals(usedWriter)) {
      throw new IllegalStateException("getOutputStream has been called on this response.");
    }
    if (outWriter == null) {
      if (outStream == null) {
        outStream = new ThresholdGzipServletOutputStream();
      }
      outWriter = new PrintWriter(new OutputStreamWriter(outStream, originalResponse.getCharacterEncoding()), true);
      usedWriter = Boolean.TRUE;
    }
    return outWriter;
  }

  @Override
  public void setContentLength(int len) {
  }

  @Override
  public void setContentLengthLong(long len) {
  }

  public void finishWrapper() throws IOException {
    if (finished) {
      return;
    }
    finished = true;

    if (outWriter != null) {
      outWriter.flush();
    }
    if (outStream != null) {
      outStream.finish();
    } else {
      originalResponse.flushBuffer();
    }
  }

  @Override
  public void resetBuffer() {
    originalResponse.resetBuffer();

    buffer.reset();
    finished = false;
  }

  @Override
  public void reset() {
    originalResponse.reset();

    outWriter = null;
    outStream = null;
    usedWriter = null;

    buffer.reset();
    finished = false;
  }

  private class ThresholdGzipServletOutputStream extends ServletOutputStream {

    private static final int STATE_BUFFERING = 0;
    private static final int STATE_GZIPPING = 1;
    private static final int STATE_PLAIN = 2;

    final ServletOutputStream originalOut;
    private int state = STATE_BUFFERING;

    private GZIPOutputStream gzipOut;
    private boolean finished;

    public ThresholdGzipServletOutputStream() throws IOException {
      originalOut = originalResponse.getOutputStream();
    }

    @Override
    public void write(int b) throws IOException {
      byte[] one = { (byte) b };
      write(one, 0, 1);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
      if (finished) {
        throw new IOException("Attempt to write after finish()");
      }
      if (len == 0) {
        return;
      }

      if (state == STATE_BUFFERING) {
        if (buffer.length() + len > threshold) {
          switchToGzip();
          writeToActiveStream(b, off, len);
        } else {
          buffer.write(b, off, len);
        }
        return;
      }
      writeToActiveStream(b, off, len);
    }

    private void writeToActiveStream(byte[] b, int off, int len) throws IOException {
      if (state == STATE_GZIPPING) {
        gzipOut.write(b, off, len);
      } else {
        // STATE_PLAIN
        originalOut.write(b, off, len);
      }
    }

    private void switchToGzip() throws IOException {
      state = STATE_GZIPPING;
      originalResponse.setHeader("Content-Encoding", "gzip");

      gzipOut = new GZIPOutputStream(originalOut, true);
      buffer.writeTo(gzipOut);
    }

    public void finish() throws IOException {
      if (finished) {
        return;
      }
      finished = true;

      if (state == STATE_BUFFERING) {
        state = STATE_PLAIN;

        originalResponse.setContentLengthLong(buffer.length());
        buffer.writeTo(originalOut);
        originalOut.flush();

      } else if (state == STATE_GZIPPING) {
        gzipOut.finish();
        gzipOut.flush();

      } else {

        // STATE_PLAIN
        originalOut.flush();
      }
    }

    @Override
    public void flush() throws IOException {
      if (state == STATE_GZIPPING) {
        gzipOut.flush();

      } else if (state == STATE_PLAIN) {
        originalOut.flush();
      }
    }

    @Override
    public void close() throws IOException {
      finish();
    }

    @Override
    public boolean isReady() {
      return true;
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {
      throw new UnsupportedOperationException("Async IO not supported.");
    }
  }
}
