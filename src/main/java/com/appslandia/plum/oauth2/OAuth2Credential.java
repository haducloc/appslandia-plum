// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.oauth2;

import com.appslandia.common.utils.Arguments;

import jakarta.security.enterprise.credential.Credential;

/**
 *
 * @author Loc Ha
 *
 */
public class OAuth2Credential implements Credential {

  protected final String provider;
  protected final String subject;
  protected final String accessToken;

  public OAuth2Credential(String provider, String subject, String accessToken) {
    this.provider = Arguments.notNull(provider);
    this.subject = Arguments.notNull(subject);
    this.accessToken = Arguments.notNull(accessToken);
  }

  public String getProvider() {
    return provider;
  }

  public String getSubject() {
    return subject;
  }

  public String getAccessToken() {
    return accessToken;
  }
}