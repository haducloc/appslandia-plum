// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.util.UUID;

import jakarta.security.enterprise.credential.Credential;

/**
 *
 * @author Loc Ha
 *
 */
public class AuthByCodeCredential implements Credential {

  final UUID series;
  final String token;

  final String code;

  public AuthByCodeCredential(UUID series, String token, String code) {
    this.series = series;
    this.token = token;

    this.code = code;
  }

  public UUID getSeries() {
    return series;
  }

  public String getToken() {
    return token;
  }

  public String getCode() {
    return code;
  }
}
