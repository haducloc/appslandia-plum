// Licensed under the MIT License.
// See LICENSE file in the project root for details.

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
      var seriesToken = remMeTokenHandler.saveToken("module1", "user1", "clientBound", 5);

      var invalidCode = new Out<String>();
      var remMeToken = remMeTokenHandler.verifyToken("module1", seriesToken.getSeries(), seriesToken.getToken(),
          "clientBound", invalidCode);
      Assertions.assertNotNull(remMeToken);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_verify_expired() {
    try {
      var seriesToken = remMeTokenHandler.saveToken("module1", "user1", "clientBound", 5);
      ThreadUtils.sleepInMs(5500);

      var invalidCode = new Out<String>();
      remMeTokenHandler.verifyToken("module1", seriesToken.getSeries(), seriesToken.getToken(), "clientBound",
          invalidCode);

      Assertions.assertNotNull(invalidCode.value);
      Assertions.assertEquals(AuthResult.TOKEN_EXPIRED, invalidCode.value);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_verify_compromised() {
    try {
      var seriesToken = remMeTokenHandler.saveToken("module1", "user1", "clientBound", 5);

      var compromisedToken = "compromised_token";
      var invalidCode = new Out<String>();
      remMeTokenHandler.verifyToken("module1", seriesToken.getSeries(), compromisedToken, "clientBound", invalidCode);

      Assertions.assertNotNull(invalidCode.value);
      Assertions.assertEquals(AuthResult.TOKEN_COMPROMISED, invalidCode.value);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }
}
