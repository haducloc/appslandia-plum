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

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.CollectionUtils;
import com.appslandia.common.utils.StringUtils;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterRegistration;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;
import jakarta.servlet.SessionCookieConfig;
import jakarta.servlet.SessionTrackingMode;
import jakarta.servlet.descriptor.JspConfigDescriptor;

/**
 *
 * @author Loc Ha
 *
 */
public class MockServletContext implements ServletContext {

  private static final Set<SessionTrackingMode> DEFAULT_SESSION_TRACKING_MODES = CollectionUtils
      .unmodifiableSet(SessionTrackingMode.COOKIE, SessionTrackingMode.URL, SessionTrackingMode.SSL);

  private String appDir = "C:/app";
  final String contextPath = "/app";
  private MockSessionCookieConfig sessionCookieConfig;

  private Map<String, Object> attributes = new HashMap<>();
  private Map<String, String> initParameters = new HashMap<>();

  private Set<SessionTrackingMode> sessionTrackingModes;

  public MockServletContext() {
    this(new MockSessionCookieConfig());
  }

  public MockServletContext(MockSessionCookieConfig sessionCookieConfig) {
    this.sessionCookieConfig = sessionCookieConfig;
  }

  public Map<String, Object> getAttributes() {
    return this.attributes;
  }

  @Override
  public String getContextPath() {
    Asserts.notNull(this.contextPath, "appDir is required.");
    return this.contextPath;
  }

  public String getAppDir() {
    Asserts.notNull(this.appDir, "appDir is required.");
    return this.appDir;
  }

  public MockServletContext setAppDir(String appDir) {
    this.appDir = appDir;
    return this;
  }

  @Override
  public ServletContext getContext(String uripath) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getMajorVersion() {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getMinorVersion() {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getEffectiveMajorVersion() {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getEffectiveMinorVersion() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getMimeType(String file) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Set<String> getResourcePaths(String path) {
    throw new UnsupportedOperationException();
  }

  @Override
  public URL getResource(String path) throws MalformedURLException {
    throw new UnsupportedOperationException();
  }

  @Override
  public InputStream getResourceAsStream(String path) {
    throw new UnsupportedOperationException();
  }

  @Override
  public RequestDispatcher getRequestDispatcher(String path) {
    return new MockRequestDispatcher(path);
  }

  @Override
  public RequestDispatcher getNamedDispatcher(String name) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void log(String msg) {
  }

  @Override
  public void log(String message, Throwable throwable) {
  }

  @Override
  public String getRealPath(String path) {
    if (StringUtils.isNullOrEmpty(path) || path.equals("/")) {
      return getAppDir();
    }
    return getAppDir() + path;
  }

  @Override
  public String getServerInfo() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getInitParameter(String name) {
    return this.initParameters.get(name);
  }

  @Override
  public boolean setInitParameter(String name, String value) {
    this.initParameters.put(name, value);
    return true;
  }

  @Override
  public Enumeration<String> getInitParameterNames() {
    return Collections.enumeration(this.initParameters.keySet());
  }

  @Override
  public Object getAttribute(String name) {
    return this.attributes.get(name);
  }

  @Override
  public void setAttribute(String name, Object value) {
    if (value != null) {
      this.attributes.put(name, value);
    } else {
      this.attributes.remove(name);
    }
  }

  @Override
  public void removeAttribute(String name) {
    this.attributes.remove(name);
  }

  @Override
  public Enumeration<String> getAttributeNames() {
    return Collections.enumeration(this.attributes.keySet());
  }

  @Override
  public String getServletContextName() {
    throw new UnsupportedOperationException();
  }

  @Override
  public jakarta.servlet.ServletRegistration.Dynamic addServlet(String servletName, String className) {
    throw new UnsupportedOperationException();
  }

  @Override
  public jakarta.servlet.ServletRegistration.Dynamic addServlet(String servletName, Servlet servlet) {
    throw new UnsupportedOperationException();
  }

  @Override
  public jakarta.servlet.ServletRegistration.Dynamic addServlet(String servletName,
      Class<? extends Servlet> servletClass) {
    throw new UnsupportedOperationException();
  }

  @Override
  public <T extends Servlet> T createServlet(Class<T> clazz) throws ServletException {
    throw new UnsupportedOperationException();
  }

  @Override
  public ServletRegistration getServletRegistration(String servletName) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Map<String, ? extends ServletRegistration> getServletRegistrations() {
    throw new UnsupportedOperationException();
  }

  @Override
  public jakarta.servlet.FilterRegistration.Dynamic addFilter(String filterName, String className) {
    throw new UnsupportedOperationException();
  }

  @Override
  public jakarta.servlet.FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {
    throw new UnsupportedOperationException();
  }

  @Override
  public jakarta.servlet.FilterRegistration.Dynamic addFilter(String filterName, Class<? extends Filter> filterClass) {
    throw new UnsupportedOperationException();
  }

  @Override
  public <T extends Filter> T createFilter(Class<T> clazz) throws ServletException {
    throw new UnsupportedOperationException();
  }

  @Override
  public FilterRegistration getFilterRegistration(String filterName) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
    throw new UnsupportedOperationException();
  }

  @Override
  public SessionCookieConfig getSessionCookieConfig() {
    return this.sessionCookieConfig;
  }

  @Override
  public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) {
    this.sessionTrackingModes = sessionTrackingModes;
  }

  @Override
  public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
    return DEFAULT_SESSION_TRACKING_MODES;
  }

  @Override
  public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
    return (this.sessionTrackingModes != null ? Collections.unmodifiableSet(this.sessionTrackingModes)
        : DEFAULT_SESSION_TRACKING_MODES);
  }

  @Override
  public void addListener(String className) {
    throw new UnsupportedOperationException();
  }

  @Override
  public <T extends EventListener> void addListener(T t) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void addListener(Class<? extends EventListener> listenerClass) {
    throw new UnsupportedOperationException();
  }

  @Override
  public <T extends EventListener> T createListener(Class<T> clazz) throws ServletException {
    throw new UnsupportedOperationException();
  }

  @Override
  public JspConfigDescriptor getJspConfigDescriptor() {
    throw new UnsupportedOperationException();
  }

  @Override
  public ClassLoader getClassLoader() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void declareRoles(String... roleNames) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getVirtualServerName() {
    throw new UnsupportedOperationException();
  }

  @Override
  public jakarta.servlet.ServletRegistration.Dynamic addJspFile(String servletName, String jspFile) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getRequestCharacterEncoding() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getResponseCharacterEncoding() {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getSessionTimeout() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setRequestCharacterEncoding(String encoding) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setResponseCharacterEncoding(String encoding) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setSessionTimeout(int sessionTimeout) {
    throw new UnsupportedOperationException();
  }
}
