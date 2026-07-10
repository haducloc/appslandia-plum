// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Loc Ha
 *
 */
public class JsonTest extends MockTestBase {

  @Override
  protected void initialize() {
    container.register(TestController.class, TestController.class);
  }

  @Test
  public void test_testModel() {
    try {
      executeCurrent("GET", "http://localhost/app/testController/testModel");

      var content = getCurrentResponse().getContent().toString(StandardCharsets.UTF_8);
      Assertions.assertTrue(content.contains("\"userId\":1"));
      Assertions.assertTrue(content.contains("\"name\":\"user1\""));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Controller("testController")
  public static class TestController extends ControllerBase {

    @HttpGet
    public UserModel testModel() throws Exception {
      return new UserModel(1, "user1");
    }
  }

  public static class UserModel {

    public int userId;
    public String name;

    public UserModel(int userId, String name) {
      this.userId = userId;
      this.name = name;
    }
  }
}
