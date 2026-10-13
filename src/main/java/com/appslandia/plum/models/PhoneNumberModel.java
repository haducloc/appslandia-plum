// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.models;

import com.appslandia.common.converters.Converter;
import com.appslandia.common.validators.PhoneNumber;
import com.appslandia.plum.base.BindField;

import jakarta.validation.constraints.NotNull;

/**
 *
 * @author Loc Ha
 *
 */
public class PhoneNumberModel {

  @BindField(converter = Converter.PHONE_NUMBER)
  @NotNull
  @PhoneNumber
  private String phoneNumber;

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }
}
