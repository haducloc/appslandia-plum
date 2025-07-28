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

package com.appslandia.plum.defaults;

import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.appslandia.common.crypto.PasswordDigester;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

/**
 *
 * @author Loc Ha
 *
 */

@ApplicationScoped
public class MemUserService {

  final TreeMap<Integer, MemUser> users = new TreeMap<>();
  final PasswordDigester passwordDigester = new PasswordDigester();

  @PostConstruct
  public void init() {
    createUser(1, "user1", "pass1", null);
    createUser(2, "manager1", "pass1", "manager");
    createUser(3, "admin1", "pass1", "admin");
    createUser(4, "manageradmin1", "pass1", "manager,admin");
  }

  public List<MemUser> getAllUsers() {
    return this.users.values().stream().map(MemUserService::copy).collect(Collectors.toList());
  }

  public MemUser getByUserId(int userId) {
    var user = this.users.get(userId);
    return user != null ? copy(user) : null;
  }

  public MemUser getByUsername(String username) {
    return this.users.values().stream().filter(e -> e.getUsername().equalsIgnoreCase(username))
        .map(MemUserService::copy).findFirst().orElse(null);
  }

  public boolean verifyPassword(String password, String hashPassword) {
    return this.passwordDigester.verify(password, hashPassword);
  }

  static MemUser copy(MemUser original) {
    if (original == null) {
      return null;
    }
    var copy = new MemUser();
    copy.setUserId(original.getUserId());
    copy.setUsername(original.getUsername());
    copy.setPassword(original.getPassword());
    copy.setUserRoles(original.getUserRoles());
    return copy;
  }

  private void createUser(int userId, String username, String password, String userRoles) {
    var user = new MemUser();
    user.setUserId(userId);
    user.setUsername(username);
    user.setPassword(this.passwordDigester.digest(password));
    user.setUserRoles(userRoles);
    this.users.put(userId, user);
  }
}
