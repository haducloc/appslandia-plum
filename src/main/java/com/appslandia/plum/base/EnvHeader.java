// The MIT License (MIT)
// Copyright © 2015 Loc Ha

// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:

// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.

// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

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
