// Licensed under the MIT License.
// See LICENSE file in the project root for details.

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
