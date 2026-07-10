// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.util.Locale;

/**
 *
 * @author Loc Ha
 *
 */
public class ActionRoute {

  final String controller;
  final String action;

  public ActionRoute(String controller, String action) {
    this.controller = controller;
    this.action = action;
  }

  @Override
  public int hashCode() {
    int hash = 1, p = 31;
    hash = p * hash + controller.toLowerCase(Locale.ENGLISH).hashCode();
    hash = p * hash + action.toLowerCase(Locale.ENGLISH).hashCode();
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof ActionRoute that)) {
      return false;
    }
    return controller.equalsIgnoreCase(that.controller) && action.equalsIgnoreCase(that.action);
  }

  @Override
  public String toString() {
    return "/" + controller + "/" + action;
  }
}
