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
public class ActionDescProviderStaticTest {

  @Test
  public void test_parsePathParams() {
    var pathParams = ActionDescProvider.parsePathParams("/{param0}");
    Assertions.assertEquals(1, pathParams.size());
    Assertions.assertEquals("param0", pathParams.get(0).getParamName());
    Assertions.assertEquals(null, pathParams.get(0).getSubParams());

    pathParams = ActionDescProvider.parsePathParams("/{param0}/{param1}");
    Assertions.assertEquals(2, pathParams.size());

    Assertions.assertEquals("param0", pathParams.get(0).getParamName());
    Assertions.assertNull(pathParams.get(0).getSubParams());
    Assertions.assertEquals("param1", pathParams.get(1).getParamName());
    Assertions.assertNull(pathParams.get(1).getSubParams());
  }

  @Test
  public void test_parsePathParams_subParams() {
    var pathParams = ActionDescProvider.parsePathParams("/{param0}-{param1}/{param2}");
    Assertions.assertEquals(2, pathParams.size());

    Assertions.assertEquals(null, pathParams.get(0).getParamName());
    Assertions.assertNotNull(pathParams.get(0).getSubParams());

    var subParams = pathParams.get(0).getSubParams();
    Assertions.assertEquals(2, subParams.size());

    Assertions.assertEquals("param0", subParams.get(0).getParamName());
    Assertions.assertNull(subParams.get(0).getSubParams());

    Assertions.assertEquals("param1", subParams.get(1).getParamName());
    Assertions.assertNull(subParams.get(1).getSubParams());

    Assertions.assertEquals("param2", pathParams.get(1).getParamName());
    Assertions.assertNull(pathParams.get(1).getSubParams());
  }

  @Test
  public void test_parseSubParams() {
    var pathParams = ActionDescProvider.parseSubParams("{param0}-{param1}");
    Assertions.assertEquals(2, pathParams.size());

    Assertions.assertEquals("param0", pathParams.get(0).getParamName());
    Assertions.assertNull(pathParams.get(0).getSubParams());

    Assertions.assertEquals("param1", pathParams.get(1).getParamName());
    Assertions.assertNull(pathParams.get(1).getSubParams());
  }

  @Test
  public void test_getPathParamCount() {
    var pathParams = ActionDescProvider.parsePathParams("/{param0}");
    var pathParamCount = ActionDescProvider.getPathParamCount(pathParams);
    Assertions.assertEquals(1, pathParamCount);
  }

  @Test
  public void test_getPathParamCount_subParams() {
    var pathParams = ActionDescProvider.parsePathParams("/{param0}-{param1}/{param2}");
    var pathParamCount = ActionDescProvider.getPathParamCount(pathParams);
    Assertions.assertEquals(3, pathParamCount);
  }
}
