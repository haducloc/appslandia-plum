// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.appslandia.common.base.Out;
import com.appslandia.common.base.Params;

/**
 *
 * @author Loc Ha
 *
 */
public class ActionParserTest extends MockTestBase {

  ActionParser actionParser;

  @Override
  protected void initialize() {
    container.register(TestController.class, TestController.class);
    actionParser = container.getObject(ActionParser.class);
  }

  @Test
  public void test_index() {
    try {
      getCurrentRequest().setRequestURL("http://localhost/app/testController");
      var pathItems = RequestContextParser.parsePathItems(getCurrentRequest());

      var pathParamsOut = new Out<Map<String, String>>();
      var actionDesc = actionParser.parse(pathItems, pathParamsOut);
      Assertions.assertNotNull(actionDesc);

      Assertions.assertEquals("index", actionDesc.getAction());
      Assertions.assertEquals("testController", actionDesc.getController());
      Assertions.assertNull(pathParamsOut.value);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_actionNoPathParams() {
    try {
      getCurrentRequest().setRequestURL("http://localhost/app/testController/actionNoPathParams");
      var pathItems = RequestContextParser.parsePathItems(getCurrentRequest());

      var pathParamsOut = new Out<Map<String, String>>();
      var actionDesc = actionParser.parse(pathItems, pathParamsOut);
      Assertions.assertNotNull(actionDesc);

      Assertions.assertEquals("actionNoPathParams", actionDesc.getAction());
      Assertions.assertEquals("testController", actionDesc.getController());
      Assertions.assertNull(pathParamsOut.value);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_actionNoPathParams_invalidPathParamsAdded() {
    try {
      getCurrentRequest().setRequestURL("http://localhost/app/testController/actionNoPathParams/param1");
      var pathItems = RequestContextParser.parsePathItems(getCurrentRequest());

      var pathParamsOut = new Out<Map<String, String>>();
      var actionDesc = actionParser.parse(pathItems, pathParamsOut);
      Assertions.assertNull(actionDesc);
      Assertions.assertNull(pathParamsOut.value);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_actionPathParams_pathParamsProvided() {
    try {
      getCurrentRequest().setRequestURL("http://localhost/app/testController/actionPathParams/param1/param2");
      var pathItems = RequestContextParser.parsePathItems(getCurrentRequest());

      var pathParamsOut = new Out<Map<String, String>>();
      var actionDesc = actionParser.parse(pathItems, pathParamsOut);
      Assertions.assertNotNull(actionDesc);

      Assertions.assertEquals("actionPathParams", actionDesc.getAction());
      Assertions.assertEquals("testController", actionDesc.getController());

      Assertions.assertNotNull(pathParamsOut.value);
      Assertions.assertEquals(2, pathParamsOut.value.size());

      Assertions.assertEquals("param1", pathParamsOut.value.get("param1"));
      Assertions.assertEquals("param2", pathParamsOut.value.get("param2"));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_actionPathParams_pathParamsMissed() {
    try {
      getCurrentRequest().setRequestURL("http://localhost/app/testController/actionPathParams/param1");
      var pathItems = RequestContextParser.parsePathItems(getCurrentRequest());

      var pathParamsOut = new Out<Map<String, String>>();
      var actionDesc = actionParser.parse(pathItems, pathParamsOut);
      Assertions.assertNull(actionDesc);
      Assertions.assertNull(pathParamsOut.value);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_actionSubPathParams_pathParamsProvided() {
    try {
      getCurrentRequest().setRequestURL("http://localhost/app/testController/actionSubPathParams/param-val-1-param2");
      var pathItems = RequestContextParser.parsePathItems(getCurrentRequest());

      var pathParamsOut = new Out<Map<String, String>>();
      var actionDesc = actionParser.parse(pathItems, pathParamsOut);
      Assertions.assertNotNull(actionDesc);

      Assertions.assertEquals("actionSubPathParams", actionDesc.getAction());
      Assertions.assertEquals("testController", actionDesc.getController());

      Assertions.assertNotNull(pathParamsOut.value);
      Assertions.assertEquals(2, pathParamsOut.value.size());

      Assertions.assertEquals("param-val-1", pathParamsOut.value.get("param1"));
      Assertions.assertEquals("param2", pathParamsOut.value.get("param2"));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_actionSubPathParams_pathParamsMissed() {
    try {
      getCurrentRequest().setRequestURL("http://localhost/app/testController/actionSubPathParams/param1");
      var pathItems = RequestContextParser.parsePathItems(getCurrentRequest());

      var pathParamsOut = new Out<Map<String, String>>();
      var actionDesc = actionParser.parse(pathItems, pathParamsOut);

      Assertions.assertNull(actionDesc);
      Assertions.assertNull(pathParamsOut.value);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_actionSubPathParams_pathParams_nullValue1() {
    try {
      getCurrentRequest().setRequestURL("http://localhost/app/testController/actionSubPathParams/-param2");
      var pathItems = RequestContextParser.parsePathItems(getCurrentRequest());

      var pathParamsOut = new Out<Map<String, String>>();
      var actionDesc = actionParser.parse(pathItems, pathParamsOut);

      Assertions.assertNull(actionDesc);
      Assertions.assertNull(pathParamsOut.value);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_actionSubPathParams_pathParams_nullValue2() {
    try {
      getCurrentRequest().setRequestURL("http://localhost/app/testController/actionSubPathParams/param1-");
      var pathItems = RequestContextParser.parsePathItems(getCurrentRequest());

      var pathParamsOut = new Out<Map<String, String>>();
      var actionDesc = actionParser.parse(pathItems, pathParamsOut);

      Assertions.assertNull(actionDesc);
      Assertions.assertNull(pathParamsOut.value);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_actionSubPathParams_invalidPathParamsAdded() {
    try {
      getCurrentRequest()
          .setRequestURL("http://localhost/app/testController/actionSubPathParams/param1-val-1-param2/param3");
      var pathItems = RequestContextParser.parsePathItems(getCurrentRequest());

      var pathParamsOut = new Out<Map<String, String>>();
      var actionDesc = actionParser.parse(pathItems, pathParamsOut);

      Assertions.assertNull(actionDesc);
      Assertions.assertNull(pathParamsOut.value);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_toActionLink() {
    try {
      executeCurrent("GET", "http://localhost/app/testController/index");

      Map<String, Object> attributes = new Params().set("class", "class1");
      var link = actionParser.toActionLink(getCurrentRequest(), "testController", "index",
          new Params().set("key1", "val1"), attributes, ">link");

      Assertions.assertEquals("<a href=\"/app/testController?key1=val1\" class=\"class1\">&gt;link</a>", link);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Controller("testController")
  @Home
  public static class TestController extends ControllerBase {

    @HttpGet
    public void index() {
    }

    @HttpGet
    public void actionNoPathParams() {
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
