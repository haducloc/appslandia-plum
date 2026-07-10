// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.utils;

import com.appslandia.common.utils.ParseUtils;
import com.appslandia.common.utils.StringUtils;

import jakarta.servlet.http.HttpServletRequest;

/**
 *
 * @author Loc Ha
 *
 */
public class ParamUtils {

  public static String getString(HttpServletRequest request, String name) {
    return StringUtils.trimToNull(request.getParameter(name));
  }

  public static String getString(HttpServletRequest request, String name, String defaultValue) {
    var value = getString(request, name);
    return value != null ? value : defaultValue;
  }

  public static int getInt(HttpServletRequest request, String name) {
    return getInt(request, name, 0);
  }

  public static int getInt(HttpServletRequest request, String name, int defaultValue) {
    return ParseUtils.parseInt(getString(request, name), defaultValue);
  }

  public static long getLong(HttpServletRequest request, String name) {
    return getLong(request, name, 0L);
  }

  public static long getLong(HttpServletRequest request, String name, long defaultValue) {
    return ParseUtils.parseLong(getString(request, name), defaultValue);
  }

  public static boolean getBoolean(HttpServletRequest request, String name) {
    return getBoolean(request, name, false);
  }

  public static boolean getBoolean(HttpServletRequest request, String name, boolean defaultValue) {
    return ParseUtils.parseBool(getString(request, name), defaultValue);
  }

  public static double getDouble(HttpServletRequest request, String name) {
    return getDouble(request, name, 0d);
  }

  public static double getDouble(HttpServletRequest request, String name, double defaultValue) {
    return ParseUtils.parseDouble(getString(request, name), defaultValue);
  }
}
