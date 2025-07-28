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

package com.appslandia.plum.tags;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;

import com.appslandia.common.converters.Converter;
import com.appslandia.common.utils.DateUtils;
import com.appslandia.plum.base.RequestContext;

/**
 *
 * @author Loc Ha
 *
 */
public class FnUtils {

  public static String formatDays(RequestContext ctx, Object val, String zoneId) {
    LocalDate ld = null;
    LocalDate now = null;
    var zone = (zoneId != null) ? ZoneId.of(zoneId) : ZoneId.systemDefault();

    // OffsetDateTime
    if (val instanceof OffsetDateTime) {
      ld = ((OffsetDateTime) val).toLocalDate();
      now = OffsetDateTime.now(((OffsetDateTime) val).getOffset()).toLocalDate();
    }
    // LocalDate
    else if (val instanceof LocalDate) {
      ld = (LocalDate) val;
      now = LocalDate.now(zone);
    }
    // LocalDateTime
    else if (val instanceof LocalDateTime) {
      ld = ((LocalDateTime) val).toLocalDate();
      now = LocalDate.now(zone);
    }
    // Date
    else if (val instanceof java.util.Date) {
      ld = Instant.ofEpochMilli(((java.util.Date) val).getTime()).atZone(zone).toLocalDate();
      now = LocalDate.now(zone);
    }
    // Long
    else if (val instanceof Long) {
      ld = Instant.ofEpochMilli((Long) val).atZone(zone).toLocalDate();
      now = LocalDate.now(zone);
    }
    // ZonedDateTime
    else if (val instanceof ZonedDateTime) {
      ld = ((ZonedDateTime) val).toLocalDate();
      now = ZonedDateTime.now(((ZonedDateTime) val).getZone()).toLocalDate();

    } else {
      throw new IllegalArgumentException("The given value is unsupported.");
    }

    var days = (int) ChronoUnit.DAYS.between(ld, now);
    var isPast = days >= 0;
    days = Math.abs(days);
    String formattedValue = null;

    if (days < 7) {
      var key = String.format(isPast ? "datetime.%d_days_ago" : "datetime.in_%d_days", days);
      formattedValue = ctx.res(key);
    } else {
      var weeks = days / 7;
      if (weeks <= 5 && days % 7 == 0) {
        var key = String.format(isPast ? "datetime.%d_weeks_ago" : "datetime.in_%d_weeks", weeks);
        formattedValue = ctx.res(key);
      } else {
        var pattern = ctx.getLanguage().getTemporalPattern(DateUtils.ISO8601_DATE);
        formattedValue = DateUtils.getFormatter(pattern).format(ld);
      }
    }
    return formattedValue;
  }

  public static String formatTemporal(RequestContext ctx, Temporal value) {
    if (value == null) {
      return null;
    }
    Converter<Object> converter = null;
    if (ctx.getConverterProvider().hasConverter(value.getClass())) {
      converter = ctx.getConverterProvider().getConverter(value.getClass());
    }
    return (converter != null) ? converter.format(value, ctx.getFormatProvider(), true) : value.toString();
  }

  public static String formatNumber(RequestContext ctx, Number value, int fractionDigits) {
    if (value == null) {
      return null;
    }
    return ctx.getFormatProvider().getNumberFormat(null, fractionDigits, true).format(value);
  }

  public static String formatPercent(RequestContext ctx, Number value, int fractionDigits) {
    if (value == null) {
      return null;
    }
    return ctx.getFormatProvider().getPercentFormat(null, fractionDigits, true).format(value);
  }

  public static String formatCurrency(RequestContext ctx, Number value, int fractionDigits) {
    if (value == null) {
      return null;
    }
    return ctx.getFormatProvider().getCurrencyFormat(null, fractionDigits, true).format(value);
  }
}
