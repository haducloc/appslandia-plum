// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.oauth2;

import java.io.Serializable;

import com.appslandia.common.utils.Arguments;

/**
 *
 * @author Loc Ha
 *
 */
public class OAuth2UserInfo implements Serializable {
  private static final long serialVersionUID = 1L;

  final String subject;

  final String email;
  final Boolean emailVerified;

  final String firstName;
  final String lastName;
  final String displayName;

  public OAuth2UserInfo(String subject, String email, Boolean emailVerified, String firstName, String lastName,
      String displayName) {

    this.subject = Arguments.notNull(subject);
    this.email = Arguments.notNull(email);
    this.emailVerified = emailVerified;
    this.firstName = firstName;
    this.lastName = lastName;
    this.displayName = displayName;
  }

  public String getSubject() {
    return subject;
  }

  public String getEmail() {
    return email;
  }

  public Boolean getEmailVerified() {
    return emailVerified;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getDisplayName() {
    return displayName;
  }
}
