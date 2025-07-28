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

import com.appslandia.common.base.ConstDesc;

/**
 *
 * @author Loc Ha
 *
 */
public class ConstDescProviderTest {

  @Test
  public void test() {
    var constDescProvider = new ConstDescProvider();
    constDescProvider.addConstClass(Actives.class);

    Assertions.assertEquals("actives.active", constDescProvider.getDescKey("actives", Actives.ACTIVE));
    Assertions.assertEquals("actives.inactive", constDescProvider.getDescKey("actives", Actives.INACTIVE));
  }

  @Test
  public void test_userTypes() {
    var constDescProvider = new ConstDescProvider();
    constDescProvider.addConstClass(UserTypes.class);

    Assertions.assertEquals("userTypes.user", constDescProvider.getDescKey("userTypes", UserTypes.USER));
    Assertions.assertEquals("userTypes.admin", constDescProvider.getDescKey("userTypes", UserTypes.ADMIN));
  }

  public static final class Actives {

    @ConstDesc("actives")
    public static final int ACTIVE = 1;

    @ConstDesc("actives")
    public static final int INACTIVE = 0;
  }

  @ConstDesc
  public static final class UserTypes {

    @ConstDesc
    public static final int USER = 1;

    @ConstDesc
    public static final int ADMIN = 2;
  }
}
