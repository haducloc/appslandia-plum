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

import java.io.IOException;

import com.appslandia.common.utils.Asserts;

import jakarta.servlet.ServletConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpUpgradeHandler;

/**
 *
 * @author Loc Ha
 *
 */
public class MockCurrentRequest implements HttpServletRequest {

  protected HttpServletRequest getCurrent() {
    HttpServletRequest current = MockContainer.currentRequestHolder.get();
    Asserts.isTrue(current != null, "currentRequest is null.");
    return current;
  }

  // jakarta.servlet.http.HttpServletRequest

  @Override
  public java.lang.String getQueryString() {
    return getCurrent().getQueryString();
  }

  @Override
  public int getIntHeader(java.lang.String arg0) {
    return getCurrent().getIntHeader(arg0);
  }

  @Override
  public long getDateHeader(java.lang.String arg0) {
    return getCurrent().getDateHeader(arg0);
  }

  @Override
  public java.lang.StringBuffer getRequestURL() {
    return getCurrent().getRequestURL();
  }

  @Override
  public boolean isRequestedSessionIdFromURL() {
    return getCurrent().isRequestedSessionIdFromURL();
  }

  @Override
  public boolean authenticate(jakarta.servlet.http.HttpServletResponse arg0)
      throws java.io.IOException, jakarta.servlet.ServletException {
    return getCurrent().authenticate(arg0);
  }

  @Override
  public java.lang.String changeSessionId() {
    return getCurrent().changeSessionId();
  }

  @Override
  public java.lang.String getAuthType() {
    return getCurrent().getAuthType();
  }

  @Override
  public java.lang.String getContextPath() {
    return getCurrent().getContextPath();
  }

  @Override
  public jakarta.servlet.http.Cookie[] getCookies() {
    return getCurrent().getCookies();
  }

  @Override
  public java.lang.String getHeader(java.lang.String arg0) {
    return getCurrent().getHeader(arg0);
  }

  @Override
  public java.util.Enumeration<java.lang.String> getHeaderNames() {
    return getCurrent().getHeaderNames();
  }

  @Override
  public java.util.Enumeration<java.lang.String> getHeaders(java.lang.String arg0) {
    return getCurrent().getHeaders(arg0);
  }

  @Override
  public jakarta.servlet.http.HttpServletMapping getHttpServletMapping() {
    return getCurrent().getHttpServletMapping();
  }

  @Override
  public java.lang.String getMethod() {
    return getCurrent().getMethod();
  }

  @Override
  public jakarta.servlet.http.Part getPart(java.lang.String arg0)
      throws java.io.IOException, jakarta.servlet.ServletException {
    return getCurrent().getPart(arg0);
  }

  @Override
  public java.util.Collection<jakarta.servlet.http.Part> getParts()
      throws java.io.IOException, jakarta.servlet.ServletException {
    return getCurrent().getParts();
  }

  @Override
  public java.lang.String getPathInfo() {
    return getCurrent().getPathInfo();
  }

  @Override
  public java.lang.String getPathTranslated() {
    return getCurrent().getPathTranslated();
  }

  @Override
  public java.lang.String getRemoteUser() {
    return getCurrent().getRemoteUser();
  }

  @Override
  public java.lang.String getRequestURI() {
    return getCurrent().getRequestURI();
  }

  @Override
  public java.lang.String getRequestedSessionId() {
    return getCurrent().getRequestedSessionId();
  }

  @Override
  public java.lang.String getServletPath() {
    return getCurrent().getServletPath();
  }

  @Override
  public jakarta.servlet.http.HttpSession getSession() {
    return getCurrent().getSession();
  }

  @Override
  public jakarta.servlet.http.HttpSession getSession(boolean arg0) {
    return getCurrent().getSession(arg0);
  }

  @Override
  public java.util.Map<java.lang.String, java.lang.String> getTrailerFields() {
    return getCurrent().getTrailerFields();
  }

  @Override
  public java.security.Principal getUserPrincipal() {
    return getCurrent().getUserPrincipal();
  }

  @Override
  public boolean isRequestedSessionIdFromCookie() {
    return getCurrent().isRequestedSessionIdFromCookie();
  }

  @Override
  public boolean isRequestedSessionIdValid() {
    return getCurrent().isRequestedSessionIdValid();
  }

  @Override
  public boolean isTrailerFieldsReady() {
    return getCurrent().isTrailerFieldsReady();
  }

  @Override
  public boolean isUserInRole(java.lang.String arg0) {
    return getCurrent().isUserInRole(arg0);
  }

  @Override
  public void login(java.lang.String arg0, java.lang.String arg1) throws jakarta.servlet.ServletException {
    getCurrent().login(arg0, arg1);
  }

  @Override
  public void logout() throws jakarta.servlet.ServletException {
    getCurrent().logout();
  }

  @Override
  public jakarta.servlet.http.PushBuilder newPushBuilder() {
    return getCurrent().newPushBuilder();
  }

