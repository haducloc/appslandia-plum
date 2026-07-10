// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.models;

import com.appslandia.common.validators.Password;
import com.appslandia.plum.validators.CheckModel;

import jakarta.validation.constraints.NotNull;

/**
 *
 * @author Loc Ha
 *
 */
@CheckModel(expr = "model.confirmPassword eq model.newPassword", reportProperty = "confirmPassword")
public class ChangePasswordModel {

  @NotNull
  @Password
  private String newPassword;

  @NotNull
  private String confirmPassword;

  @NotNull
  @Password
  private String currentPassword;

  public String getNewPassword() {
    return newPassword;
  }

  public void setNewPassword(String newPassword) {
    this.newPassword = newPassword;
  }

  public String getConfirmPassword() {
    return confirmPassword;
  }

  public void setConfirmPassword(String confirmPassword) {
    this.confirmPassword = confirmPassword;
  }

  public String getCurrentPassword() {
    return currentPassword;
  }

  public void setCurrentPassword(String currentPassword) {
    this.currentPassword = currentPassword;
  }
}
