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

import java.time.LocalDate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import com.appslandia.common.utils.AssertUtils;
import com.appslandia.common.utils.URLEncoding;
import com.appslandia.plum.utils.ServletUtils;
import com.appslandia.plum.utils.XmlEscaper;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class Functions {

	@Function
	public static String hidden(boolean b) {
		return b ? "hidden=\"hidden\"" : null;
	}

	@Function
	public static String disabled(boolean b) {
		return b ? "disabled=\"disabled\"" : null;
	}

	@Function
	public static String readonly(boolean b) {
		return b ? "readonly=\"readonly\"" : null;
	}

	@Function
	public static String required(boolean b) {
		return b ? "required=\"required\"" : null;
	}

	@Function
	public static String checked(boolean b) {
		return b ? "checked=\"checked\"" : null;
	}

	@Function
	public static String selected(boolean b) {
		return b ? "selected=\"selected\"" : null;
	}

	@Function
	public static String hardWrap(boolean b) {
		return b ? "wrap=\"hard\"" : null;
	}

	@Function
	public static String multiple(boolean b) {
		return b ? "multiple=\"multiple\"" : null;
	}

	@Function
	public static String novalidate(boolean b) {
		return b ? "novalidate=\"novalidate\"" : null;
	}

	@Function(name = "if")
	public static String out(boolean b, Object value) {
		if (b) {
			if (value != null) {
				return value.toString();
			}
		}
		return null;
	}

	@Function(name = "ifEsc")
	public static String outEsc(boolean b, String value) {
		if (b) {
			if (value != null) {
				return XmlEscaper.escapeXml(value);
			}
		}
		return null;
	}

	@Function(name = "ifEscCt")
	public static String outEscCt(boolean b, String value) {
		if (b) {
			if (value != null) {
				return XmlEscaper.escapeXmlContent(value);
			}
		}
		return null;
	}

	@Function
	public static String iif(boolean b, Object trueValue, Object falseValue) {
		if (b) {
			if (trueValue != null) {
				return trueValue.toString();
			}
		} else {
			if (falseValue != null) {
				return falseValue.toString();
			}
		}
		return null;
	}

	@Function
	public static String iifEsc(boolean b, String trueValue, String falseValue) {
		if (b) {
			if (trueValue != null) {
				return XmlEscaper.escapeXml(trueValue);
			}
		} else {
			if (falseValue != null) {
				return XmlEscaper.escapeXml(falseValue);
			}
		}
		return null;
	}

	@Function
	public static String iifEscCt(boolean b, String trueValue, String falseValue) {
		if (b) {
			if (trueValue != null) {
				return XmlEscaper.escapeXmlContent(trueValue);
			}
		} else {
			if (falseValue != null) {
				return XmlEscaper.escapeXmlContent(falseValue);
			}
		}
		return null;
	}

	@Function
	public static String esc(Object value) {
		if (value == null) {
			return null;
		}
		return XmlEscaper.escapeXml(value.toString());
	}

	@Function
	public static String escCt(Object value) {
		if (value == null) {
			return null;
		}
		return XmlEscaper.escapeXmlContent(value.toString());
	}

	@Function
	public static String encParam(String value, boolean spaceToPlus) {
		if (value == null) {
			return null;
		}
		return URLEncoding.encodeParam(value, spaceToPlus);
	}

	@Function
	public static int todayYear() {
		return LocalDate.now().getYear();
	}

	@Function
	public static long now() {
		return System.currentTimeMillis();
	}

	@Function
	public static String cprYears(int startYear) {
		if (startYear == LocalDate.now().getYear())
			return Integer.toString(startYear);
		return String.format("%d-%d", startYear, LocalDate.now().getYear());
	}

	@Function
	public static String fmtMonth(int month) {
		AssertUtils.assertTrue(month >= 1 && month <= 12, "month is invalid.");
		if (month < 10)
			return "0" + month;
		return String.valueOf(month);
	}

	@Function
	public static boolean hasCookie(PageContext ctx, String cookieName) {
		return ServletUtils.getCookieValue((HttpServletRequest) ctx.getRequest(), cookieName) != null;
	}
}
