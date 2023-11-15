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

import com.appslandia.common.base.Bind;
import com.appslandia.common.converters.Converter;
import com.appslandia.common.validators.Email;
import com.appslandia.common.validators.MaxLength;

import jakarta.validation.constraints.NotNull;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class ContactUsModel {

  @NotNull
  @MaxLength(50)
  private String yourName;

  @Bind(converter = Converter.STRING_LC)
  @NotNull
  @Email
  private String yourEmail;

  @NotNull
  @MaxLength(255)
  private String subject;

  @Bind(converter = Converter.TEXT)
  @NotNull
  @MaxLength(8000)
  private String message;

  public String getYourName() {
    return this.yourName;
  }

  public void setYourName(String yourName) {
    this.yourName = yourName;
  }

  public String getYourEmail() {
    return this.yourEmail;
  }

  public void setYourEmail(String yourEmail) {
    this.yourEmail = yourEmail;
  }

  public String getMessage() {
    return this.message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getSubject() {
    return this.subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }
}
