// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.models;

import com.appslandia.common.validators.MaxLength;
import com.appslandia.common.validators.Password;
import com.appslandia.plum.validators.CheckModel;

import jakarta.validation.constraints.NotNull;

/**
 *
 * @author Loc Ha
 *
 */
@CheckModel(expr = "model.confirmPassword eq model.newPassword", reportProperty = "confirmPassword")
public class IdentityResetPasswordModel {

  @NotNull
  @MaxLength(255)
  private String series;

  @NotNull
  @MaxLength(255)
  private String token;

  @NotNull
  @MaxLength(255)
  private String identity;

  @NotNull
  @Password
  private String newPassword;

  @NotNull
  private String confirmPassword;

  public String getSeries() {
    return series;
  }

  public void setSeries(String series) {
    this.series = series;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getIdentity() {
    return identity;
  }

  public void setIdentity(String identity) {
    this.identity = identity;
  }

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
}
