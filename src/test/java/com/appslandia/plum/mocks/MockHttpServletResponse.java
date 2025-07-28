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
    return this.content;
  }

  @Override
  public String getCharacterEncoding() {
    if (this.encoding == null) {
      return StandardCharsets.ISO_8859_1.name();
    }
    return this.encoding;
  }

  @Override
  public String getContentType() {
    Asserts.notNull(this.contentType, "contentType is required.");
    return this.contentType;
  }

  @Override
  public ServletOutputStream getOutputStream() throws IOException {
    if (this.outWriter != null) {
      throw new IllegalStateException("getWriter has been called on this response.");
    }
    if (this.outStream == null) {
      this.outStream = new ServletOutputStreamImpl();
    }
    return this.outStream;
  }

  @Override
  public PrintWriter getWriter() throws IOException {
    if (this.outStream != null) {
      throw new IllegalStateException("getOutputStream has been called on this response.");
    }
    if (this.outWriter == null) {
      this.outWriter = new PrintWriter(
          new BufferedWriter(new OutputStreamWriter(this.content, this.getCharacterEncoding())));
    }
    return this.outWriter;
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
    this.contentType = type;
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
    if (this.outWriter != null) {
      this.outWriter.flush();

    } else if (this.outStream != null) {
      this.outStream.flush();
    }
    this.committed = true;
  }

  @Override
  public void resetBuffer() {
    assertNotCommitted();
    this.outWriter = null;
    this.outStream = null;
    this.content.reset();
  }

  @Override
  public boolean isCommitted() {
    return this.committed;
  }

  @Override
  public void reset() {
    assertNotCommitted();

    this.encoding = null;
    this.cookies.clear();
    this.headers.clear();

    this.status = HttpServletResponse.SC_OK;
    this.resetBuffer();
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
    if (this.committed) {
      throw new IllegalStateException("Response is already committed.");
    }
  }

  @Override
  public void sendError(int sc, String msg) throws IOException {
    assertNotCommitted();
    this.status = sc;
    this.resetBuffer();
    this.committed = true;
  }

  @Override
  public void sendError(int sc) throws IOException {
    assertNotCommitted();
    this.status = sc;
    this.resetBuffer();
    this.committed = true;
  }

  @Override
  public void sendRedirect(String location) throws IOException {
    assertNotCommitted();
    setHeader("Location", location);
    this.status = 302;
    this.resetBuffer();
    this.committed = true;
  }

  @Override
  public void setStatus(int sc) {
    assertNotCommitted();
    this.status = sc;
  }

  @Override
  public int getStatus() {
    return this.status;
  }

  public Cookie getCookie(String name) {
    return MockServletUtils.getCookie(this.cookies, name);
  }

  @Override
  public void addCookie(Cookie cookie) {
    this.cookies.add(cookie);
  }

  public MockHttpServletResponse addSessionCookie(String value) {
    this.cookies.add(MockServletUtils.createSessionCookie(this.servletContext, value));
    return this;
  }

  public MockHttpServletResponse setHeaderValues(String name, String... values) {
    MockServletUtils.setHeaderValues(this.headers, name, values);
    return this;
  }

  public MockHttpServletResponse addHeaderValues(String name, String... values) {
    MockServletUtils.addHeaderValues(this.headers, name, values);
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
    return MockServletUtils.getDateHeader(this.headers, name);
  }

  public int getIntHeader(String name) {
    return MockServletUtils.getIntHeader(this.headers, name);
  }

  @Override
  public boolean containsHeader(String name) {
    return MockHttpHeader.getByName(this.headers, name) != null;
  }

  @Override
  public String getHeader(String name) {
    var header = MockHttpHeader.getByName(this.headers, name);
    return (header != null) ? header.getValue() : null;
  }

  @Override
  public Collection<String> getHeaders(String name) {
    var header = MockHttpHeader.getByName(this.headers, name);
    return (header != null) ? Collections.unmodifiableCollection(header.getValues()) : Collections.emptyList();
  }

  @Override
  public Collection<String> getHeaderNames() {
    return this.headers.keySet();
  }

  private class ServletOutputStreamImpl extends ServletOutputStream {

    final BufferedOutputStream bos = new BufferedOutputStream(content);

    @Override
    public void write(int b) throws IOException {
      this.bos.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
      this.bos.write(b, off, len);
    }

    @Override
    public void write(byte[] b) throws IOException {
      this.bos.write(b);
    }

    @Override
    public void flush() throws IOException {
      this.bos.flush();
    }

    @Override
    public void close() throws IOException {
      this.bos.close();
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
