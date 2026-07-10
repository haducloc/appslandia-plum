// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.appslandia.common.base.TemporalFormatException;

/**
 *
 * @author Loc Ha
 *
 */
public class HeaderUtils {

  private static final TimeZone GMT_ZONE = TimeZone.getTimeZone("GMT");

  private static final String RFC1123_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";

  public static SimpleDateFormat newDateHeaderFormat() {
    var df = new SimpleDateFormat(RFC1123_DATE_FORMAT, Locale.US);
    df.setTimeZone(GMT_ZONE);
    return df;
  }

  public static String toDateHeaderString(long timeInMillis) {
    return newDateHeaderFormat().format(new Date(timeInMillis));
  }

  public static long parseDateHeader(String value) {
    try {
      return newDateHeaderFormat().parse(value).getTime();
    } catch (ParseException ex) {
      throw new TemporalFormatException(ex);
    }
  }
}
