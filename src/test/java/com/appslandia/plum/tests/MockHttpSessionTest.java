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

package com.appslandia.plum.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.appslandia.plum.mocks.MockHttpSession;
import com.appslandia.plum.mocks.MockServletContext;
import com.appslandia.plum.mocks.MockSessionCookieConfig;

/**
 *
 * @author Loc Ha
 *
 */
public class MockHttpSessionTest {

  MockServletContext servletContext;

  @BeforeEach
  public void beforeEachTest() {
    servletContext = new MockServletContext(new MockSessionCookieConfig());
  }

  @Test
  public void test() {
    var session = new MockHttpSession(servletContext);
    Assertions.assertNotNull(session.getServletContext());

    session.setMaxInactiveInterval(1800);
    Assertions.assertEquals(1800, session.getMaxInactiveInterval());
  }

  @Test
  public void test_attributes() {
    var session = new MockHttpSession(servletContext);
    session.setAttribute("attr1", "value1");
    session.setAttribute("attr2", "value2");

    Assertions.assertEquals("value1", session.getAttribute("attr1"));
    Assertions.assertNull(session.getAttribute("attr3"));

    session.removeAttribute("attr2");
    Assertions.assertNull(session.getAttribute("attr2"));
  }

  @Test
  public void test_invalidate() {
    var session = new MockHttpSession(servletContext);
    Assertions.assertFalse(session.isInvalidated());

    session.invalidate();
    Assertions.assertTrue(session.isInvalidated());
  }

  @Test
  public void test_changeSessionId() {
    var session = new MockHttpSession(servletContext);
    var oldId = session.getId();

    session.changeSessionId();
    Assertions.assertNotEquals(session.getId(), oldId);
  }
}
