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

package com.appslandia.plum.jsp;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import com.appslandia.common.utils.DateUtils;
import com.appslandia.common.utils.XmlEscaper;

import jakarta.servlet.jsp.JspException;

/**
 *
 * @author Loc Ha
 *
 */
@Tag(name = "fmtDays", dynamicAttributes = false)
public class FmtDaysTag extends TagBase {

  private Object value;
  private String zoneId;

  @Override
  public void doTag() throws JspException, IOException {
    Object val = this.value;
    if (val == null) {
      return;
    }
    LocalDate ld = null;
    LocalDate now = null;

    // OffsetDateTime
    if (val instanceof OffsetDateTime) {
      ld = ((OffsetDateTime) val).toLocalDate();
      now = OffsetDateTime.now(((OffsetDateTime) val).getOffset()).toLocalDate();
    }
    // LocalDate
    else if (val instanceof LocalDate) {
      ZoneId zone = (this.zoneId != null) ? ZoneId.of(this.zoneId) : ZoneId.systemDefault();

      ld = (LocalDate) val;
      now = LocalDate.now(zone);
    }
    // LocalDateTime
    else if (val instanceof LocalDateTime) {
      ZoneId zone = (this.zoneId != null) ? ZoneId.of(this.zoneId) : ZoneId.systemDefault();

      ld = ((LocalDateTime) val).toLocalDate();
      now = LocalDate.now(zone);
    }
    // Date
    else if (val instanceof java.util.Date) {
      ZoneId zone = (this.zoneId != null) ? ZoneId.of(this.zoneId) : ZoneId.systemDefault();

      ld = Instant.ofEpochMilli(((java.util.Date) val).getTime()).atZone(zone).toLocalDate();
      now = LocalDate.now(zone);
    }
    // Long
    else if (val instanceof Long) {
      ZoneId zone = (this.zoneId != null) ? ZoneId.of(this.zoneId) : ZoneId.systemDefault();

      ld = Instant.ofEpochMilli((Long) val).atZone(zone).toLocalDate();
      now = LocalDate.now(zone);
    }
    // ZonedDateTime
    else if (val instanceof ZonedDateTime) {
      ld = ((ZonedDateTime) val).toLocalDate();
      now = ZonedDateTime.now(((ZonedDateTime) val).getZone()).toLocalDate();

    } else {
      throw new JspException("The given value is unsupported.");
    }
    int days = (int) ChronoUnit.DAYS.between(ld, now);

    if (days < 7) {
      String key = String.format("datetime.%d_days_ago", days);
      XmlEscaper.escapeXml(this.pageContext.getOut(), this.getRequestContext().res(key));

    } else {
      int weeks = days / 7;
      if (weeks <= 5 && days % 7 == 0) {

        String key = String.format("datetime.%d_weeks_ago", weeks);
        XmlEscaper.escapeXml(this.pageContext.getOut(), this.getRequestContext().res(key));

      } else {
        String pattern = getRequestContext().getLanguage().getTemporalPattern(DateUtils.ISO8601_DATE);
        XmlEscaper.escapeXml(this.pageContext.getOut(), DateUtils.getFormatter(pattern).format(ld));
      }
    }
  }

  @Attribute(required = true, rtexprvalue = true)
  public void setValue(Object value) {
    this.value = value;
  }

  @Attribute(required = false, rtexprvalue = true)
  public void setZoneId(String zoneId) {
    this.zoneId = zoneId;
  }
}
