// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.oauth2;

/**
 *
 * @author Loc Ha
 *
 */
public class OAuth2Exception extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public OAuth2Exception(String message) {
    super(message);
  }

  public OAuth2Exception(String message, Throwable cause) {
    super(message, cause);
  }

  public OAuth2Exception(Throwable cause) {
    super(cause);
  }
}
