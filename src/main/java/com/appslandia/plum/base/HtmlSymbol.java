// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.io.Serializable;

import com.appslandia.common.utils.Arguments;

/**
 *
 * @author Loc Ha
 *
 */
public class HtmlSymbol implements Serializable {
  private static final long serialVersionUID = 1L;

  final String name;
  final String code;

  public HtmlSymbol(String name, String code) {
    this.name = Arguments.notNull(name);
    this.code = Arguments.notNull(code);
  }

  public String getName() {
    return name;
  }

  public String getCode() {
    return code;
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof HtmlSymbol that)) {
      return false;
    }
    return name.equals(that.name);
  }
}
