// Licensed under the MIT License.
// See LICENSE file in the project root for details.

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
    // Instant
    else if (val instanceof Instant) {
      ld = ((Instant) val).atZone(zone).toLocalDate();
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

    var days = ChronoUnit.DAYS.between(ld, now);
    var isPast = days >= 0;
    days = Math.abs(days);
    String formattedValue = null;

    if (days < 7) {
      var key = String.format(isPast ? "datetime.%d_days_ago" : "datetime.in_%d_days", days);
      formattedValue = ctx.res(key);

    } else if (days <= 35 && days % 7 == 0) {
      var weeks = days / 7;
      var key = String.format(isPast ? "datetime.%d_weeks_ago" : "datetime.in_%d_weeks", weeks);
      formattedValue = ctx.res(key);

    } else {
      var pattern = ctx.getLanguage().getTemporalPattern(DateUtils.ISO8601_DATE);
      formattedValue = DateUtils.getFormatter(pattern).format(ld);
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
