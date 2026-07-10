// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

import com.appslandia.common.utils.Arguments;

/**
 *
 * @author Loc Ha
 *
 */
public class ResKey implements Serializable {
  private static final long serialVersionUID = 1L;

  private String key;
  private Object[] params;

  public ResKey(String key, Object... params) {
    this.key = Arguments.notNull(key);
    this.params = (params != null) ? params : new Object[] { null };
  }

  public String resolve(Resources resources) {
    return resources.get(key, params);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(key);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof ResKey that)) {
      return false;
    }
    return Objects.equals(key, that.key);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "{key=" + key + ", params=" + Arrays.toString(params) + '}';
  }
}
