// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

/**
 *
 * @author Loc Ha
 *
 */
public class SelectItem {

  final Object value;
  final String displayName;
  final boolean disabled;

  public SelectItem(Object value, String displayName) {
    this(value, displayName, false);
  }

  public SelectItem(Object value, String displayName, boolean disabled) {
    this.value = value;
    this.displayName = displayName;
    this.disabled = disabled;
  }

  public Object getValue() {
    return value;
  }

  public String getDisplayName() {
    return displayName;
  }

  public boolean isDisabled() {
    return disabled;
  }
}
