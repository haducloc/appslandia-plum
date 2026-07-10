// Licensed under the MIT License.
// See LICENSE file in the project root for details.

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
  public void initServletContext() {
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

    var requestWrapper = new HttpRequestFacade(request, pathParamMap);
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
