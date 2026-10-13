// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.util.UUID;

import com.appslandia.common.utils.Arguments;

import jakarta.security.enterprise.credential.Credential;

/**
 *
 * @author Loc Ha
 *
 */
public class CodeLoginCredential implements Credential {

  final UUID series;
  final String token;
  final String code;

  public CodeLoginCredential(UUID series, String token, String code) {
    this.series = Arguments.notNull(series);
    this.token = Arguments.notNull(token);
    this.code = Arguments.notNull(code);
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
