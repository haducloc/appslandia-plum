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

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.junit.Assert;
import org.junit.Test;

import com.appslandia.common.validators.BitMask;
import com.appslandia.common.validators.MaxLength;
import com.appslandia.plum.utils.ServletUtils;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
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

			UserModel userModel = (UserModel) getCurrentRequest().getAttribute(ServletUtils.REQUEST_ATTRIBUTE_MODEL);
			Assert.assertNotNull(userModel);

			Assert.assertEquals(userModel.userId, 0);
			Assert.assertNull(userModel.name);
			Assert.assertNull(userModel.dob);
			Assert.assertEquals(userModel.permissions, 0);

			Assert.assertFalse(getCurrentModelState().isValid("name"));

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_testUserModel_userId() {
		try {
			getCurrentRequest().addParameter("userId", "100");

			executeCurrent("POST", "http://localhost/app/testController/testUserModel");

			UserModel userModel = (UserModel) getCurrentRequest().getAttribute(ServletUtils.REQUEST_ATTRIBUTE_MODEL);
			Assert.assertEquals(userModel.userId, 100);

			Assert.assertTrue(getCurrentModelState().isValid("userId"));

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_testUserModel_name() {
		try {
			getCurrentRequest().addParameter("name", "user1 ");

			executeCurrent("POST", "http://localhost/app/testController/testUserModel");

			UserModel userModel = (UserModel) getCurrentRequest().getAttribute(ServletUtils.REQUEST_ATTRIBUTE_MODEL);
			Assert.assertEquals(userModel.name, "user1");

			Assert.assertTrue(getCurrentModelState().isValid("name"));

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_testUserModel_dob() {
		try {
			// ISO8601
			getCurrentRequest().addParameter("dob", "2010-10-10");

			executeCurrent("POST", "http://localhost/app/testController/testUserModel");

			UserModel userModel = (UserModel) getCurrentRequest().getAttribute(ServletUtils.REQUEST_ATTRIBUTE_MODEL);
			Assert.assertNotNull(userModel.dob);

			Assert.assertTrue(getCurrentModelState().isValid("dob"));

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_testUserModel_permissions() {
		try {
			getCurrentRequest().addParameter("permissions", "1", "4");

			executeCurrent("POST", "http://localhost/app/testController/testUserModel");

			UserModel userModel = (UserModel) getCurrentRequest().getAttribute(ServletUtils.REQUEST_ATTRIBUTE_MODEL);
			Assert.assertEquals(userModel.permissions, 5);

			Assert.assertTrue(getCurrentModelState().isValid("permissions"));

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_testUserModel_manager() {
		try {
			getCurrentRequest().addParameter("userId", "1");
			getCurrentRequest().addParameter("name", "user1");

			getCurrentRequest().addParameter("manager", "ignore");
			getCurrentRequest().addParameter("manager.userId", "100");
			getCurrentRequest().addParameter("manager.name", "manager");

			executeCurrent("POST", "http://localhost/app/testController/testUserModel");

			UserModel userModel = (UserModel) getCurrentRequest().getAttribute(ServletUtils.REQUEST_ATTRIBUTE_MODEL);
			Assert.assertNotNull(userModel.manager);
			Assert.assertEquals(userModel.manager.userId, 100);
			Assert.assertEquals(userModel.manager.name, "manager");

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_testListModel() {
		try {
			executeCurrent("POST", "http://localhost/app/testController/testUserList");

			UserList userList = (UserList) getCurrentRequest().getAttribute(ServletUtils.REQUEST_ATTRIBUTE_MODEL);
			Assert.assertNull(userList.users);

			Assert.assertFalse(getCurrentModelState().isValid("users"));

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_testListModel_users() {
		try {
			getCurrentRequest().addParameter("users", "ignore");
			getCurrentRequest().addParameter("users[1]", "ignore");

			getCurrentRequest().addParameter("users[1].userId", "1");
			getCurrentRequest().addParameter("users[1].name", "user1");
			getCurrentRequest().addParameter("users[2].userId", "2");
			getCurrentRequest().addParameter("users[2].name", "user2");

			executeCurrent("POST", "http://localhost/app/testController/testUserList");

			UserList userList = (UserList) getCurrentRequest().getAttribute(ServletUtils.REQUEST_ATTRIBUTE_MODEL);
			Assert.assertNotNull(userList.users);

			Assert.assertTrue(getCurrentModelState().isValid("users"));

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_testMapModel() {
		try {
			executeCurrent("POST", "http://localhost/app/testController/testUserMap");

			UserMap userMap = (UserMap) getCurrentRequest().getAttribute(ServletUtils.REQUEST_ATTRIBUTE_MODEL);
			Assert.assertNull(userMap.users);

			Assert.assertFalse(getCurrentModelState().isValid("users"));

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_testMapModel_users() {
		try {
			getCurrentRequest().addParameter("users", "ignore");
			getCurrentRequest().addParameter("users[1]", "ignore");

			getCurrentRequest().addParameter("users[1].userId", "1");
			getCurrentRequest().addParameter("users[1].name", "user1");
			getCurrentRequest().addParameter("users[2].userId", "2");
			getCurrentRequest().addParameter("users[2].name", "user2");

			executeCurrent("POST", "http://localhost/app/testController/testUserMap");

			UserMap userMap = (UserMap) getCurrentRequest().getAttribute(ServletUtils.REQUEST_ATTRIBUTE_MODEL);
			Assert.assertNotNull(userMap.users);

			Assert.assertTrue(getCurrentModelState().isValid("users"));

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_testExcludePermissions() {
		try {
			getCurrentRequest().addParameter("name", "user1 ");
			getCurrentRequest().addParameter("permissions", "1", "4");

			executeCurrent("POST", "http://localhost/app/testController/testExcludePermissions");

			UserModel userModel = (UserModel) getCurrentRequest().getAttribute(ServletUtils.REQUEST_ATTRIBUTE_MODEL);
			Assert.assertEquals(userModel.name, "user1");
			Assert.assertEquals(userModel.permissions, 0);

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Controller("testController")
	public static class TestController {

		@HttpPost
		public void testUserModel(@Model UserModel model, RequestAccessor request) throws Exception {
			request.storeModel(model);
		}

		@HttpPost
		public void testUserList(@Model UserList model, RequestAccessor request) throws Exception {
			request.storeModel(model);
		}

		@HttpPost
		public void testUserMap(@Model UserMap model, RequestAccessor request) throws Exception {
			request.storeModel(model);
		}

		@HttpPost
		public void testExcludePermissions(@Model(excludes = "permissions") UserModel model, RequestAccessor request) throws Exception {
			request.storeModel(model);
		}
	}

	public static class UserModel {

		protected int userId;

		@NotNull
		@MaxLength(10)
		protected String name;

		protected Date dob;

		// 1|2|4
		@BitMask(3)
		protected int permissions;

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

		public Date getDob() {
			return dob;
		}

		public void setDob(Date dob) {
			this.dob = dob;
		}

		public int getPermissions() {
			return permissions;
		}

		public void setPermissions(int permissions) {
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
