// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.oauth2;

/**
 *
 * @author Loc Ha
 *
 */
public interface OAuth2Account {

  String getEmail();

  String getSubject(String provider);
}
