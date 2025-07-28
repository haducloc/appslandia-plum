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

package com.appslandia.plum.tags;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Loc Ha
 *
 */
public class TagUtilsTest {

  @Test
  public void test_toTagId() {
    var id = TagUtils.toTagId("user.userName");
    Assertions.assertEquals("user_userName", id);

    id = TagUtils.toTagId("user.location.address");
    Assertions.assertEquals("user_location_address", id);

    id = TagUtils.toTagId("user.addresses[1].location");
    Assertions.assertEquals("user_addresses_1__location", id);
  }

  @Test
  public void test_toIdPart() {
    var id = TagUtils.toIdPart(1);
    Assertions.assertEquals("1", id);

    id = TagUtils.toIdPart("id 1");
    Assertions.assertEquals("id_1", id);
  }

  @Test
  public void test_toIdPart_null() {
    var id = TagUtils.toIdPart(null);
    Assertions.assertEquals("null", id);
  }
}
