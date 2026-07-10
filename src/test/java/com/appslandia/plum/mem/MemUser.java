// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.mem;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.appslandia.common.jpa.EntityBase;
import com.appslandia.plum.base.AuthPrincipal;

/**
 *
 * @author Loc Ha
 *
 */
public class MemUser extends EntityBase {

  private static final long serialVersionUID = 1L;

  private Integer userId;
  private String username;
  private String password;
  private String userRoles;

  @Override
  public Serializable getPk() {
    return userId;
  }

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getUserRoles() {
    return userRoles;
  }

  public void setUserRoles(String userRoles) {
    this.userRoles = userRoles;
  }

  public Map<String, Object> toAttributes() {
    Map<String, Object> map = new HashMap<>();
    map.put(AuthPrincipal.ATTRIBUTE_SUB, this.username);
    map.put(AuthPrincipal.ATTRIBUTE_ROLES, this.userRoles);
    return map;
  }
}
