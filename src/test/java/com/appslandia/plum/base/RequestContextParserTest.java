// Licensed under the MIT License.
// See LICENSE file in the project root for details.

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
    requestContextParser.initRequestContext(getCurrentRequest(), getCurrentResponse());

    var requestContext = (RequestContext) getCurrentRequest().getAttribute(RequestContext.REQUEST_ATTRIBUTE_ID);
    Assertions.assertNotNull(requestContext.getActionDesc());

    Assertions.assertFalse(requestContext.isPathLanguage());
    Assertions.assertEquals("en", requestContext.getLanguage().getId());
  }

  @Test
  public void test_noAction() {
    getCurrentRequest().setRequestURL("http://localhost/app/testController/noAction");
    requestContextParser.initRequestContext(getCurrentRequest(), getCurrentResponse());

    var requestContext = (RequestContext) getCurrentRequest().getAttribute(RequestContext.REQUEST_ATTRIBUTE_ID);
    Assertions.assertNull(requestContext.getActionDesc());

    Assertions.assertFalse(requestContext.isPathLanguage());
    Assertions.assertEquals("en", requestContext.getLanguage().getId());
  }

  @Controller("testController")
  public static class TestController extends ControllerBase {

    @HttpGet
    public void index() {
    }
  }
}
