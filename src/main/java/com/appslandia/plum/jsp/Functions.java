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

import java.time.temporal.Temporal;
import java.util.Locale;

import com.appslandia.common.base.ToStringBuilder;
import com.appslandia.common.utils.XmlEscaper;
import com.appslandia.plum.base.RequestContext;
import com.appslandia.plum.tags.FnUtils;
import com.appslandia.plum.tags.TagUtils;

/**
 *
 * @author Loc Ha
 *
 */
public class Functions {

  @Function(name = "nowms")
  public static long nowMs() {
    return System.currentTimeMillis();
  }

  @Function(name = "ifcls")
  public static String ifCls(boolean test, String cssClass) {
    if (test) {
      return cssClass;
    }
    return TagUtils.CSS_NOOP;
  }

  @Function(name = "tostr")
  public static String toStr(Object value, int tsDepthLevel) {
    var ts = new ToStringBuilder(tsDepthLevel).toString(value);
    return XmlEscaper.escapeContent(ts);
  }

  @Function(name = "tostrattr")
  public static String toStrAttr(Object value, int tsDepthLevel) {
    var ts = new ToStringBuilder(tsDepthLevel).toString(value);
    return XmlEscaper.escapeAttribute(ts);
  }

  @Function(name = "fmtdt")
  public static String fmtDt(RequestContext ctx, Temporal value) {
    if (value == null) {
      return null;
    }
    var fmtValue = FnUtils.formatTemporal(ctx, value);
    return XmlEscaper.escapeContent(fmtValue);
  }

  @Function(name = "fmtdtattr")
  public static String fmtDtAttr(RequestContext ctx, Temporal value) {
    if (value == null) {
      return null;
    }
    var fmtValue = FnUtils.formatTemporal(ctx, value);
    return XmlEscaper.escapeAttribute(fmtValue);
  }

  @Function(name = "fmtint")
  public static String fmtInt(RequestContext ctx, Number value) {
    if (value == null) {
      return null;
    }
    var fmtValue = FnUtils.formatNumber(ctx, value, 0);
    return XmlEscaper.escapeContent(fmtValue);
  }

  @Function(name = "fmtintattr")
  public static String fmtIntAttr(RequestContext ctx, Number value) {
    if (value == null) {
      return null;
    }
    var fmtValue = FnUtils.formatNumber(ctx, value, 0);
    return XmlEscaper.escapeAttribute(fmtValue);
  }

  @Function(name = "fmtnum")
  public static String fmtNum(RequestContext ctx, Number value, int fractionDigits) {
    if (value == null) {
      return null;
    }
    var fmtValue = FnUtils.formatNumber(ctx, value, fractionDigits);
    return XmlEscaper.escapeContent(fmtValue);
  }

  @Function(name = "fmtnumattr")
  public static String fmtNumAttr(RequestContext ctx, Number value, int fractionDigits) {
    if (value == null) {
      return null;
    }
    var fmtValue = FnUtils.formatNumber(ctx, value, fractionDigits);
    return XmlEscaper.escapeAttribute(fmtValue);
  }

  @Function(name = "fmtper")
  public static String fmtPer(RequestContext ctx, Number value, int fractionDigits) {
    if (value == null) {
      return null;
    }
    var fmtValue = FnUtils.formatPercent(ctx, value, fractionDigits);
    return XmlEscaper.escapeContent(fmtValue);
  }

  @Function(name = "fmtperattr")
  public static String fmtPerAttr(RequestContext ctx, Number value, int fractionDigits) {
    if (value == null) {
      return null;
    }
    var fmtValue = FnUtils.formatPercent(ctx, value, fractionDigits);
    return XmlEscaper.escapeAttribute(fmtValue);
  }

  @Function(name = "fmtcur")
  public static String fmtCur(RequestContext ctx, Number value, int fractionDigits) {
    if (value == null) {
      return null;
    }
    var fmtValue = FnUtils.formatCurrency(ctx, value, fractionDigits);
    return XmlEscaper.escapeContent(fmtValue);
  }

  @Function(name = "fmtcurattr")
  public static String fmtCurAttr(RequestContext ctx, Number value, int fractionDigits) {
    if (value == null) {
      return null;
    }
    var fmtValue = FnUtils.formatCurrency(ctx, value, fractionDigits);
    return XmlEscaper.escapeAttribute(fmtValue);
  }

  @Function(name = "res")
  public static String res(RequestContext ctx, String key) {
    var msg = ctx.res(key);
    return XmlEscaper.escapeContent(msg);
  }

  @Function(name = "resattr")
  public static String resAttr(RequestContext ctx, String key) {
    var msg = ctx.res(key);
    return XmlEscaper.escapeAttribute(msg);
  }

  @Function(name = "esc")
  public static String esc(Object value) {
    if (value == null) {
      return null;
    }
    return XmlEscaper.escapeContent(value.toString());
  }

