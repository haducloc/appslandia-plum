// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.mocks;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionBindingEvent;
import jakarta.servlet.http.HttpSessionBindingListener;

/**
 *
 * @author Loc Ha
 *
 */
public class MockHttpSession implements HttpSession {

  private static final AtomicInteger ID_GENERATOR = new AtomicInteger(0);

  private String sessionId;
  private boolean invalidated = false;
  private int maxInactiveInterval = (int) TimeUnit.SECONDS.convert(30, TimeUnit.MINUTES);

  private ServletContext servletContext;
  private Map<String, Object> attributes = new HashMap<>();

  public MockHttpSession(ServletContext servletContext) {
    this.servletContext = servletContext;
    sessionId = nextSessionId();
  }

  @Override
  public String getId() {
    assertUninvalidated();
    return sessionId;
  }

  @Override
  public long getCreationTime() {
    assertUninvalidated();
    throw new UnsupportedOperationException();
  }

  @Override
  public long getLastAccessedTime() {
    assertUninvalidated();
    throw new UnsupportedOperationException();
  }

  @Override
  public int getMaxInactiveInterval() {
    assertUninvalidated();
    return maxInactiveInterval;
  }

  @Override
  public void setMaxInactiveInterval(int maxInactiveInterval) {
    this.maxInactiveInterval = maxInactiveInterval;
  }

  @Override
  public ServletContext getServletContext() {
    return servletContext;
  }

  @Override
  public void invalidate() {
    assertUninvalidated();
    invalidated = true;
    clearAttributes();
  }

  public boolean isInvalidated() {
    return invalidated;
  }

  @Override
  public boolean isNew() {
    assertUninvalidated();
    throw new UnsupportedOperationException();
  }

  private void assertUninvalidated() {
    if (invalidated) {
      throw new IllegalStateException("Invalidated.");
    }
  }

  private static String nextSessionId() {
    return "session-" + ID_GENERATOR.incrementAndGet();
  }

  public String changeSessionId() {
    sessionId = nextSessionId();
    return sessionId;
  }

  @Override
  public Object getAttribute(String name) {
    assertUninvalidated();
    return attributes.get(name);
  }

  @Override
  public void setAttribute(String name, Object value) {
    assertUninvalidated();

    if (value != null) {
      attributes.put(name, value);
      if (value instanceof HttpSessionBindingListener) {
        ((HttpSessionBindingListener) value).valueBound(new HttpSessionBindingEvent(this, name, value));
      }
    } else {
      removeAttribute(name);
    }
  }

  @Override
  public void removeAttribute(String name) {
    assertUninvalidated();

    var value = attributes.remove(name);
    if (value instanceof HttpSessionBindingListener) {
      ((HttpSessionBindingListener) value).valueUnbound(new HttpSessionBindingEvent(this, name, value));
    }
  }

  @Override
  public Enumeration<String> getAttributeNames() {
    assertUninvalidated();
    return Collections.enumeration(attributes.keySet());
  }

  private void clearAttributes() {
    for (var it = attributes.entrySet().iterator(); it.hasNext();) {
      var entry = it.next();
      var name = entry.getKey();
      var value = entry.getValue();
      it.remove();
      if (value instanceof HttpSessionBindingListener) {
        ((HttpSessionBindingListener) value).valueUnbound(new HttpSessionBindingEvent(this, name, value));
      }
    }
  }
}
