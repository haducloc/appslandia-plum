// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.oauth2;

/**
 *
 * @author Loc Ha
 *
 */
public class OAuth2HttpException extends OAuth2Exception {
  private static final long serialVersionUID = 1L;

  private final int statusCode;
  private final String responseBody;

  public OAuth2HttpException(int statusCode, String responseBody) {
    super("OAuth2 HTTP request failed. Status: " + statusCode);

    this.statusCode = statusCode;
    this.responseBody = responseBody;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public String getResponseBody() {
    return responseBody;
  }
}
