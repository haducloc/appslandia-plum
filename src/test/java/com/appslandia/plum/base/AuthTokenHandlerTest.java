// The MIT License (MIT)
// Copyright © 2015 AppsLandia. All rights reserved.

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

import com.appslandia.common.base.Out;
import com.appslandia.common.utils.ThreadUtils;
import com.appslandia.plum.mocks.MemUser;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class AuthTokenHandlerTest extends MockTestBase {

  AuthTokenHandler authTokenHandler;

  @Override
  protected void initialize() {
    authTokenHandler = container.getObject(AuthTokenHandler.class);
  }

  @Test
  public void test_verify() {
    try {
      SeriesToken seriesToken = authTokenHandler.saveToken(MemUser.createEmail("user1"), "verifyCode1", 5000);

      Out<String> invalidCode = new Out<>();
      boolean isValid = authTokenHandler.verifyToken(seriesToken.getSeries(), seriesToken.getToken(),
          MemUser.createEmail("user1"), "verifyCode1", 0, invalidCode);
      Assertions.assertTrue(isValid);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_verify_expired() {
    try {
      SeriesToken seriesToken = authTokenHandler.saveToken(MemUser.createEmail("user1"), "verifyCode1", 1000);
      ThreadUtils.sleepInMs(2000);

      Out<String> invalidCode = new Out<>();
      boolean isValid = authTokenHandler.verifyToken(seriesToken.getSeries(), seriesToken.getToken(),
          MemUser.createEmail("user1"), "verifyCode1", 0, invalidCode);
      Assertions.assertFalse(isValid);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_verify_leeways() {
    try {
      SeriesToken seriesToken = authTokenHandler.saveToken(MemUser.createEmail("user1"), "verifyCode1", 1000);
      ThreadUtils.sleepInMs(1500);

      Out<String> invalidCode = new Out<>();
      boolean isValid = authTokenHandler.verifyToken(seriesToken.getSeries(), seriesToken.getToken(),
          MemUser.createEmail("user1"), "verifyCode1", 2000, invalidCode);
      Assertions.assertTrue(isValid);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }
}
