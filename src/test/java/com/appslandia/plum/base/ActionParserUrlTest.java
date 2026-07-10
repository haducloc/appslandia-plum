// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Loc Ha
 *
 */
public class ActionParserUrlTest extends MockTestBase {

  ActionParser actionParser;
  RequestContextParser requestContextParser;

  @Override
  protected void initialize() {
    container.register(TestController.class, TestController.class);
    actionParser = container.getObject(ActionParser.class);
    requestContextParser = container.getObject(RequestContextParser.class);
  }

  @Test
  public void test_actionPathParam() {
    try {
      getCurrentRequest().setRequestURL("http://localhost/app/testController/actionPathParam/param1");
      requestContextParser.initRequestContext(getCurrentRequest(), getCurrentResponse());

      Map<String, Object> params = new HashMap<>();
      params.put("param1", "param1");
      params.put("param2", "param2");

      var url = actionParser.toActionUrl(getCurrentRequest(), "testController", "actionPathParam", params, false);
      Assertions.assertEquals("/app/testController/actionPathParam/param1?param2=param2", url);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_actionNoPathParams() {
    try {
      getCurrentRequest().setRequestURL("http://localhost/app/testController/actionNoPathParams");
      requestContextParser.initRequestContext(getCurrentRequest(), getCurrentResponse());

      var url = actionParser.toActionUrl(getCurrentRequest(), "testController", "actionNoPathParams", null, false);
      Assertions.assertEquals("/app/testController/actionNoPathParams", url);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_actionPathParams() {
    try {
      getCurrentRequest().setRequestURL("http://localhost/app/testController/actionPathParams/param1/param2");
      requestContextParser.initRequestContext(getCurrentRequest(), getCurrentResponse());

      Map<String, Object> params = new HashMap<>();
      params.put("param1", "param1");
      params.put("param2", "param2");

      var url = actionParser.toActionUrl(getCurrentRequest(), "testController", "actionPathParams", params, false);
      Assertions.assertEquals("/app/testController/actionPathParams/param1/param2", url);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_actionPathParams_pathParamsMissed() {
    try {
      getCurrentRequest().setRequestURL("http://localhost/app/testController/actionPathParams/param1/param2");
      requestContextParser.initRequestContext(getCurrentRequest(), getCurrentResponse());

      Map<String, Object> params = new HashMap<>();
      params.put("param1", "param1");

      actionParser.toActionUrl(getCurrentRequest(), "testController", "actionPathParams", params, false);
      Assertions.fail();

    } catch (Exception ex) {
    }
  }

  @Test
  public void test_actionSubPathParams() {
    try {
      getCurrentRequest().setRequestURL("http://localhost/app/testController/actionSubPathParams/param1-param2");
      requestContextParser.initRequestContext(getCurrentRequest(), getCurrentResponse());

      Map<String, Object> params = new HashMap<>();
      params.put("param1", "param-val-1");
      params.put("param2", "param2");

      var url = actionParser.toActionUrl(getCurrentRequest(), "testController", "actionSubPathParams", params, false);
      Assertions.assertEquals("/app/testController/actionSubPathParams/param-val-1-param2", url);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_actionSubPathParams_pathParamsMissed() {
    try {
      getCurrentRequest().setRequestURL("http://localhost/app/testController/actionSubPathParams/param1-param2");
      requestContextParser.initRequestContext(getCurrentRequest(), getCurrentResponse());

      Map<String, Object> params = new HashMap<>();
      params.put("param1", "param-val-1");

      actionParser.toActionUrl(getCurrentRequest(), "testController", "actionSubPathParams", params, false);
      Assertions.fail();

    } catch (Exception ex) {
    }
  }

  @Test
  public void test_actionSubPathParams_withQueryParams() {
    try {
      getCurrentRequest().setRequestURL("http://localhost/app/testController/actionSubPathParams/param1-param2");
      requestContextParser.initRequestContext(getCurrentRequest(), getCurrentResponse());

      Map<String, Object> params = new LinkedHashMap<>();
      params.put("param1", "param-val-1");
      params.put("param2", "param2");
      params.put("param3", "param3");
      params.put("param4", "param4");

      var url = actionParser.toActionUrl(getCurrentRequest(), "testController", "actionSubPathParams", params, false);
      Assertions.assertEquals("/app/testController/actionSubPathParams/param-val-1-param2?param3=param3&param4=param4",
          url);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Controller("testController")
  public static class TestController extends ControllerBase {

    @HttpGet
    public void index() {
    }

    @HttpGet
    public void actionNoPathParams() {
    }

    @HttpGet
    @PathParams("/{param1}")
    public void actionPathParam() {
    }

    @HttpGet
    @PathParams("/{param1}/{param2}")
    public void actionPathParams() {
    }

    @HttpGet
    @PathParams("/{param1}-{param2}")
    public void actionSubPathParams() {
    }
  }
}
