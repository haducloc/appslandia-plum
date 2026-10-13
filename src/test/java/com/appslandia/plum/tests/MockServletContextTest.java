// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.appslandia.plum.mocks.MockServletContext;
import com.appslandia.plum.mocks.MockSessionCookieConfig;

/**
 *
 * @author Loc Ha
 *
 */
public class MockServletContextTest {

  @Test
  public void test() {
    var sc = new MockServletContext(new MockSessionCookieConfig());

    Assertions.assertNotNull(sc.getSessionCookieConfig());
    Assertions.assertEquals("/app", sc.getContextPath());

    sc.setAppDir("C:/webapps");
    Assertions.assertEquals("C:/webapps", sc.getAppDir());
  }

  @Test
  public void test_attributes() {
    var sc = new MockServletContext(new MockSessionCookieConfig());

    sc.setAttribute("location", "location1");
    Assertions.assertEquals("location1", sc.getAttribute("location"));

    sc.removeAttribute("location");
    Assertions.assertNull(sc.getAttribute("location"));
  }

  @Test
  public void test_initParameters() {
    var sc = new MockServletContext(new MockSessionCookieConfig());
    sc.setInitParameter("p1", "v1");
    sc.setInitParameter("p2", "v2");

    Assertions.assertEquals("v1", sc.getInitParameter("p1"));
  }
}
