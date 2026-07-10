// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.appslandia.common.base.Params;

/**
 *
 * @author Loc Ha
 *
 */
public class ActionParserStaticTest {

  @Test
  public void test_parseSubParams() {
    var pathParams = ActionDescProvider.parsePathParams("/{param0}-{param1}");
    Assertions.assertEquals(1, pathParams.size());

    var pathParam = pathParams.get(0);
    Map<String, String> pathParamMap = new HashMap<>();

    var parsed = ActionParser.parseSubParams("param0-param1", pathParam.getSubParams(), pathParamMap);
    Assertions.assertTrue(parsed);

    Assertions.assertEquals("param0", pathParamMap.get("param0"));
    Assertions.assertEquals("param1", pathParamMap.get("param1"));
  }

  @Test
  public void test_parseSubParams_invalid() {
    var pathParams = ActionDescProvider.parsePathParams("/{param0}-{param1}");
    Assertions.assertEquals(1, pathParams.size());

    var pathParam = pathParams.get(0);
    Map<String, String> pathParamMap = new HashMap<>();

    var parsed = ActionParser.parseSubParams("param0", pathParam.getSubParams(), pathParamMap);
    Assertions.assertFalse(parsed);

    pathParamMap.clear();
    parsed = ActionParser.parseSubParams("-param0", pathParam.getSubParams(), pathParamMap);
    Assertions.assertFalse(parsed);

    pathParamMap.clear();
    parsed = ActionParser.parseSubParams("param0-", pathParam.getSubParams(), pathParamMap);
    Assertions.assertFalse(parsed);

    pathParamMap.clear();
    parsed = ActionParser.parseSubParams("param0_param1", pathParam.getSubParams(), pathParamMap);
    Assertions.assertFalse(parsed);
  }

  @Test
  public void test_addPathParams() {
    var pathParams = ActionDescProvider.parsePathParams("/{param0}/{param1}-{param2}");
    Assertions.assertEquals(2, pathParams.size());

    Map<String, Object> parameters = new Params();
    parameters.put("param0", "param0");
    parameters.put("param1", "param1");
    parameters.put("param2", "param2");

    var url = new StringBuilder("http://myDomain.com");
    ActionParser.addPathParams(url, parameters, pathParams);
    Assertions.assertEquals("http://myDomain.com/param0/param1-param2", url.toString());
  }

  @Test
  public void test_addPathParams_missedParams() {
    var pathParams = ActionDescProvider.parsePathParams("/{param0}/{param1}-{param2}");
    Assertions.assertEquals(2, pathParams.size());

    Map<String, Object> parameters = new Params();
    parameters.put("param1", "param1");
    parameters.put("param2", "param2");

    var url = new StringBuilder("http://myDomain.com");
    try {
      ActionParser.addPathParams(url, parameters, pathParams);
      Assertions.fail();
    } catch (Exception ex) {
    }
    parameters = new Params();
    parameters.put("param0", "param0");
    parameters.put("param1", "param1");
    url = new StringBuilder("http://myDomain.com");
    try {
      ActionParser.addPathParams(url, parameters, pathParams);
      Assertions.fail();
    } catch (Exception ex) {
    }
  }

  @Test
  public void test_addQueryParams() {
    var pathParams = ActionDescProvider.parsePathParams("/{param0}");
    Assertions.assertEquals(1, pathParams.size());

    Map<String, Object> parameters = new LinkedHashMap<>();
    parameters.put("param0", "param0");
    parameters.put("param1", "param1");
    parameters.put("param2", "param2");

    var url = new StringBuilder();
    ActionParser.addQueryParams(url, parameters, pathParams);
    Assertions.assertEquals("?param1=param1&param2=param2", url.toString());
  }
}
