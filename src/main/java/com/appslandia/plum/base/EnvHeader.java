// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.util.Locale;
import java.util.Objects;

import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.StringUtils;

/**
 *
 * @author Loc Ha
 *
 */
public class EnvHeader {
  private final String env;
  private final String name;

  public EnvHeader(String env, String name) {
    this.env = env;
    this.name = Arguments.notNull(name);
  }

  public String getEnv() {
    return env;
  }

  public String getName() {
    return name;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof EnvHeader other)) {
      return false;
    }
    return StringUtils.equalsIgnoreCase(env, other.env) && StringUtils.equalsIgnoreCase(name, other.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash((env != null) ? env.toLowerCase(Locale.ENGLISH) : null, name.toLowerCase(Locale.ENGLISH));
  }

  @Override
  public String toString() {
    return (env != null) ? ("ENV." + env + "." + name) : name;
  }

  public static EnvHeader parse(String name) {
    if (!StringUtils.startsWithIgnoreCase(name, "ENV.")) {
      return new EnvHeader(null, name);
    }

    var span = name.substring(4);
    var firstDot = span.indexOf('.');
    if (firstDot <= 0) {
      return null;
    }

    var firstPart = span.substring(0, firstDot).strip();
    if (firstPart.isEmpty()) {
      return null;
    }

    var rest = span.substring(firstDot + 1).strip();
    if (rest.isEmpty()) {
      return null;
    }
    return new EnvHeader(firstPart, rest);
  }
}
