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
public class ConstGroupProviderTest {

  @Test
  public void test() {
    var provider = new ConstGroupProvider();
    provider.addConstClass(ActiveStatuses.class);

    Assertions.assertEquals("user_actives.active", provider.getDescKey("user_actives", ActiveStatuses.ACTIVE));
    Assertions.assertEquals("user_actives.inactive", provider.getDescKey("user_actives", ActiveStatuses.INACTIVE));
  }

  @Test
  public void test_userTypes() {
    var provider = new ConstGroupProvider();
    provider.addConstClass(UserTypes.class);

    Assertions.assertEquals("user_types.user", provider.getDescKey("user_types", UserTypes.USER));
    Assertions.assertEquals("user_types.admin", provider.getDescKey("user_types", UserTypes.ADMIN));
  }

  public static final class ActiveStatuses {

    @ConstGroup("user_actives")
    public static final int ACTIVE = 1;

    @ConstGroup("user_actives")
    public static final int INACTIVE = 0;
  }

  public static final class UserTypes {

    @ConstGroup
    public static final int USER = 1;

    @ConstGroup
    public static final int ADMIN = 2;
  }
}
