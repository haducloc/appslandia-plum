// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.utils;

import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.HexUtils;

/**
 *
 * @author Loc Ha
 *
 */
public class EtagUtils {

  public static String toWeakEtag(Object etag) {
    Arguments.notNull(etag);

    var value = etag.toString();
    var sb = new StringBuilder(value.length() + 4);
    sb.append("W/\"");
    sb.append(value);
    sb.append('"');
    return sb.toString();
  }

  public static String toStrongEtag(byte[] md5) {
    Arguments.notNull(md5);

    var sb = new StringBuilder(34);
    sb.append('"');
    HexUtils.appendAsHex(sb, md5);
    sb.append('"');
    return sb.toString();
  }

  public static String stripWeakPrefix(String etag) {
    Arguments.notNull(etag);

    if (etag.startsWith("W/")) {
      etag = etag.substring(2).strip();
    }
    return etag;
  }

  public static boolean containsWeakEtag(String headerValue, String etag) {
    Arguments.notNull(etag);

    if (headerValue == null) {
      return false;
    }

    headerValue = headerValue.strip();
    if (headerValue.isEmpty()) {
      return false;
    }
    if ("*".equals(headerValue)) {
      return true;
    }

    var nEtag = stripWeakPrefix(etag);
    for (var item : headerValue.split(",")) {
      item = item.strip();

      if (item.isEmpty()) {
        continue;
      }
      item = stripWeakPrefix(item);

      if (nEtag.equals(item)) {
        return true;
      }
    }
    return false;
  }

  public static boolean containsStrongEtag(String headerValue, String etag) {
    Arguments.notNull(etag);

    if (headerValue == null) {
      return false;
    }

    headerValue = headerValue.strip();
    if (headerValue.isEmpty()) {
      return false;
    }

    if ("*".equals(headerValue)) {
      return true;
    }
    if (etag.startsWith("W/")) {
      return false;
    }

    for (var item : headerValue.split(",")) {
      item = item.strip();
      if (item.isEmpty() || item.startsWith("W/")) {
        continue;
      }

      if (etag.equals(item)) {
        return true;
      }
    }
    return false;
  }
}
