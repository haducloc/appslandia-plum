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

package com.appslandia.plum.mocks;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.appslandia.common.base.CaseInsensitiveMap;
import com.appslandia.common.base.MemoryStream;
import com.appslandia.common.base.Params;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.URLUtils;
import com.appslandia.plum.utils.HeaderUtils;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
public class MockHttpServletResponse implements HttpServletResponse {

  private MockServletContext servletContext;

  private String contentType;
  private String encoding;
  private int status = HttpServletResponse.SC_OK;
  private boolean committed;

  private MemoryStream content = new MemoryStream();
  private PrintWriter outWriter;
  private ServletOutputStream outStream;

  private List<Cookie> cookies = new ArrayList<>();
  private Map<String, MockHttpHeader> headers = new CaseInsensitiveMap<>();

  public MockHttpServletResponse(MockServletContext servletContext) {
    this.servletContext = servletContext;
  }

  public MemoryStream getContent() {
    return content;
  }

  @Override
  public String getCharacterEncoding() {
    if (encoding == null) {
      return StandardCharsets.ISO_8859_1.name();
    }
    return encoding;
  }

  @Override
  public String getContentType() {
    Asserts.notNull(contentType, "contentType is required.");
    return contentType;
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
      outWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(content, getCharacterEncoding())));
    }
    return outWriter;
  }

  @Override
  public void setCharacterEncoding(String encoding) {
    this.encoding = encoding;
  }

  @Override
  public void setContentLength(int len) {
    setHeader("Content-Length", Integer.toString(len));
  }

  @Override
  public void setContentLengthLong(long len) {
    setHeader("Content-Length", Long.toString(len));
  }

  @Override
  public void setContentType(String type) {
    contentType = type;
  }

  @Override
  public void setBufferSize(int size) {
  }

  @Override
  public int getBufferSize() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void flushBuffer() throws IOException {
    if (outWriter != null) {
      outWriter.flush();

    } else if (outStream != null) {
      outStream.flush();
    }
    committed = true;
  }

  @Override
  public void resetBuffer() {
    assertNotCommitted();
    outWriter = null;
    outStream = null;
    content.reset();
  }

  @Override
  public boolean isCommitted() {
    return committed;
  }

  @Override
  public void reset() {
    assertNotCommitted();

    encoding = null;
    cookies.clear();
    headers.clear();

    status = HttpServletResponse.SC_OK;
    resetBuffer();
  }

  @Override
  public void setLocale(Locale locale) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Locale getLocale() {
    return Locale.getDefault();
  }

  @Override
  public String encodeURL(String url) {
    return URLUtils.toUrl(url, new Params().set("encodeURL", true));
  }

  @Override
  public String encodeRedirectURL(String url) {
    return URLUtils.toUrl(url, new Params().set("encodeRedirectURL", true));
  }

  private void assertNotCommitted() {
    if (committed) {
      throw new IllegalStateException("Response is already committed.");
    }
  }

  @Override
  public void sendError(int sc, String msg) throws IOException {
    assertNotCommitted();
    status = sc;
    resetBuffer();
    committed = true;
  }

  @Override
  public void sendError(int sc) throws IOException {
    assertNotCommitted();
    status = sc;
    resetBuffer();
    committed = true;
  }

  @Override
  public void sendRedirect(String location) throws IOException {
    assertNotCommitted();
    setHeader("Location", location);
    status = 302;

    resetBuffer();
    committed = true;
  }

  @Override
  public void sendRedirect(String location, int sc, boolean clearBuffer) throws IOException {
    assertNotCommitted();
    setHeader("Location", location);
    status = sc;

    if (clearBuffer) {
      resetBuffer();
    }
    committed = true;
  }

  @Override
  public void setStatus(int sc) {
    assertNotCommitted();
    status = sc;
  }

  @Override
  public int getStatus() {
    return status;
  }

  public Cookie getCookie(String name) {
    return MockServletUtils.getCookie(cookies, name);
  }

  @Override
  public void addCookie(Cookie cookie) {
    cookies.add(cookie);
  }

  public MockHttpServletResponse addSessionCookie(String value) {
    cookies.add(MockServletUtils.createSessionCookie(servletContext, value));
    return this;
  }

  public MockHttpServletResponse setHeaderValues(String name, String... values) {
    MockServletUtils.setHeaderValues(headers, name, values);
    return this;
  }

  public MockHttpServletResponse addHeaderValues(String name, String... values) {
    MockServletUtils.addHeaderValues(headers, name, values);
    return this;
  }

  @Override
  public void setDateHeader(String name, long date) {
    setHeaderValues(name, HeaderUtils.toDateHeaderString(date));
  }

  @Override
  public void addDateHeader(String name, long date) {
    addHeaderValues(name, HeaderUtils.toDateHeaderString(date));
  }

  @Override
  public void setHeader(String name, String value) {
    setHeaderValues(name, value);
  }

  @Override
  public void addHeader(String name, String value) {
    addHeaderValues(name, value);
  }

  @Override
  public void setIntHeader(String name, int value) {
    setHeaderValues(name, Integer.toString(value));
  }

  @Override
  public void addIntHeader(String name, int value) {
    addHeaderValues(name, Integer.toString(value));
  }

  public long getDateHeader(String name) {
    return MockServletUtils.getDateHeader(headers, name);
  }

  public int getIntHeader(String name) {
    return MockServletUtils.getIntHeader(headers, name);
  }

  @Override
  public boolean containsHeader(String name) {
    return MockHttpHeader.getByName(headers, name) != null;
  }

  @Override
  public String getHeader(String name) {
    var header = MockHttpHeader.getByName(headers, name);
    return (header != null) ? header.getValue() : null;
  }

  @Override
  public Collection<String> getHeaders(String name) {
    var header = MockHttpHeader.getByName(headers, name);
    return (header != null) ? Collections.unmodifiableCollection(header.getValues()) : Collections.emptyList();
  }

  @Override
  public Collection<String> getHeaderNames() {
    return headers.keySet();
  }

  private class ServletOutputStreamImpl extends ServletOutputStream {

    final BufferedOutputStream bos = new BufferedOutputStream(content);

    @Override
    public void write(int b) throws IOException {
      bos.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
      bos.write(b, off, len);
    }

    @Override
    public void write(byte[] b) throws IOException {
      bos.write(b);
    }

    @Override
    public void flush() throws IOException {
      bos.flush();
    }

    @Override
    public void close() throws IOException {
      bos.close();
    }

    @Override
    public boolean isReady() {
      return false;
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {
      throw new UnsupportedOperationException();
    }
  }
}
