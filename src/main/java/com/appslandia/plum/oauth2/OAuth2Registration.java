// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.oauth2;

import java.io.Serializable;
import java.time.Instant;

/**
 *
 * @author Loc Ha
 *
 */
public class OAuth2Registration implements Serializable {
  private static final long serialVersionUID = 1L;

  public static final String SESSION_ATTRIBUTE_ID = "__oauth2_registration";

  final String module;
  final String provider;
  final String accessToken;
  final Instant expiresAtUtc;
  final String returnUrl;
  final OAuth2UserInfo userInfo;

  public OAuth2Registration(String module, String provider, String accessToken, Instant expiresAtUtc, String returnUrl,
      OAuth2UserInfo userInfo) {

    this.module = module;
    this.provider = provider;
    this.accessToken = accessToken;
    this.expiresAtUtc = expiresAtUtc;
    this.returnUrl = returnUrl;
    this.userInfo = userInfo;
  }

  public String getModule() {
    return module;
  }

  public String getProvider() {
    return provider;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public Instant getExpiresAtUtc() {
    return expiresAtUtc;
  }

  public String getReturnUrl() {
    return returnUrl;
  }

  public OAuth2UserInfo getUserInfo() {
    return userInfo;
  }
}
