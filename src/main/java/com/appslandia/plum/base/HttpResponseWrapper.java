// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.io.IOException;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

/**
 *
 * @author Loc Ha
 *
 */
public class HttpResponseWrapper extends HttpServletResponseWrapper {

  private int status = HttpServletResponse.SC_OK;
  private String contentType;
  private Long contentLength;

  public HttpResponseWrapper(HttpServletResponse response) {
    super(response);
  }

  @Override
  public void setContentType(String type) {
    this.contentType = type;
    super.setContentType(type);
  }

  @Override
  public void setContentLength(int len) {
    this.contentLength = (long) len;
    super.setContentLength(len);
  }

  @Override
  public void setContentLengthLong(long len) {
    this.contentLength = len;
    super.setContentLengthLong(len);
  }

  @Override
  public void setHeader(String name, String value) {
    if ("Content-Type".equalsIgnoreCase(name)) {
      this.contentType = value;

    } else if ("Content-Length".equalsIgnoreCase(name)) {
      this.contentLength = Long.valueOf(value);
    }
    super.setHeader(name, value);
  }

  @Override
  public void addHeader(String name, String value) {
    if ("Content-Type".equalsIgnoreCase(name)) {
      this.contentType = value;

    } else if ("Content-Length".equalsIgnoreCase(name)) {
      this.contentLength = Long.valueOf(value);
    }
    super.addHeader(name, value);
  }

  @Override
  public String getContentType() {
    var current = super.getContentType();
    return current != null ? current : contentType;
  }

  public Long getContentLength() {
    return contentLength;
  }

  @Override
  public void setStatus(int sc) {
    status = sc;
    super.setStatus(sc);
  }

  @Override
  public void sendError(int sc) throws IOException {
    status = sc;
    super.sendError(sc);
  }

  @Override
  public void sendError(int sc, String msg) throws IOException {
    status = sc;
    super.sendError(sc, msg);
  }

  @Override
  public void sendRedirect(String location) throws IOException {
    status = HttpServletResponse.SC_FOUND;
    super.sendRedirect(location);
  }

  @Override
  public void sendRedirect(String location, boolean clearBuffer) throws IOException {
    status = HttpServletResponse.SC_FOUND;
    super.sendRedirect(location, clearBuffer);
  }

  @Override
  public void sendRedirect(String location, int sc) throws IOException {
    status = sc;
    super.sendRedirect(location, sc);
  }

  @Override
  public void sendRedirect(String location, int sc, boolean clearBuffer) throws IOException {
    status = sc;
    super.sendRedirect(location, sc, clearBuffer);
  }

  public int getTrackedStatus() {
    return status;
  }

  @Override
  public void reset() {
    status = HttpServletResponse.SC_OK;
    contentType = null;
    contentLength = null;
    super.reset();
  }
}
