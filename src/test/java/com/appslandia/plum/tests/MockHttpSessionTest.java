// Licensed under the MIT License.
// See LICENSE file in the project root for details.

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
  public void initServletContext() {
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
