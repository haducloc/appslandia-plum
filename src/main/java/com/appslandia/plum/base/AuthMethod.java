// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

/**
 *
 * @author Loc Ha
 *
 */
public enum AuthMethod {

  BASIC("Basic"), FORM("Form"), BEARER("Bearer");

  final String type;

  private AuthMethod(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }
}
