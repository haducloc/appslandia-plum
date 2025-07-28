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
