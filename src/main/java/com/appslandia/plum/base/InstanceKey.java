// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.lang.annotation.Annotation;
import java.util.Objects;

/**
 *
 * @author Loc Ha
 *
 */
public class InstanceKey {

  final Class<?> type;
  final Annotation qualifier;

  public InstanceKey(Class<?> type, Annotation qualifier) {
    this.type = type;
    this.qualifier = qualifier;
  }

  public Class<?> getType() {
    return type;
  }

  public Annotation getQualifier() {
    return qualifier;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (!(obj instanceof InstanceKey that)) {
      return false;
    }

    return Objects.equals(type, that.type) && Objects.equals(qualifier, that.qualifier);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, qualifier);
  }
}