  @Override
  public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
    return getCurrent().upgrade(handlerClass);
  }

  // jakarta.servlet.ServletRequest

  @Override
  public long getContentLengthLong() {
    return getCurrent().getContentLengthLong();
  }

  @Override
  public java.io.BufferedReader getReader() throws java.io.IOException {
    return getCurrent().getReader();
  }

  @Override
  public jakarta.servlet.ServletInputStream getInputStream() throws java.io.IOException {
    return getCurrent().getInputStream();
  }

  @Override
  public jakarta.servlet.AsyncContext getAsyncContext() {
    return getCurrent().getAsyncContext();
  }

  @Override
  public java.lang.Object getAttribute(java.lang.String arg0) {
    return getCurrent().getAttribute(arg0);
  }

  @Override
  public java.util.Enumeration<java.lang.String> getAttributeNames() {
    return getCurrent().getAttributeNames();
  }

  @Override
  public java.lang.String getCharacterEncoding() {
    return getCurrent().getCharacterEncoding();
  }

  @Override
  public int getContentLength() {
    return getCurrent().getContentLength();
  }

  @Override
  public java.lang.String getContentType() {
    return getCurrent().getContentType();
  }

  @Override
  public jakarta.servlet.DispatcherType getDispatcherType() {
    return getCurrent().getDispatcherType();
  }

  @Override
  public java.lang.String getLocalAddr() {
    return getCurrent().getLocalAddr();
  }

  @Override
  public java.lang.String getLocalName() {
    return getCurrent().getLocalName();
  }

  @Override
  public int getLocalPort() {
    return getCurrent().getLocalPort();
  }

  @Override
  public java.util.Locale getLocale() {
    return getCurrent().getLocale();
  }

  @Override
  public java.util.Enumeration<java.util.Locale> getLocales() {
    return getCurrent().getLocales();
  }

  @Override
  public java.lang.String getParameter(java.lang.String arg0) {
    return getCurrent().getParameter(arg0);
  }

  @Override
  public java.util.Map<java.lang.String, java.lang.String[]> getParameterMap() {
    return getCurrent().getParameterMap();
  }

  @Override
  public java.util.Enumeration<java.lang.String> getParameterNames() {
    return getCurrent().getParameterNames();
  }

  @Override
  public java.lang.String[] getParameterValues(java.lang.String arg0) {
    return getCurrent().getParameterValues(arg0);
  }

  @Override
  public java.lang.String getProtocol() {
    return getCurrent().getProtocol();
  }

  @Override
  public java.lang.String getRemoteAddr() {
    return getCurrent().getRemoteAddr();
  }

  @Override
  public java.lang.String getRemoteHost() {
    return getCurrent().getRemoteHost();
  }

  @Override
  public int getRemotePort() {
    return getCurrent().getRemotePort();
  }

  @Override
  public jakarta.servlet.RequestDispatcher getRequestDispatcher(java.lang.String arg0) {
    return getCurrent().getRequestDispatcher(arg0);
  }

  @Override
  public java.lang.String getScheme() {
    return getCurrent().getScheme();
  }

  @Override
  public java.lang.String getServerName() {
    return getCurrent().getServerName();
  }

  @Override
  public int getServerPort() {
    return getCurrent().getServerPort();
  }

  @Override
  public jakarta.servlet.ServletContext getServletContext() {
    return getCurrent().getServletContext();
  }

  @Override
  public boolean isAsyncStarted() {
    return getCurrent().isAsyncStarted();
  }

  @Override
  public boolean isAsyncSupported() {
    return getCurrent().isAsyncSupported();
  }

  @Override
  public boolean isSecure() {
    return getCurrent().isSecure();
  }

  @Override
  public void removeAttribute(java.lang.String arg0) {
    getCurrent().removeAttribute(arg0);
  }

  @Override
  public void setAttribute(java.lang.String arg0, java.lang.Object arg1) {
    getCurrent().setAttribute(arg0, arg1);
  }

  @Override
  public void setCharacterEncoding(java.lang.String arg0) throws java.io.UnsupportedEncodingException {
    getCurrent().setCharacterEncoding(arg0);
  }

  @Override
  public jakarta.servlet.AsyncContext startAsync() throws java.lang.IllegalStateException {
    return getCurrent().startAsync();
  }

  @Override
  public jakarta.servlet.AsyncContext startAsync(jakarta.servlet.ServletRequest arg0,
      jakarta.servlet.ServletResponse arg1) throws java.lang.IllegalStateException {
    return getCurrent().startAsync(arg0, arg1);
  }

  @Override
  public String getRequestId() {
    return getCurrent().getRequestId();
  }

  @Override
  public String getProtocolRequestId() {
    return getCurrent().getProtocolRequestId();
  }

  @Override
  public ServletConnection getServletConnection() {
    return getCurrent().getServletConnection();
  }
}
