// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Locale;

import com.appslandia.common.base.MemoryStream;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
public class MemoryResponseWrapper extends FinishableResponseWrapper {

  final boolean allowSetHeaders;
  final MemoryStream content;

  private PrintWriter outWriter;
  private ServletOutputStream outStream;

  public MemoryResponseWrapper(HttpServletResponse response, boolean allowSetHeaders, MemoryStream content) {
    super(response);
    this.allowSetHeaders = allowSetHeaders;
    this.content = content;
  }

  public MemoryStream getContent() {
    return content;
  }

  public boolean isAllowSetHeaders() {
    return allowSetHeaders;
  }

  @Override
  public void finishResponse() throws IOException {
    if (outWriter != null) {
      outWriter.flush();

    } else if (outStream != null) {
      outStream.flush();
    }
  }

  @Override
  public ServletOutputStream getOutputStream() throws IOException {
    if (outWriter != null) {
      throw new IllegalStateException("getWriter has been called on this response.");
    }
    if (outStream == null) {
      outStream = new ServletOutputStreamImpl();
    }
    return outStream;
  }

  @Override
  public PrintWriter getWriter() throws IOException {
    if (outStream != null) {
      throw new IllegalStateException("getOutputStream has been called on this response.");
    }
    if (outWriter == null) {
      outWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(content, getCharacterEncoding())), true);
    }
    return outWriter;
  }

  @Override
  public void flushBuffer() throws IOException {
    if (outWriter != null) {
      outWriter.flush();

    } else if (outStream != null) {
      outStream.flush();
    }
  }

  @Override
  public void resetBuffer() {
    super.resetBuffer();

    content.reset();
  }

  @Override
  public void reset() {
    super.reset();

    outStream = null;
    outWriter = null;
    content.reset();
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
    if (allowSetHeaders) {
      super.setContentType(type);
    }
  }

  @Override
  public void setHeader(String name, String value) {
    if (allowSetHeaders) {
      super.setHeader(name, value);
    }
  }

  @Override
  public void setIntHeader(String name, int value) {
    if (allowSetHeaders) {
      super.setIntHeader(name, value);
    }
  }

  @Override
  public void setDateHeader(String name, long date) {
    if (allowSetHeaders) {
      super.setDateHeader(name, date);
    }
  }

  @Override
  public void addHeader(String name, String value) {
    if (allowSetHeaders) {
      super.addHeader(name, value);
    }
  }

  @Override
  public void addIntHeader(String name, int value) {
    if (allowSetHeaders) {
      super.addIntHeader(name, value);
    }
  }

  @Override
  public void addDateHeader(String name, long date) {
    if (allowSetHeaders) {
      super.addDateHeader(name, date);
    }
  }

  @Override
  public void setLocale(Locale loc) {
    if (allowSetHeaders) {
      super.setLocale(loc);
    }
  }

  @Override
  public void setCharacterEncoding(String charset) {
    if (allowSetHeaders) {
      super.setCharacterEncoding(charset);
    }
  }

  @Override
  public void setStatus(int sc) {
    if (allowSetHeaders) {
      super.setStatus(sc);
    }
  }

  @Override
  public void sendError(int sc) throws IOException {
    if (allowSetHeaders) {
      super.sendError(sc);
    }
  }

  @Override
  public void sendError(int sc, String msg) throws IOException {
    if (allowSetHeaders) {
      super.sendError(sc, msg);
    }
  }

  @Override
  public void sendRedirect(String location) throws IOException {
    if (allowSetHeaders) {
      super.sendRedirect(location);
    }
  }

  @Override
  public void sendRedirect(String location, boolean clearBuffer) throws IOException {
    if (allowSetHeaders) {
      super.sendRedirect(location, clearBuffer);
    }
  }

  @Override
  public void sendRedirect(String location, int sc) throws IOException {
    if (allowSetHeaders) {
      super.sendRedirect(location, sc);
    }
  }

  @Override
  public void sendRedirect(String location, int sc, boolean clearBuffer) throws IOException {
    if (allowSetHeaders) {
      super.sendRedirect(location, sc, clearBuffer);
    }
  }

  @Override
  public void addCookie(Cookie cookie) {
    if (allowSetHeaders) {
      super.addCookie(cookie);
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
      return true;
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {
      throw new UnsupportedOperationException("Async IO not supported.");
    }
  }
}
