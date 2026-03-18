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
