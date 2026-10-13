// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.appslandia.common.base.Out;
import com.appslandia.common.threading.ThreadUtils;

/**
 *
 * @author Loc Ha
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
      var seriesToken = authTokenHandler.saveToken("module1", "purpose1", "user1", null, "clientBound", 5, null, null);

      var invalidCode = new Out<String>();
      var authToken = authTokenHandler.verifyToken("module1", "purpose1", seriesToken.getSeries(),
          seriesToken.getToken(), null, "clientBound", 0, invalidCode);

      Assertions.assertNull(invalidCode.value);
      Assertions.assertNotNull(authToken);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_verify_expired() {
    try {
      var seriesToken = authTokenHandler.saveToken("module1", "purpose1", "user1", null, "clientBound", 5, null, null);
      ThreadUtils.sleepInMs(5500);

      var invalidCode = new Out<String>();
      authTokenHandler.verifyToken("module1", "purpose1", seriesToken.getSeries(), seriesToken.getToken(), null,
          "clientBound", 0, invalidCode);

      Assertions.assertNotNull(invalidCode.value);
      Assertions.assertEquals(AuthResult.TOKEN_EXPIRED, invalidCode.value);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_verify_compromised() {
    try {
      var seriesToken = authTokenHandler.saveToken("module1", "purpose1", "user1", null, "clientBound", 5, null, null);

      var compromisedToken = "compromised_token";
      var invalidCode = new Out<String>();
      authTokenHandler.verifyToken("module1", "purpose1", seriesToken.getSeries(), compromisedToken, null,
          "clientBound", 0, invalidCode);

      Assertions.assertNotNull(invalidCode.value);
      Assertions.assertEquals(AuthResult.TOKEN_INVALID, invalidCode.value);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_verify_codeValid() {
    try {
      var seriesToken = authTokenHandler.saveToken("module1", "purpose1", "user1", null, "clientBound", 5, "code1",
          300);

      var invalidCode = new Out<String>();
      var authToken = authTokenHandler.verifyToken("module1", "purpose1", seriesToken.getSeries(),
          seriesToken.getToken(), "code1", "clientBound", 0, invalidCode);

      Assertions.assertNull(invalidCode.value);
      Assertions.assertNotNull(authToken);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_verify_codeInvalid() {
    try {
      var seriesToken = authTokenHandler.saveToken("module1", "purpose1", "user1", null, "clientBound", 5, "code1",
          300);

      var invalidCode = new Out<String>();
      var authToken = authTokenHandler.verifyToken("module1", "purpose1", seriesToken.getSeries(),
          seriesToken.getToken(), "code_invalid", "clientBound", 0, invalidCode);

      Assertions.assertEquals(invalidCode.value, AuthResult.CODE_INVALID);
      Assertions.assertNotNull(authToken);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }
}
