// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.utils;

import java.util.regex.Pattern;

import com.appslandia.common.utils.NormalizeUtils;

/**
 *
 * @author Loc Ha
 *
 */
public class HttpSecurityUtils {

  private static final Pattern INVALID_HEADER_VAL_PATTERN = Pattern.compile("[\"\\x00-\\x1F\\x7F]");

  public static String sanitizeHttpHeaderValue(String value) {
    if (value == null) {
      return null;
    }
    return NormalizeUtils.normalize(value, "", INVALID_HEADER_VAL_PATTERN);
  }

  public static String sanitizeHostHeaderValue(String host) {
    if (host == null) {
      return null;
    }

    host = host.strip();
    if (host.isEmpty()) {
      return null;
    }

    for (var i = 0; i < host.length(); i++) {
      var c = host.charAt(i);
      if (c == '\r' || c == '\n' || c == '/' || c == '\\' || c == '\t' || c == '"') {
        return null;
      }
    }

    return host;
  }
}
