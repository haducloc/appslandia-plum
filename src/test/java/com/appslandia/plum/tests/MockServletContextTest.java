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
