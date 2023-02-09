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

package com.appslandia.plum.models;

import com.appslandia.common.validators.ModelValidator;
import com.appslandia.common.validators.Password;
import com.appslandia.common.validators.Validate;

import jakarta.validation.constraints.NotNull;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@Validate(modelValidator = ChangePasswordModel.CONFIRM_PASSWORD_VALIDATOR, message = ConfirmPasswordValidator.MESSAGE_TEMPLATE, reportProperty = "confirmPassword")
public class ChangePasswordModel implements ConfirmPasswordModel {

    public static final String CONFIRM_PASSWORD_VALIDATOR = "changePasswordModel.confirmPasswordValidator";

    static {
	ModelValidator.addValidator(CONFIRM_PASSWORD_VALIDATOR, new ConfirmPasswordValidator<ChangePasswordModel>() {
	});
    }
    @NotNull
    @Password
    private String newPassword;

    @NotNull
    private String confirmPassword;

    @NotNull
    @Password
    private String currentPassword;

    @Override
    public String getNewPassword() {
	return this.newPassword;
    }

    public void setNewPassword(String newPassword) {
	this.newPassword = newPassword;
    }

    @Override
    public String getConfirmPassword() {
	return this.confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
	this.confirmPassword = confirmPassword;
    }

    public String getCurrentPassword() {
	return this.currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
	this.currentPassword = currentPassword;
    }
}