  @Function(name = "escattr")
  public static String escAttr(Object value) {
    if (value == null) {
      return null;
    }
    return XmlEscaper.escapeAttribute(value.toString());
  }

  @Function(name = "if")
  public static String if_(boolean test, Object value) {
    if (test) {
      if (value != null) {
        return XmlEscaper.escapeContent(value.toString());
      }
    }
    return null;
  }

  @Function(name = "ifattr")
  public static String ifAttr(boolean test, Object value) {
    if (test) {
      if (value != null) {
        return XmlEscaper.escapeAttribute(value.toString());
      }
    }
    return null;
  }

  @Function(name = "iif")
  public static String iif(boolean test, Object trueValue, Object falseValue) {
    if (test) {
      if (trueValue != null) {
        return XmlEscaper.escapeContent(trueValue.toString());
      }
    } else {
      if (falseValue != null) {
        return XmlEscaper.escapeContent(falseValue.toString());
      }
    }
    return null;
  }

  @Function(name = "iifattr")
  public static String iifAttr(boolean test, Object trueValue, Object falseValue) {
    if (test) {
      if (trueValue != null) {
        return XmlEscaper.escapeAttribute(trueValue.toString());
      }
    } else {
      if (falseValue != null) {
        return XmlEscaper.escapeAttribute(falseValue.toString());
      }
    }
    return null;
  }

  @Function(name = "trunc")
  public static String trunc(Object value, int len) {
    if (value == null) {
      return null;
    }
    len = Math.max(len, 1);
    var content = value.toString();
    if (content.length() <= len) {
      return XmlEscaper.escapeContent(content);
    }
    content = content.substring(0, len);
    return XmlEscaper.escapeContent(content + "…");
  }

  @Function(name = "truncattr")
  public static String truncAttr(Object value, int len) {
    if (value == null) {
      return null;
    }
    len = Math.max(len, 1);
    var content = value.toString();
    if (content.length() <= len) {
      return XmlEscaper.escapeAttribute(content);
    }
    content = content.substring(0, len);
    return XmlEscaper.escapeAttribute(content + "…");
  }

  @Function(name = "upper")
  public static String upper(Object value) {
    if (value == null) {
      return null;
    }
    return XmlEscaper.escapeContent(value.toString().toUpperCase(Locale.ROOT));
  }

  @Function(name = "upperattr")
  public static String upperAttr(Object value) {
    if (value == null) {
      return null;
    }
    return XmlEscaper.escapeAttribute(value.toString().toUpperCase(Locale.ROOT));
  }

  @Function(name = "lower")
  public static String lower(Object value) {
    if (value == null) {
      return null;
    }
    return XmlEscaper.escapeContent(value.toString().toLowerCase(Locale.ROOT));
  }

  @Function(name = "lowerattr")
  public static String lowerAttr(Object value) {
    if (value == null) {
      return null;
    }
    return XmlEscaper.escapeAttribute(value.toString().toLowerCase(Locale.ROOT));
  }

  @Function(name = "mask")
  public static String mask(Object value, int len) {
    if (value == null) {
      return null;
    }
    var content = value.toString();
    var maskLen = Math.min(Math.max(len, 1), content.length());

    var masked = "*".repeat(maskLen) + content.substring(maskLen);
    return XmlEscaper.escapeContent(masked);
  }

  @Function(name = "maskattr")
  public static String maskAttr(Object value, int len) {
    if (value == null) {
      return null;
    }
    var content = value.toString();
    var maskLen = Math.min(Math.max(len, 1), content.length());

    var masked = "*".repeat(maskLen) + content.substring(maskLen);
    return XmlEscaper.escapeAttribute(masked);
  }

  @Function(name = "maske")
  public static String maske(Object value, int len) {
    if (value == null) {
      return null;
    }
    var content = value.toString();
    var maskLen = Math.min(Math.max(len, 1), content.length());

    var masked = content.substring(0, content.length() - maskLen) + "*".repeat(maskLen);
    return XmlEscaper.escapeContent(masked);
  }

  @Function(name = "maskeattr")
  public static String maskeAttr(Object value, int len) {
    if (value == null) {
      return null;
    }
    var content = value.toString();
    var maskLen = Math.min(Math.max(len, 1), content.length());

    var masked = content.substring(0, content.length() - maskLen) + "*".repeat(maskLen);
    return XmlEscaper.escapeAttribute(masked);
  }

  @Function(name = "fmtdays")
  public static String fmtDays(RequestContext ctx, Object value, Object zoneId) {
    if (value == null) {
      return null;
    }
    var fmtValue = FnUtils.formatDays(ctx, value, (String) zoneId);
    return XmlEscaper.escapeContent(fmtValue);
  }

  @Function(name = "fmtdaysattr")
  public static String fmtDaysAttr(RequestContext ctx, Object value, Object zoneId) {
    if (value == null) {
      return null;
    }
    var fmtValue = FnUtils.formatDays(ctx, value, (String) zoneId);
    return XmlEscaper.escapeAttribute(fmtValue);
  }
}
