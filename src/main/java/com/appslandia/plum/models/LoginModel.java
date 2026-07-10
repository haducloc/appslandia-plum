// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.models;

import com.appslandia.common.converters.Converter;
import com.appslandia.common.validators.Password;
import com.appslandia.common.validators.Username;
import com.appslandia.plum.base.BindField;

import jakarta.validation.constraints.NotNull;

/**
 *
 * @author Loc Ha
 *
 */
public class LoginModel {

  @BindField(converter = Converter.STRING_LOWER)
  @NotNull
  @Username
  private String username;

  @NotNull
  @Password
  private String password;

  private boolean rememberMe;

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

  public boolean isRememberMe() {
    return rememberMe;
  }

  public void setRememberMe(boolean rememberMe) {
    this.rememberMe = rememberMe;
  }
}
