// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import com.appslandia.common.base.MemoryStream;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
public class DeflateResponseWrapper extends FinishableResponseWrapper {

  final HttpServletResponse originalResponse;
  final int threshold;
  final MemoryStream buffer;

  private ThresholdDeflateServletOutputStream outStream;
  private PrintWriter outWriter;
  private Boolean usedWriter;
  private boolean finished;

  public DeflateResponseWrapper(HttpServletResponse response, int threshold, MemoryStream buffer) {
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
      outStream = new ThresholdDeflateServletOutputStream();
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
        outStream = new ThresholdDeflateServletOutputStream();
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

  @Override
  public void finishResponse() throws IOException {
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

  private class ThresholdDeflateServletOutputStream extends ServletOutputStream {

    private static final int STATE_BUFFERING = 0;
    private static final int STATE_DEFLATING = 1;
    private static final int STATE_PLAIN = 2;

    final ServletOutputStream originalOut;
    private int state = STATE_BUFFERING;

    private DeflaterOutputStream deflateOut;
    private boolean finished;

    public ThresholdDeflateServletOutputStream() throws IOException {
      originalOut = originalResponse.getOutputStream();
    }

    @Override
    public void write(int b) throws IOException {
      write(new byte[] { (byte) b }, 0, 1);
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
        if (buffer.length() + len >= threshold) {
          switchToDeflate();
          writeToActiveStream(b, off, len);
        } else {
          buffer.write(b, off, len);
        }
        return;
      }
      writeToActiveStream(b, off, len);
    }

    private void writeToActiveStream(byte[] b, int off, int len) throws IOException {
      if (state == STATE_DEFLATING) {
        deflateOut.write(b, off, len);
      } else {
        originalOut.write(b, off, len);
      }
    }

    private void switchToDeflate() throws IOException {
      state = STATE_DEFLATING;
      originalResponse.setHeader("Content-Encoding", "deflate");

      deflateOut = new DeflaterOutputStream(originalOut, new Deflater(Deflater.DEFAULT_COMPRESSION, false), true);
      buffer.writeTo(deflateOut);
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

      } else if (state == STATE_DEFLATING) {
        deflateOut.finish();
        deflateOut.flush();

      } else {
        originalOut.flush();
      }
    }

    @Override
    public void flush() throws IOException {
      if (state == STATE_DEFLATING) {
        deflateOut.flush();
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
