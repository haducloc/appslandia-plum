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

package com.appslandia.plum.utils;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.appslandia.common.base.StringWriter;

/**
 *
 * @author Loc Ha
 *
 */
public class HtmlUtilsTest {

  @Test
  public void test_escAttribute() {
    StringWriter out = new StringWriter();
    try {
      HtmlUtils.escAttribute(out, "title", "test < title");
      Assertions.assertTrue(out.toString().contains("title=\"test &lt; title\""));
    } catch (IOException ex) {
      Assertions.fail(ex.getMessage());
    }
    out = new StringWriter();
    try {
      HtmlUtils.escAttribute(out, "title", "");
      Assertions.assertTrue(out.toString().contains("title=\"\""));
    } catch (IOException ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_toTagId() {
    String id = HtmlUtils.toValueTagId("user.userName");
    Assertions.assertEquals("user_userName", id);

    id = HtmlUtils.toValueTagId("user.location.address");
    Assertions.assertEquals("user_location_address", id);

    id = HtmlUtils.toValueTagId("user.addresses[1].location");
    Assertions.assertEquals("user_addresses_1__location", id);
  }
}
