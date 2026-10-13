// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.mem;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.appslandia.common.base.BaseEncoder;
import com.appslandia.common.base.InitializingObject;
import com.appslandia.common.crypto.PasswordHasher;
import com.appslandia.common.crypto.PbeKeyDeriver;
import com.appslandia.common.crypto.Pbkdf2PasswordHasher;

/**
 *
 * @author Loc Ha
 *
 */
public class MemUserService extends InitializingObject {

  private Map<Integer, MemUser> users = new TreeMap<>();

  final PasswordHasher passwordHasher = new Pbkdf2PasswordHasher().setPbeKeyDeriver(new PbeKeyDeriver()
      .setKdfAlgorithm("PBKDF2WithHmacSHA256").setKeySize(32).setIterationCount(50_000).setSaltSize(16));

  @Override
  protected void init() throws Exception {
    createUser(1, "user1", "pass1", null);
    createUser(2, "manager1", "pass1", "manager");
    createUser(3, "admin1", "pass1", "admin");
    createUser(4, "sysadmin1", "pass1", "admin,manager");

    this.users = Collections.unmodifiableMap(this.users);
  }

  public List<MemUser> getAllUsers() {
    initialize();
    return users.values().stream().map(MemUserService::copy).collect(Collectors.toList());
  }

  public MemUser getByUserId(int userId) {
    initialize();
    var user = users.get(userId);
    return user != null ? copy(user) : null;
  }

  public MemUser getByUsername(String username) {
    initialize();
    return users.values().stream().filter(e -> e.getUsername().equalsIgnoreCase(username)).map(MemUserService::copy)
        .findFirst().orElse(null);
  }

  public boolean verifyPassword(String password, String hashPassword) {
    initialize();

    var passwordHashAsBytes = BaseEncoder.BASE64.decode(hashPassword);
    return passwordHasher.verify(password.toCharArray(), passwordHashAsBytes);
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

    var passwordHashAsBytes = passwordHasher.compute(password.toCharArray());
    var passwordHash = BaseEncoder.BASE64.encode(passwordHashAsBytes);
    user.setPassword(passwordHash);

    user.setUserRoles(userRoles);
    users.put(userId, user);
  }
}
