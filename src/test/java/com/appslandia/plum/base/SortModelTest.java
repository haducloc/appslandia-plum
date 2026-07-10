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
public class SortModelTest {

  @Test
  public void test() {
    var config = new SortConfig().asc("name").desc("dateCreated").defBy("name");

    var model = new SortModel(config);
    model.setState("name", null);

    Assertions.assertEquals("name", model.getState().getSortBy());
    Assertions.assertTrue(model.getState().isSortAsc());

    Assertions.assertFalse(model.nextSortAsc("name"));
    Assertions.assertNull(model.nextSortAsc("dateCreated"));
  }

  @Test
  public void test_sortAsc() {
    var config = new SortConfig().asc("name").desc("dateCreated").defBy("name");

    var model = new SortModel(config);
    model.setState("name", false);

    Assertions.assertEquals("name", model.getState().getSortBy());
    Assertions.assertFalse(model.getState().isSortAsc());
  }

  @Test
  public void test_invalid_sortBy() {
    var config = new SortConfig().asc("name").desc("dateCreated").defBy("name");

    var model = new SortModel(config);
    model.setState("dob", true);

    Assertions.assertEquals("name", model.getState().getSortBy());
    Assertions.assertTrue(model.getState().isSortAsc());
  }
}
