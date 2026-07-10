// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.appslandia.plum.mocks.MockHttpHeader;

/**
 *
 * @author Loc Ha
 *
 */
public class MockHttpHeaderTest {

  @Test
  public void test_addValues() {
    var header = new MockHttpHeader();
    header.addValues("v1", "v2");

    Assertions.assertEquals("v1", header.getValue());
    Assertions.assertEquals("v1", header.getValues().get(0));
    Assertions.assertEquals("v2", header.getValues().get(1));
  }

  @Test
  public void test_setValues() {
    var header = new MockHttpHeader();
    header.addValues("v1", "v2");

    Assertions.assertEquals("v1", header.getValues().get(0));
    Assertions.assertEquals("v2", header.getValues().get(1));

    header.setValues("v3", "v4");
    Assertions.assertEquals("v3", header.getValues().get(0));
    Assertions.assertEquals("v4", header.getValues().get(1));
  }
}
