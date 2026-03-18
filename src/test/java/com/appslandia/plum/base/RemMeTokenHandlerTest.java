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

import com.appslandia.common.base.Out;
import com.appslandia.common.utils.ThreadUtils;

/**
 *
 * @author Loc Ha
 *
 */
public class RemMeTokenHandlerTest extends MockTestBase {

  RemMeTokenHandler remMeTokenHandler;

  @Override
  protected void initialize() {
    remMeTokenHandler = container.getObject(RemMeTokenHandler.class);
  }

  @Test
  public void test_verify() {
    try {
      var seriesToken = remMeTokenHandler.saveToken("user1", "module1", null, 3);
      var invalidCode = new Out<String>();

      var remMeToken = remMeTokenHandler.verifyToken(seriesToken.getSeries(), seriesToken.getToken(), "module1", null,
          0, invalidCode);
      Assertions.assertNotNull(remMeToken);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_verify_expired() {
    try {
      var seriesToken = remMeTokenHandler.saveToken("user1", "module1", null, 3);
      ThreadUtils.sleepInMs(3500);
      var invalidCode = new Out<String>();

      remMeTokenHandler.verifyToken(seriesToken.getSeries(), seriesToken.getToken(), "module1", null, 0, invalidCode);

      Assertions.assertNotNull(invalidCode.value);
      Assertions.assertEquals(InvalidAuth.TOKEN_EXPIRED.getCode(), invalidCode.value);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_verify_compromised() {
    try {
      var seriesToken = remMeTokenHandler.saveToken("user1", "module1", null, 3);
      var invalidCode = new Out<String>();

      var compromisedToken = "compromised_token";
      remMeTokenHandler.verifyToken(seriesToken.getSeries(), compromisedToken, "module1", null, 0, invalidCode);

      Assertions.assertNotNull(invalidCode.value);
      Assertions.assertEquals(InvalidAuth.TOKEN_COMPROMISED.getCode(), invalidCode.value);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_verify_leeways() {
    try {
      var seriesToken = remMeTokenHandler.saveToken("user1", "module1", null, 3);
      ThreadUtils.sleepInMs(2500);
      var invalidCode = new Out<String>();

      var remMeToken = remMeTokenHandler.verifyToken(seriesToken.getSeries(), seriesToken.getToken(), "module1", null,
          500, invalidCode);
      Assertions.assertNotNull(remMeToken);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }
}
