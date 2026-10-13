// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.models;

import com.appslandia.common.validators.Password;

import jakarta.validation.constraints.NotNull;

/**
 *
 * @author Loc Ha
 *
 */
public class NewPasswordModel {

  @NotNull
  @Password
  private String newPassword;

  public String getNewPassword() {
    return newPassword;
  }

  public void setNewPassword(String newPassword) {
    this.newPassword = newPassword;
  }
}
