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

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.appslandia.common.validators.MaxLength;
import com.appslandia.common.validators.MultiValues;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 *
 * @author Loc Ha
 *
 */
public class ModelBinderTest extends MockTestBase {

  @Override
  protected void initialize() {
    container.register(TestController.class, TestController.class);
  }

  @Test
  public void test_testUserModel() {
    try {
      executeCurrent("POST", "http://localhost/app/testController/testUserModel");

      var userModel = (UserModel) getCurrentRequest().getAttribute(ServletUtils.REQUEST_ATTRIBUTE_MODEL);
      Assertions.assertNotNull(userModel);

      Assertions.assertEquals(0, userModel.userId);
      Assertions.assertNull(userModel.name);
      Assertions.assertNull(userModel.dob);
      Assertions.assertNull(userModel.permissions);

      Assertions.assertFalse(getCurrentModelState().isValid("name"));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_testUserModel_userId() {
    try {
      getCurrentRequest().addParameter("userId", "100");

      executeCurrent("POST", "http://localhost/app/testController/testUserModel");

      var userModel = (UserModel) getCurrentRequest().getAttribute(ServletUtils.REQUEST_ATTRIBUTE_MODEL);
      Assertions.assertEquals(100, userModel.userId);

      Assertions.assertTrue(getCurrentModelState().isValid("userId"));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_testUserModel_name() {
    try {
      getCurrentRequest().addParameter("name", "user1 ");

      executeCurrent("POST", "http://localhost/app/testController/testUserModel");

      var userModel = (UserModel) getCurrentRequest().getAttribute(ServletUtils.REQUEST_ATTRIBUTE_MODEL);
      Assertions.assertEquals("user1", userModel.name);

      Assertions.assertTrue(getCurrentModelState().isValid("name"));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_testUserModel_dob() {
    try {
      // ISO8601
      getCurrentRequest().addParameter("dob", "2010-10-10");

      executeCurrent("POST", "http://localhost/app/testController/testUserModel");

      var userModel = (UserModel) getCurrentRequest().getAttribute(ServletUtils.REQUEST_ATTRIBUTE_MODEL);
      Assertions.assertNotNull(userModel.dob);

      Assertions.assertTrue(getCurrentModelState().isValid("dob"));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_testUserModel_permissions() {
    try {
      getCurrentRequest().addParameter("permissions", "1", "2");

      executeCurrent("POST", "http://localhost/app/testController/testUserModel");

      var userModel = (UserModel) getCurrentRequest().getAttribute(ServletUtils.REQUEST_ATTRIBUTE_MODEL);
      Assertions.assertEquals("1,2", userModel.permissions);

      Assertions.assertTrue(getCurrentModelState().isValid("permissions"));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_testUserModel_permissions_invalid() {
    try {
      getCurrentRequest().addParameter("permissions", "1", "2x");

      executeCurrent("POST", "http://localhost/app/testController/testUserModel");

      var userModel = (UserModel) getCurrentRequest().getAttribute(ServletUtils.REQUEST_ATTRIBUTE_MODEL);
      Assertions.assertEquals("1", userModel.permissions);

      Assertions.assertFalse(getCurrentModelState().isValid("permissions"));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_testUserModel_manager() {
    try {
      getCurrentRequest().addParameter("userId", "1");
      getCurrentRequest().addParameter("name", "user1");

      getCurrentRequest().addParameter("manager.userId", "100");
      getCurrentRequest().addParameter("manager.name", "manager");

      executeCurrent("POST", "http://localhost/app/testController/testUserModel");

      var userModel = (UserModel) getCurrentRequest().getAttribute(ServletUtils.REQUEST_ATTRIBUTE_MODEL);
      Assertions.assertNotNull(userModel.manager);
      Assertions.assertEquals(100, userModel.manager.userId);
      Assertions.assertEquals("manager", userModel.manager.name);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_testListModel() {
    try {
      executeCurrent("POST", "http://localhost/app/testController/testUserList");

      var userList = (UserList) getCurrentRequest().getAttribute(ServletUtils.REQUEST_ATTRIBUTE_MODEL);
      Assertions.assertNull(userList.users);

      Assertions.assertFalse(getCurrentModelState().isValid("users"));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_testListModel_users() {
    try {
      getCurrentRequest().addParameter("users[1].userId", "1");
      getCurrentRequest().addParameter("users[1].name", "user1");
      getCurrentRequest().addParameter("users[2].userId", "2");
      getCurrentRequest().addParameter("users[2].name", "user2");

      executeCurrent("POST", "http://localhost/app/testController/testUserList");

      var userList = (UserList) getCurrentRequest().getAttribute(ServletUtils.REQUEST_ATTRIBUTE_MODEL);
      Assertions.assertNotNull(userList.users);

      Assertions.assertTrue(getCurrentModelState().isValid("users"));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_testMapModel() {
    try {
      executeCurrent("POST", "http://localhost/app/testController/testUserMap");

      var userMap = (UserMap) getCurrentRequest().getAttribute(ServletUtils.REQUEST_ATTRIBUTE_MODEL);
      Assertions.assertNull(userMap.users);

      Assertions.assertFalse(getCurrentModelState().isValid("users"));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_testMapModel_users() {
    try {
      getCurrentRequest().addParameter("users[1].userId", "1");
      getCurrentRequest().addParameter("users[1].name", "user1");
      getCurrentRequest().addParameter("users[2].userId", "2");
      getCurrentRequest().addParameter("users[2].name", "user2");

      executeCurrent("POST", "http://localhost/app/testController/testUserMap");

      var userMap = (UserMap) getCurrentRequest().getAttribute(ServletUtils.REQUEST_ATTRIBUTE_MODEL);
      Assertions.assertNotNull(userMap.users);

      Assertions.assertTrue(getCurrentModelState().isValid("users"));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_testExcludesName() {
    try {
      getCurrentRequest().addParameter("name", "user1 ");

      executeCurrent("POST", "http://localhost/app/testController/testExcludesName");

      var userModel = (UserModel) getCurrentRequest().getAttribute(ServletUtils.REQUEST_ATTRIBUTE_MODEL);
      Assertions.assertNull(userModel.name);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Controller("testController")
  public static class TestController {

    @HttpPost
    public void testUserModel(@BindModel UserModel model, RequestWrapper request) throws Exception {
      request.storeModel(model);
    }

    @HttpPost
    public void testUserList(@BindModel UserList model, RequestWrapper request) throws Exception {
      request.storeModel(model);
    }

    @HttpPost
    public void testUserMap(@BindModel UserMap model, RequestWrapper request) throws Exception {
      request.storeModel(model);
    }

    @HttpPost
    public void testExcludesName(@BindModel(excl = "name") UserModel model, RequestWrapper request) throws Exception {
      request.storeModel(model);
    }
  }

  public static class UserModel {

    protected int userId;

    @NotNull
    @MaxLength(10)
    protected String name;

    protected LocalDate dob;

    @MultiValues(ints = { 1, 2, 3 }, type = int.class)
    protected String permissions;

    @Valid
    protected UserModel manager;

    public int getUserId() {
      return userId;
    }

    public void setUserId(int userId) {
      this.userId = userId;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public LocalDate getDob() {
      return dob;
    }

    public void setDob(LocalDate dob) {
      this.dob = dob;
    }

    public String getPermissions() {
      return permissions;
    }

    public void setPermissions(String permissions) {
      this.permissions = permissions;
    }

    public UserModel getManager() {
      return manager;
    }

    public void setManager(UserModel manager) {
      this.manager = manager;
    }
  }

  public static class UserList {

    @NotNull
    protected List<UserModel> users;

    public List<UserModel> getUsers() {
      return users;
    }

    public void setUsers(List<UserModel> users) {
      this.users = users;
    }
  }

  public static class UserMap {

    @NotNull
    protected Map<String, UserModel> users;

    public Map<String, UserModel> getUsers() {
      return users;
    }

    public void setUsers(Map<String, UserModel> users) {
      this.users = users;
    }
  }
}
