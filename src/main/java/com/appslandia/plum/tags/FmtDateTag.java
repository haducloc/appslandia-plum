// The MIT License (MIT)
// Copyright © 2015 AppsLandia. All rights reserved.

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

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import javax.servlet.jsp.JspException;

import com.appslandia.common.utils.DateUtils;
import com.appslandia.plum.utils.XmlEscaper;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@Tag(name = "fmtDate", dynamicAttributes = false)
public class FmtDateTag extends TagBase {

	private Object value;
	private String zoneId;

	@Override
	public void doTag() throws JspException, IOException {
		Object val = this.value;
		if (val == null)
			return;

		LocalDate ld = null;
		LocalDate now = null;

		// OffsetDateTime
		if (val instanceof OffsetDateTime) {
			ld = ((OffsetDateTime) val).toLocalDate();
			now = OffsetDateTime.now(((OffsetDateTime) val).getOffset()).toLocalDate();
		}
		// LocalDate
		else if (val instanceof LocalDate) {
			ZoneId zoneId = (this.zoneId != null) ? ZoneId.of(this.zoneId) : ZoneId.systemDefault();

			ld = (LocalDate) val;
			now = LocalDate.now(zoneId);

		}
		// LocalDateTime
		else if (val instanceof LocalDateTime) {
			ZoneId zoneId = (this.zoneId != null) ? ZoneId.of(this.zoneId) : ZoneId.systemDefault();

			ld = ((LocalDateTime) val).toLocalDate();
			now = LocalDate.now(zoneId);

		}
		// Date
		else if (val instanceof java.util.Date) {
			ZoneId zoneId = (this.zoneId != null) ? ZoneId.of(this.zoneId) : ZoneId.systemDefault();

			ld = Instant.ofEpochMilli(((java.util.Date) val).getTime()).atZone(zoneId).toLocalDate();
			now = LocalDate.now(zoneId);

		}
		// Long
		else if (val instanceof Long) {
			ZoneId zoneId = (this.zoneId != null) ? ZoneId.of(this.zoneId) : ZoneId.systemDefault();

			ld = Instant.ofEpochMilli((Long) val).atZone(zoneId).toLocalDate();
			now = LocalDate.now(zoneId);
		}
		// ZonedDateTime
		else if (val instanceof ZonedDateTime) {
			ld = ((ZonedDateTime) val).toLocalDate();
			now = ZonedDateTime.now(((ZonedDateTime) val).getZone()).toLocalDate();

		} else {
			throw new JspException("Invalid input type.");
		}

		Period diff = Period.between(ld, now);

		if (diff.getDays() < 7) {
			String key = String.format("datetime.%d_days_ago", diff.getDays());
			XmlEscaper.escapeXml(this.pageContext.getOut(), this.getRequestContext().res(key));

		} else {
			int weeks = diff.getDays() / 7;
			if (weeks <= 5 && diff.getDays() % 7 == 0) {

				String key = String.format("datetime.%d_weeks_ago", weeks);
				XmlEscaper.escapeXml(this.pageContext.getOut(), this.getRequestContext().res(key));

			} else {
				XmlEscaper.escapeXml(this.pageContext.getOut(), DateUtils.getFormatter(getRequestContext().getLanguage().getPatternDate()).format(ld));
			}
		}
	}

	@Attribute(required = true, rtexprvalue = true)
	public void setValue(Object value) {
		this.value = value;
	}
}
