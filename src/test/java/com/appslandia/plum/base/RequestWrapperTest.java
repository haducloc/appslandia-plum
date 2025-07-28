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

package com.appslandia.plum.base;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.appslandia.plum.mocks.MockHttpServletRequest;
import com.appslandia.plum.mocks.MockServletContext;
import com.appslandia.plum.mocks.MockSessionCookieConfig;

/**
 *
 * @author Loc Ha
 *
 */
public class RequestWrapperTest {

  MockServletContext servletContext;

  @BeforeEach
  public void beforeEachTest() {
    servletContext = new MockServletContext(new MockSessionCookieConfig());
  }

  @Test
  public void test() {
    var request = new MockHttpServletRequest(servletContext);

    request.addParameter("p1", "v1");
    request.addParameter("p2", "v2");
    request.addParameter("p3", "v31", "v32");

    Map<String, String> pathParamMap = new HashMap<>();
    pathParamMap.put("p2", "v22");
    pathParamMap.put("p4", "v4");

    var requestWrapper = new RequestWrapper(request, pathParamMap);
    try {
      Assertions.assertEquals("v1", requestWrapper.getParameter("p1"));
      Assertions.assertEquals("v4", requestWrapper.getParameter("p4"));

      // p2 is path parameter
      // value orders
      Assertions.assertEquals("v22", requestWrapper.getParameterValues("p2")[0]);
      Assertions.assertEquals("v2", requestWrapper.getParameterValues("p2")[1]);

      Assertions.assertEquals("v31", requestWrapper.getParameterValues("p3")[0]);
      Assertions.assertEquals("v32", requestWrapper.getParameterValues("p3")[1]);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }
}
