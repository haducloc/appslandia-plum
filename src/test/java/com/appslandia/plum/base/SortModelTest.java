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
public class SortModelTest {

  @Test
  public void test() {
    SortConfig config = new SortConfig().asc("name").desc("dateCreated").defBy("name");
    SortModel model = new SortModel(config);

    model.setCurrent("name", null);

    Assertions.assertEquals("name", model.getSortBy());
    Assertions.assertTrue(model.getSortAsc());

    Assertions.assertFalse(model.getNext("name"));
    Assertions.assertNull(model.getNext("dateCreated"));
  }

  @Test
  public void test_sortAsc() {
    SortConfig config = new SortConfig().asc("name").desc("dateCreated").defBy("name");
    SortModel model = new SortModel(config);

    model.setCurrent("name", false);

    Assertions.assertEquals("name", model.getSortBy());
    Assertions.assertFalse(model.getSortAsc());
  }

  @Test
  public void test_invalid_sortBy() {
    SortConfig config = new SortConfig().asc("name").desc("dateCreated").defBy("name");
    SortModel model = new SortModel(config);

    model.setCurrent("dob", true);

    Assertions.assertEquals("name", model.getSortBy());
    Assertions.assertTrue(model.getSortAsc());
  }
}
