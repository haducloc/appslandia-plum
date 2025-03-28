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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Loc Ha
 *
 */
public class RequestContextParserTest extends MockTestBase {

  RequestContextParser requestContextParser;

  @Override
  protected void initialize() {
    container.register(TestController.class, TestController.class);
    requestContextParser = container.getObject(RequestContextParser.class);
  }

  @Test
  public void test_defaultLanguage() {
    getCurrentRequest().setRequestURL("http://localhost/app/testController/index");
    requestContextParser.parse(getCurrentRequest(), getCurrentResponse());

    RequestContext requestContext = (RequestContext) getCurrentRequest()
        .getAttribute(RequestContext.REQUEST_ATTRIBUTE_ID);
    Assertions.assertNotNull(requestContext.getActionDesc());

    Assertions.assertFalse(requestContext.isPathLanguage());
    Assertions.assertEquals("en", requestContext.getLanguage().getId());
  }

  @Test
  public void test_noActionDesc() {
    getCurrentRequest().setRequestURL("http://localhost/app/testController/noAction");
    requestContextParser.parse(getCurrentRequest(), getCurrentResponse());

    RequestContext requestContext = (RequestContext) getCurrentRequest()
        .getAttribute(RequestContext.REQUEST_ATTRIBUTE_ID);
    Assertions.assertNull(requestContext.getActionDesc());

    Assertions.assertFalse(requestContext.isPathLanguage());
    Assertions.assertEquals("en", requestContext.getLanguage().getId());
  }

  @Controller("testController")
  public static class TestController {

    @HttpGet
    public void index() {
    }
  }
}
