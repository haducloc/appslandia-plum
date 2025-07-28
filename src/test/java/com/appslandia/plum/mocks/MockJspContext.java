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
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import com.appslandia.common.utils.Asserts;

import jakarta.el.ELContext;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.PageContext;

/**
 *
 * @author Loc Ha
 *
 */
public class MockJspContext extends PageContext {

  private HttpServletRequest request;
  private HttpServletResponse response;
  private JspWriter jspWriter;

  private ServletConfig servletConfig;
  private Exception exception;

  final Map<String, Object> pageScopeMap = new HashMap<>();

  public MockJspContext(HttpServletRequest request, HttpServletResponse response) {
    this(request, response, new MockJspWriter());
  }

  public MockJspContext(HttpServletRequest request, HttpServletResponse response, JspWriter jspWriter) {
    this.request = request;
    this.response = response;
    this.jspWriter = jspWriter;
  }

  @Override
  public void initialize(Servlet servlet, ServletRequest request, ServletResponse response, String errorPageURL,
      boolean needsSession, int bufferSize, boolean autoFlush)
      throws IOException, IllegalStateException, IllegalArgumentException {
  }

  @Override
  public void release() {
  }

  @Override
  public HttpSession getSession() {
    return this.request.getSession();
  }

  @Override
  public Object getPage() {
    throw new UnsupportedOperationException();
  }

  @Override
  public ServletRequest getRequest() {
    return this.request;
  }

  @Override
  public ServletResponse getResponse() {
    return this.response;
  }

  @Override
  public Exception getException() {
    return this.exception;
  }

  public void setException(Exception exception) {
    this.exception = exception;
  }

  @Override
  public ServletConfig getServletConfig() {
    Asserts.notNull(this.servletConfig, "servletConfig is required.");
    return this.servletConfig;
  }

  public void setServletConfig(MockServletConfig servletConfig) {
    this.servletConfig = servletConfig;
  }

  @Override
  public ServletContext getServletContext() {
    return this.request.getServletContext();
  }

  @Override
  public void forward(String relativeUrlPath) throws ServletException, IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void include(String relativeUrlPath) throws ServletException, IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void include(String relativeUrlPath, boolean flush) throws ServletException, IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void handlePageException(Exception ex) throws ServletException, IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void handlePageException(Throwable t) throws ServletException, IOException {
    throw new UnsupportedOperationException();
  }

  private Map<String, Object> getScopeMap(int scope) {
    if (scope == PAGE_SCOPE) {
      return this.pageScopeMap;
    }
    if (scope == REQUEST_SCOPE) {
      return getRequestScopeMap();
    }
    if (scope == SESSION_SCOPE) {
      return getSessionScopeMap();
    }
    if (scope == APPLICATION_SCOPE) {
      return getApplicationScopeMap();
    }
    throw new IllegalArgumentException("The given scope is invalid.");
  }

  @Override
  public void setAttribute(String name, Object value) {
    setAttribute(name, value, PAGE_SCOPE);
  }

  @Override
  public void setAttribute(String name, Object value, int scope) {
    if (scope == PAGE_SCOPE) {
      if (value == null) {
        this.pageScopeMap.remove(name);
      } else {
        this.pageScopeMap.put(name, value);
      }
      return;
    }
    if (scope == REQUEST_SCOPE) {
      if (value == null) {
        this.request.removeAttribute(name);
      } else {
        this.request.setAttribute(name, value);
      }
      return;
    }
    if (scope == SESSION_SCOPE) {
      if (value == null) {
        if (this.request.getSession(false) != null) {
          this.request.getSession().removeAttribute(name);
        }
      } else {
        this.request.getSession().setAttribute(name, value);
      }
      return;
    }
    if (scope == APPLICATION_SCOPE) {
      if (value == null) {
        getServletContext().removeAttribute(name);
      } else {
        getServletContext().setAttribute(name, value);
      }
      return;
    }
    throw new IllegalArgumentException("The given scope is invalid.");
  }

  @Override
  public Object getAttribute(String name) {
    return getAttribute(name, PAGE_SCOPE);
  }

  @Override
  public Object getAttribute(String name, int scope) {
    return getScopeMap(scope).get(name);
  }

  @Override
  public Object findAttribute(String name) {
    var scope = getAttributesScope(name);
    if (scope > 0) {
      return getScopeMap(scope).get(name);
    }
    return null;
  }

  @Override
  public void removeAttribute(String name) {
    this.pageScopeMap.remove(name);
    this.request.removeAttribute(name);

    if (this.request.getSession(false) != null) {
      this.request.getSession().removeAttribute(name);
    }
    getServletContext().removeAttribute(name);
  }

  @Override
  public void removeAttribute(String name, int scope) {
    setAttribute(name, null, scope);
  }

  @Override
  public int getAttributesScope(String name) {
    if (this.pageScopeMap.containsKey(name)) {
      return PAGE_SCOPE;
    }
    if (getRequestScopeMap().containsKey(name)) {
      return REQUEST_SCOPE;
    }
    if (getSessionScopeMap().containsKey(name)) {
      return SESSION_SCOPE;
    }
    if (getApplicationScopeMap().containsKey(name)) {
      return APPLICATION_SCOPE;
    }
    return 0;
  }

  @Override
  public Enumeration<String> getAttributeNamesInScope(int scope) {
    return Collections.enumeration(this.getScopeMap(scope).keySet());
  }

  @Override
  public JspWriter getOut() {
    return this.jspWriter;
  }

  @Deprecated
  @Override
  public jakarta.servlet.jsp.el.ExpressionEvaluator getExpressionEvaluator() {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  public jakarta.servlet.jsp.el.VariableResolver getVariableResolver() {
    throw new UnsupportedOperationException();
  }

  @Override
  public ELContext getELContext() {
    throw new UnsupportedOperationException();
  }

  private Map<String, Object> getRequestScopeMap() {
    Map<String, Object> m = new HashMap<>();
    var en = this.request.getAttributeNames();
    while (en.hasMoreElements()) {
      var name = en.nextElement();
      m.put(name, this.request.getAttribute(name));
    }
    return m;
  }

  private Map<String, Object> getSessionScopeMap() {
    Map<String, Object> m = new HashMap<>();
    if (this.request.getSession(false) == null) {
      return m;
    }
    var en = this.request.getSession().getAttributeNames();
    while (en.hasMoreElements()) {
      var name = en.nextElement();
      m.put(name, this.request.getSession().getAttribute(name));
    }
    return m;
  }

  private Map<String, Object> getApplicationScopeMap() {
    Map<String, Object> m = new HashMap<>();
    var en = getServletContext().getAttributeNames();
    while (en.hasMoreElements()) {
      var name = en.nextElement();
      m.put(name, getServletContext().getAttribute(name));
    }
    return m;
  }
}
