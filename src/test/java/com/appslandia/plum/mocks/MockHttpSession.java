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
    this.sessionId = nextSessionId();
  }

  @Override
  public String getId() {
    assertUninvalidated();
    return this.sessionId;
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
    return this.maxInactiveInterval;
  }

  @Override
  public void setMaxInactiveInterval(int maxInactiveInterval) {
    this.maxInactiveInterval = maxInactiveInterval;
  }

  @Override
  public ServletContext getServletContext() {
    return this.servletContext;
  }

  @Override
  public void invalidate() {
    assertUninvalidated();
    this.invalidated = true;
    clearAttributes();
  }

  public boolean isInvalidated() {
    return this.invalidated;
  }

  @Override
  public boolean isNew() {
    assertUninvalidated();
    throw new UnsupportedOperationException();
  }

  private void assertUninvalidated() {
    if (this.invalidated) {
      throw new IllegalStateException("Invalidated.");
    }
  }

  private static String nextSessionId() {
    return "session-" + ID_GENERATOR.incrementAndGet();
  }

  public String changeSessionId() {
    this.sessionId = nextSessionId();
    return this.sessionId;
  }

  @Override
  public Object getAttribute(String name) {
    assertUninvalidated();
    return this.attributes.get(name);
  }

  @Override
  public void setAttribute(String name, Object value) {
    assertUninvalidated();

    if (value != null) {
      this.attributes.put(name, value);
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

    var value = this.attributes.remove(name);
    if (value instanceof HttpSessionBindingListener) {
      ((HttpSessionBindingListener) value).valueUnbound(new HttpSessionBindingEvent(this, name, value));
    }
  }

  @Override
  public Enumeration<String> getAttributeNames() {
    assertUninvalidated();
    return Collections.enumeration(this.attributes.keySet());
  }

  private void clearAttributes() {
    for (var it = this.attributes.entrySet().iterator(); it.hasNext();) {
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
