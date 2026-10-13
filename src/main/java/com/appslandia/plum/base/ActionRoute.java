// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.util.Locale;

import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.StringUtils;

/**
 *
 * @author Loc Ha
 *
 */
public class ActionRoute {

  final String language;
  final String controller;
  final String action;

  public ActionRoute(String language, String controller, String action) {
    this.language = language;
    this.controller = Arguments.notNull(controller);
    this.action = Arguments.notNull(action);
  }

  @Override
  public int hashCode() {
    int hash = 1, p = 31;
    hash = p * hash + (language != null ? language.toLowerCase(Locale.ROOT).hashCode() : 0);
    hash = p * hash + controller.toLowerCase(Locale.ROOT).hashCode();
    hash = p * hash + action.toLowerCase(Locale.ROOT).hashCode();
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
    return StringUtils.equalsIgnoreCase(language, that.language) && controller.equalsIgnoreCase(that.controller)
        && action.equalsIgnoreCase(that.action);
  }

  @Override
  public String toString() {
    return "/" + (language == null ? "" : language + "/") + controller + "/" + action;
  }
}
