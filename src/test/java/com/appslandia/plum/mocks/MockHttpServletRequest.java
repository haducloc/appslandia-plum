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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.appslandia.common.base.CaseInsensitiveMap;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.CollectionUtils;
import com.appslandia.common.utils.SplitUtils;
import com.appslandia.plum.base.UserPrincipal;
import com.appslandia.plum.utils.HeaderUtils;
import com.appslandia.plum.utils.SecurityUtils;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConnection;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpUpgradeHandler;
import jakarta.servlet.http.Part;

/**
 *
 * @author Loc Ha
 *
 */
public class MockHttpServletRequest implements HttpServletRequest {

  private MockHttpSession session;
  private ServletContext servletContext;

  private byte[] content = {};
  private String contentType;
  private String characterEncoding;

  private MockServletInputStream inputStream;
  private BufferedReader reader;
  private Locale[] locales = { Locale.US };

  private String scheme;
  private String serverName;
  private int serverPort;
  private String remoteAddr = "127.0.0.1";
  private boolean isAsyncStarted = false;
  private boolean isRequestedSessionIdValid = true;

  private String authType;
  private Principal userPrincipal;

  private DispatcherType dispatcherType = DispatcherType.REQUEST;
  private String method = "GET";

  // requestURI = contextPath + servletPath + pathInfo
  // pathTranslated = <documentRoot> + pathInfo

  private String requestURI;
  private String queryString;
  private String servletPath;
  private String pathInfo;

  private Map<String, Object> attributes = new HashMap<>();
  private Map<String, String[]> parameterMap = new HashMap<>();
  private List<Cookie> cookies = new ArrayList<>();
  private Map<String, MockHttpHeader> headers = new CaseInsensitiveMap<>();

  private int requestId;
  private static final AtomicInteger SEQ = new AtomicInteger();

  public MockHttpServletRequest(ServletContext servletContext) {
    this.servletContext = servletContext;
    requestId = SEQ.incrementAndGet();
  }

  protected byte[] getContent() {
    Asserts.notNull(content, "content is required.");
    return content;
  }

  public MockHttpServletRequest setContent(byte[] content) {
    this.content = content;
    return this;
  }

  @Override
  public String getContentType() {
    return contentType;
  }

  public MockHttpServletRequest setContentType(String contentType) {
    this.contentType = contentType;
    return this;
  }

  @Override
  public int getContentLength() {
    return getContent().length;
  }

  @Override
  public long getContentLengthLong() {
    return getContentLength();
  }

  @Override
  public String getCharacterEncoding() {
    return characterEncoding;
  }

  @Override
  public void setCharacterEncoding(String characterEncoding) {
    this.characterEncoding = characterEncoding;
  }

  @Override
  public MockServletInputStream getInputStream() throws IOException {
    if (reader != null) {
      throw new IllegalStateException("getReader has been called on this request.");
    }
    if (inputStream == null) {
      inputStream = new MockServletInputStream(new ByteArrayInputStream(getContent()));
    }
    return inputStream;
  }

