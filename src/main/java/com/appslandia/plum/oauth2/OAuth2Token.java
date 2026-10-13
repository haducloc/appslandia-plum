// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.oauth2;

import jakarta.json.bind.annotation.JsonbCreator;

/**
 *
 * @author Loc Ha
 *
 */
public class OAuth2Token {

  private final String access_token;
  private final String refresh_token;
  private final String token_type;
  private final Long expires_in;
  private final String scope;

  @JsonbCreator
  public OAuth2Token(String access_token, String refresh_token, String token_type, Long expires_in, String scope) {
    this.access_token = access_token;
    this.refresh_token = refresh_token;
    this.token_type = token_type;
    this.expires_in = expires_in;
    this.scope = scope;
  }

  public String getAccess_token() {
    return access_token;
  }

  public String getRefresh_token() {
    return refresh_token;
  }

  public String getToken_type() {
    return token_type;
  }

  public Long getExpires_in() {
    return expires_in;
  }

  public String getScope() {
    return scope;
  }
}
