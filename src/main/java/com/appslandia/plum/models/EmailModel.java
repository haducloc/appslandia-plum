// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.models;

import com.appslandia.common.converters.Converter;
import com.appslandia.common.validators.Email;
import com.appslandia.plum.base.BindField;

import jakarta.validation.constraints.NotNull;

/**
 *
 * @author Loc Ha
 *
 */
public class EmailModel {

  @BindField(converter = Converter.STRING_LOWER)
  @NotNull
  @Email
  private String email;

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }
}
