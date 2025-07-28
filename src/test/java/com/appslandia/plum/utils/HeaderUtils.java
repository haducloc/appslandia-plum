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
