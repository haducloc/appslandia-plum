// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import jakarta.security.enterprise.credential.Credential;

/**
 *
 * @author Loc Ha
 *
 */
public class JwtCredential implements Credential {

  final String token;

  public JwtCredential(String token) {
    this.token = token;
  }

  public String getToken() {
    return token;
  }
}