  @Override
  public BufferedReader getReader() throws IOException {
    if (inputStream != null) {
      throw new IllegalStateException("getInputStream has been called on this request.");
    }
    if (reader == null) {
      var encoding = characterEncoding;
      if (encoding == null) {
        encoding = StandardCharsets.ISO_8859_1.name();
      }
      reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(getContent()), encoding));
    }
    return reader;
  }

  @Override
  public String getProtocol() {
    return "HTTP/1.1";
  }

  @Override
  public String getScheme() {
    Asserts.notNull(scheme, "scheme is required.");
    return scheme;
  }

  @Override
  public boolean isSecure() {
    return getScheme().equals("https");
  }

  @Override
  public String getServerName() {
    Asserts.notNull(serverName, "serverName is required.");
    return serverName;
  }

  @Override
  public int getServerPort() {
    Asserts.isTrue(serverPort >= 0, "serverPort is required.");
    return serverPort;
  }

  @Override
  public String getRemoteAddr() {
    Asserts.notNull(serverName, "remoteAddr is required.");
    return remoteAddr;
  }

  @Override
  public String getRemoteHost() {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getRemotePort() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getLocalAddr() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getLocalName() {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getLocalPort() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getMethod() {
    Asserts.notNull(method, "method is required.");
    return method;
  }

  public MockHttpServletRequest setMethod(String method) {
    this.method = method;
    return this;
  }

  @Override
  public String getRequestURI() {
    Asserts.notNull(requestURI, "requestURI is required.");
    return requestURI;
  }

  public MockHttpServletRequest setRequestURL(String requestURL) {
    try {
      var url = new URI(requestURL).toURL();
      serverName = url.getHost();

      Asserts.isTrue("http".equals(url.getProtocol()) || "https".equals(url.getProtocol()), "http|https is required.");
      scheme = url.getProtocol();
      serverPort = (url.getPort() >= 0) ? url.getPort() : url.getDefaultPort();

      Asserts.isTrue(url.getPath().startsWith("/app"), "The contextPath /app must be used.");
      requestURI = url.getPath();

      var relativePath = url.getPath().substring(4);
      if (relativePath.length() == 0) {
        relativePath = "/";
      }

      var items = SplitUtils.split(relativePath, '/');
      if (items.length <= 1) {
        servletPath = relativePath;
        pathInfo = null;
      } else {
        servletPath = "/" + items[0];

        pathInfo = relativePath.substring(servletPath.length());
        if (pathInfo.length() == 0) {
          pathInfo = null;
        }
      }
      queryString = url.getQuery();
      return this;
    } catch (Exception ex) {
      throw new IllegalArgumentException(requestURL);
    }
  }

  @Override
  public StringBuffer getRequestURL() {
    var sb = new StringBuffer(getScheme()).append("://").append(getServerName());
    if ((getScheme().equals("http") && (getServerPort() != 80))
        || (getScheme().equals("https") && (getServerPort() != 443))) {
      sb.append(':').append(getServerPort());
    }
    sb.append(getRequestURI());
    return sb;
  }

  @Override
  public String getQueryString() {
    return queryString;
  }

  @Override
  public String getServletPath() {
    Asserts.notNull(servletPath, "servletPath is required.");
    return servletPath;
  }

  @Override
  public String getPathInfo() {
    return pathInfo;
  }

  @Override
  public String getPathTranslated() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getContextPath() {
    return servletContext.getContextPath();
  }

  @Override
  public Locale getLocale() {
    Asserts.hasElements(locales, "locales is required.");
    return locales[0];
  }

  @Override
  public Enumeration<Locale> getLocales() {
    Asserts.hasElements(locales, "locales is required.");
    return Collections.enumeration(CollectionUtils.toList(locales));
  }

  public MockHttpServletRequest setLocales(Locale[] locales) {
    this.locales = locales;
    return this;
  }

  @Override
  public RequestDispatcher getRequestDispatcher(String path) {
    return new MockRequestDispatcher(path);
  }

  @Override
  public DispatcherType getDispatcherType() {
    Asserts.notNull(dispatcherType, "dispatcherType is required.");
    return dispatcherType;
  }

  public MockHttpServletRequest setDispatcherType(DispatcherType dispatcherType) {
    this.dispatcherType = dispatcherType;
    return this;
  }

  @Override
  public ServletContext getServletContext() {
    return servletContext;
  }

  @Override
  public AsyncContext startAsync() throws IllegalStateException {
    isAsyncStarted = true;
    var response = (ServletResponse) getAttribute(ServletResponse.class.getName());
    return new MockAsyncContext(this, Asserts.notNull(response));
  }

  @Override
  public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse)
      throws IllegalStateException {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isAsyncStarted() {
    return isAsyncStarted;
  }

  @Override
  public boolean isAsyncSupported() {
    throw new UnsupportedOperationException();
  }

  @Override
  public AsyncContext getAsyncContext() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Cookie[] getCookies() {
    if (cookies.isEmpty()) {
      return null;
    }
    return cookies.toArray(new Cookie[cookies.size()]);
  }

  public Cookie getCookie(String name) {
    return MockServletUtils.getCookie(cookies, name);
  }

  public MockHttpServletRequest addCookie(Cookie cookie) {
    cookies.add(cookie);
    return this;
  }

  public MockHttpServletRequest addSessionCookie(String value) {
    cookies.add(MockServletUtils.createSessionCookie(servletContext, value));
    return this;
  }

  public MockHttpServletRequest setHeaderValues(String name, String... values) {
    MockServletUtils.setHeaderValues(headers, name, values);
    return this;
  }

  public MockHttpServletRequest addHeaderValues(String name, String... values) {
    MockServletUtils.addHeaderValues(headers, name, values);
    return this;
  }

  public MockHttpServletRequest setDateHeader(String name, long date) {
    setHeaderValues(name, HeaderUtils.toDateHeaderString(date));
    return this;
  }

  public MockHttpServletRequest addDateHeader(String name, long date) {
    addHeaderValues(name, HeaderUtils.toDateHeaderString(date));
    return this;
  }

  public MockHttpServletRequest setHeader(String name, String value) {
    setHeaderValues(name, value);
    return this;
  }

  public MockHttpServletRequest addHeader(String name, String value) {
    addHeaderValues(name, value);
    return this;
  }

  public MockHttpServletRequest setIntHeader(String name, int value) {
    setHeaderValues(name, Integer.toString(value));
    return this;
  }

  public MockHttpServletRequest addIntHeader(String name, int value) {
    addHeaderValues(name, Integer.toString(value));
    return this;
  }

  @Override
  public long getDateHeader(String name) {
    return MockServletUtils.getDateHeader(headers, name);
  }

  @Override
  public int getIntHeader(String name) {
    return MockServletUtils.getIntHeader(headers, name);
  }

  @Override
  public String getHeader(String name) {
    var header = MockHttpHeader.getByName(headers, name);
    return (header != null) ? header.getValue() : null;
  }

  @Override
  public Enumeration<String> getHeaders(String name) {
    var header = MockHttpHeader.getByName(headers, name);
    return (header != null) ? Collections.enumeration(header.getValues()) : Collections.emptyEnumeration();
  }

  @Override
  public Enumeration<String> getHeaderNames() {
    return Collections.enumeration(headers.keySet());
  }

  @Override
  public String getParameter(String name) {
    var values = parameterMap.get(name);
    return (values != null) ? values[0] : null;
  }

  @Override
  public Enumeration<String> getParameterNames() {
    return Collections.enumeration(parameterMap.keySet());
  }

  @Override
  public String[] getParameterValues(String name) {
    return parameterMap.get(name);
  }

  @Override
  public Map<String, String[]> getParameterMap() {
    return parameterMap;
  }

  public MockHttpServletRequest addParameter(String name, String... values) {
    MockServletUtils.addParameter(parameterMap, name, values);
    return this;
  }

  public MockHttpServletRequest setParameter(String name, String... values) {
    parameterMap.remove(name);
    MockServletUtils.addParameter(parameterMap, name, values);
    return this;
  }

  @Override
  public Object getAttribute(String name) {
    return attributes.get(name);
  }

  @Override
  public void setAttribute(String name, Object value) {
    if (value != null) {
      attributes.put(name, value);
    } else {
      removeAttribute(name);
    }
  }

  @Override
  public void removeAttribute(String name) {
    attributes.remove(name);
  }

  @Override
  public Enumeration<String> getAttributeNames() {
    return Collections.enumeration(attributes.keySet());
  }

  @Override
  public String getAuthType() {
    if (userPrincipal == null) {
      return null;
    }
    return authType;
  }

  @Override
  public String getRemoteUser() {
    if (userPrincipal == null) {
      return null;
    }
    return userPrincipal.getName();
  }

  @Override
  public boolean isUserInRole(String role) {
    if (userPrincipal == null) {
      return false;
    }
    Asserts.isTrue(userPrincipal instanceof UserPrincipal);

    var userRoles = (String) ((UserPrincipal) userPrincipal).get(UserPrincipal.ATTRIBUTE_ROLES);
    var roles = SecurityUtils.parseUserRoles(userRoles);
    return Arrays.stream(roles).anyMatch(r -> role.equalsIgnoreCase(r));
  }

  @Override
  public Principal getUserPrincipal() {
    return userPrincipal;
  }

  public MockHttpServletRequest setUserPrincipal(Principal userPrincipal) {
    this.userPrincipal = userPrincipal;
    return this;
  }

  @Override
  public MockHttpSession getSession(boolean create) {
    if (session != null && session.isInvalidated()) {
      session = null;
    }
    if (create) {
      if (session == null) {
        session = new MockHttpSession(servletContext);
      }
    }
    return session;
  }

  @Override
  public MockHttpSession getSession() {
    return getSession(true);
  }

  public MockHttpServletRequest setSession(MockHttpSession session) {
    this.session = session;
    return this;
  }

  @Override
  public String changeSessionId() {
    if (session != null) {
      return session.changeSessionId();
    }
    return null;
  }

  @Override
  public boolean isRequestedSessionIdValid() {
    return isRequestedSessionIdValid;
  }

  public MockHttpServletRequest setRequestedSessionIdValid(boolean isRequestedSessionIdValid) {
    this.isRequestedSessionIdValid = isRequestedSessionIdValid;
    return this;
  }

  @Override
  public String getRequestedSessionId() {
    return getCookie(servletContext.getSessionCookieConfig().getName()).getValue();
  }

  @Override
  public boolean isRequestedSessionIdFromCookie() {
    return true;
  }

  @Override
  public boolean isRequestedSessionIdFromURL() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void login(String remoteUser, String password) throws ServletException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void logout() throws ServletException {
    userPrincipal = null;
    authType = null;
  }

  @Override
  public Collection<Part> getParts() throws IOException, ServletException {
    throw new UnsupportedOperationException();
  }

  @Override
  public Part getPart(String name) throws IOException, ServletException {
    throw new UnsupportedOperationException();
  }

  @Override
  public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getRequestId() {
    return Integer.toString(requestId);
  }

  @Override
  public String getProtocolRequestId() {
    return null;
  }

  @Override
  public ServletConnection getServletConnection() {
    throw new UnsupportedOperationException();
  }
}
