// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.util.Collections;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.appslandia.plum.mem.MemModules;
import com.appslandia.plum.mocks.MockAppConfig;
import com.appslandia.plum.mocks.MockServletUtils;

import jakarta.security.enterprise.credential.Credential;
import jakarta.security.enterprise.identitystore.IdentityStore;

/**
 *
 * @author Loc Ha
 *
 */
public class IdentityStoreGroupsTest extends MockTestBase {

  @Override
  protected void initialize() {
    container.register(TestController.class, TestController.class);
    container.register(IdentityStore.class, TestGroupIdentityStore.class);

    var appConfig = container.getObject(MockAppConfig.class);
    appConfig.set(AppConfig.CONFIG_DEFAULT_MODULE, MemModules.MEM_BASIC);
  }

  @Test
  public void test_testAction_manager() {
    try {
      getCurrentRequest().setHeader("Authorization",
          "Basic " + MockServletUtils.createBasicCredential("manager1", "pass1"));

      executeCurrent("GET", "http://localhost/app/testController/testAction");
      Assertions.assertEquals(200, getCurrentResponse().getStatus());

      Assertions.assertTrue(getCurrentRequest().isUserInRole("manager"));
      Assertions.assertTrue(getCurrentRequest().isUserInRole("role1"));
      Assertions.assertTrue(getCurrentRequest().isUserInRole("role2"));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_testAction_admin() {
    try {
      getCurrentRequest().setHeader("Authorization",
          "Basic " + MockServletUtils.createBasicCredential("admin1", "pass1"));

      executeCurrent("GET", "http://localhost/app/testController/testAction");
      Assertions.assertEquals(200, getCurrentResponse().getStatus());

      Assertions.assertTrue(getCurrentRequest().isUserInRole("admin"));
      Assertions.assertFalse(getCurrentRequest().isUserInRole("role1"));
      Assertions.assertFalse(getCurrentRequest().isUserInRole("role2"));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Controller("testController")
  @Module(MemModules.MEM_BASIC)
  public static class TestController extends ControllerBase {

    @HttpGet
    @Authorize
    public void testAction() throws Exception {
    }
  }

  public static class TestGroupIdentityStore extends IdentityStoreBase {

    @Override
    public Set<String> validationModules() {
      return Set.of(MemModules.MEM_BASIC);
    }

    @Override
    protected AuthResult doValidate(String module, Credential credential) {
      throw new UnsupportedOperationException();
    }

    @Override
    protected Set<String> loadCallerGroups(AuthPrincipal principal) {
      var username = principal.getName();
      if ("manager1".equals(username)) {
        return Set.of("role1", "role2");
      }
      return Collections.emptySet();
    }

    @Override
    public Set<ValidationType> validationTypes() {
      return Set.of(ValidationType.PROVIDE_GROUPS);
    }
  }
}
