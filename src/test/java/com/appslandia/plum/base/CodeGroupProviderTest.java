// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.appslandia.common.base.CodeGroup;

/**
 *
 * @author Loc Ha
 *
 */
public class CodeGroupProviderTest {

  @Test
  public void test() {
    var provider = new CodeGroupProvider();
    provider.addCodeClass(ActiveStatuses.class);

    Assertions.assertEquals("user_actives.active", provider.getDescKey("user_actives", ActiveStatuses.ACTIVE));
    Assertions.assertEquals("user_actives.inactive", provider.getDescKey("user_actives", ActiveStatuses.INACTIVE));
  }

  @Test
  public void test_userTypes() {
    var provider = new CodeGroupProvider();
    provider.addCodeClass(UserTypes.class);

    Assertions.assertEquals("user_types.user", provider.getDescKey("user_types", UserTypes.USER));
    Assertions.assertEquals("user_types.admin", provider.getDescKey("user_types", UserTypes.ADMIN));
  }

  public interface ActiveStatuses {

    @CodeGroup("user_actives")
    public static final int ACTIVE = 1;

    @CodeGroup("user_actives")
    public static final int INACTIVE = 0;
  }

  public interface UserTypes {

    @CodeGroup
    public static final int USER = 1;

    @CodeGroup
    public static final int ADMIN = 2;
  }
}
