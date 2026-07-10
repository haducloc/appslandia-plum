// Licensed under the MIT License.
// See LICENSE file in the project root for details.

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

  static final String DESC_TAG_ATTRIBUTE = "JSTL tag attribute";

  @Function(description = DESC_TAG_ATTRIBUTE)
  public static long __nowMs() {
    return System.currentTimeMillis();
  }

  @Function(description = DESC_TAG_ATTRIBUTE)
  public static String __id(String pathExpression) {
    return TagUtils.toTagId(pathExpression);
  }

  @Function(description = DESC_TAG_ATTRIBUTE)
  public static String __toStr(Object value, int tsLevel) {
    return new ToStringBuilder(tsLevel).toString(value);
  }

  @Function(description = DESC_TAG_ATTRIBUTE)
  public static String __fmtDt(RequestContext ctx, Temporal value) {
    return FnUtils.formatTemporal(ctx, value);
  }

  @Function(description = DESC_TAG_ATTRIBUTE)
  public static String __fmtInt(RequestContext ctx, Number value) {
    return FnUtils.formatNumber(ctx, value, 0);
  }

  @Function(description = DESC_TAG_ATTRIBUTE)
  public static String __fmtNum(RequestContext ctx, Number value, int fractionDigits) {
    return FnUtils.formatNumber(ctx, value, fractionDigits);
  }

  @Function(description = DESC_TAG_ATTRIBUTE)
  public static String __fmtPer(RequestContext ctx, Number value, int fractionDigits) {
    return FnUtils.formatPercent(ctx, value, fractionDigits);
  }

  @Function(description = DESC_TAG_ATTRIBUTE)
  public static String __fmtCur(RequestContext ctx, Number value, int fractionDigits) {
    return FnUtils.formatCurrency(ctx, value, fractionDigits);
  }

  @Function(description = DESC_TAG_ATTRIBUTE)
  public static String __res(RequestContext ctx, String key) {
    return ctx.res(key);
  }

  @Function(description = DESC_TAG_ATTRIBUTE)
  public static String __if(boolean test, Object value) {
    if (test) {
      if (value != null) {
        return value.toString();
      }
    }
    return null;
  }

  @Function(description = DESC_TAG_ATTRIBUTE)
  public static String __iif(boolean test, Object trueValue, Object falseValue) {
    if (test) {
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

  @Function(description = DESC_TAG_ATTRIBUTE)
  public static String __trunc(Object value, int len) {
    if (value == null) {
      return null;
    }
    len = Math.max(len, 1);
    var content = value.toString();
    if (content.length() <= len) {
      return content;
    }
    content = content.substring(0, len);
    return content + "…";
  }

  @Function(description = DESC_TAG_ATTRIBUTE)
  public static String __upper(Object value) {
    if (value == null) {
      return null;
    }
    return value.toString().toUpperCase(Locale.ROOT);
  }

  @Function(description = DESC_TAG_ATTRIBUTE)
  public static String __lower(Object value) {
    if (value == null) {
      return null;
    }
    return value.toString().toLowerCase(Locale.ROOT);
  }

  @Function(description = DESC_TAG_ATTRIBUTE)
  public static String __mask(Object value, int len) {
    if (value == null) {
      return null;
    }
    var content = value.toString();
    var maskLen = Math.min(Math.max(len, 1), content.length());

    return "*".repeat(maskLen) + content.substring(maskLen);
  }

  @Function(description = DESC_TAG_ATTRIBUTE)
  public static String __maskEnd(Object value, int len) {
    if (value == null) {
      return null;
    }
    var content = value.toString();
    var maskLen = Math.min(Math.max(len, 1), content.length());

    return content.substring(0, content.length() - maskLen) + "*".repeat(maskLen);
  }

  @Function(description = DESC_TAG_ATTRIBUTE)
  public static String __fmtDays(RequestContext ctx, Object value, Object zoneId) {
    if (value == null) {
      return null;
    }
    return FnUtils.formatDays(ctx, value, (String) zoneId);
  }

  @Function
  public static long nowMs() {
    return System.currentTimeMillis();
  }

  @Function
  public static String id(String pathExpression) {
    return TagUtils.toTagId(pathExpression);
  }

  @Function
  public static String toStr(Object value, int tsLevel) {
    var ts = new ToStringBuilder(tsLevel).toString(value);
    return XmlEscaper.escapeXml(ts);
  }

  @Function
  public static String fmtDt(RequestContext ctx, Temporal value) {
    if (value == null) {
      return null;
    }
    var fmtValue = FnUtils.formatTemporal(ctx, value);
    return XmlEscaper.escapeXml(fmtValue);
  }

  @Function
  public static String fmtInt(RequestContext ctx, Number value) {
    if (value == null) {
      return null;
    }
    var fmtValue = FnUtils.formatNumber(ctx, value, 0);
    return XmlEscaper.escapeXml(fmtValue);
  }

  @Function
  public static String fmtNum(RequestContext ctx, Number value, int fractionDigits) {
    if (value == null) {
      return null;
    }
    var fmtValue = FnUtils.formatNumber(ctx, value, fractionDigits);
    return XmlEscaper.escapeXml(fmtValue);
  }

  @Function
  public static String fmtPer(RequestContext ctx, Number value, int fractionDigits) {
    if (value == null) {
      return null;
    }
    var fmtValue = FnUtils.formatPercent(ctx, value, fractionDigits);
    return XmlEscaper.escapeXml(fmtValue);
  }

  @Function
  public static String fmtCur(RequestContext ctx, Number value, int fractionDigits) {
    if (value == null) {
      return null;
    }
    var fmtValue = FnUtils.formatCurrency(ctx, value, fractionDigits);
    return XmlEscaper.escapeXml(fmtValue);
  }

  @Function
  public static String res(RequestContext ctx, String key) {
    var msg = ctx.res(key);
    return XmlEscaper.escapeXml(msg);
  }

  @Function
  public static String esc(Object value) {
    if (value == null) {
      return null;
    }
    return XmlEscaper.escapeXml(value.toString());
  }

  @Function(name = "if")
  public static String if_(boolean test, Object value) {
    if (test) {
      if (value != null) {
        return XmlEscaper.escapeXml(value.toString());
      }
    }
    return null;
  }

  @Function
  public static String iif(boolean test, Object trueValue, Object falseValue) {
    if (test) {
      if (trueValue != null) {
        return XmlEscaper.escapeXml(trueValue.toString());
      }
    } else {
      if (falseValue != null) {
        return XmlEscaper.escapeXml(falseValue.toString());
      }
    }
    return null;
  }

  @Function
  public static String trunc(Object value, int len) {
    if (value == null) {
      return null;
    }
    len = Math.max(len, 1);
    var content = value.toString();
    if (content.length() <= len) {
      return XmlEscaper.escapeXml(content);
    }
    content = content.substring(0, len);
    return XmlEscaper.escapeXml(content + "…");
  }

  @Function
  public static String upper(Object value) {
    if (value == null) {
      return null;
    }
    return XmlEscaper.escapeXml(value.toString().toUpperCase(Locale.ROOT));
  }

  @Function
  public static String lower(Object value) {
    if (value == null) {
      return null;
    }
    return XmlEscaper.escapeXml(value.toString().toLowerCase(Locale.ROOT));
  }

  @Function
  public static String mask(Object value, int len) {
    if (value == null) {
      return null;
    }
    var content = value.toString();
    var maskLen = Math.min(Math.max(len, 1), content.length());

    var masked = "*".repeat(maskLen) + content.substring(maskLen);
    return XmlEscaper.escapeXml(masked);
  }

  @Function
  public static String maskEnd(Object value, int len) {
    if (value == null) {
      return null;
    }
    var content = value.toString();
    var maskLen = Math.min(Math.max(len, 1), content.length());

    var masked = content.substring(0, content.length() - maskLen) + "*".repeat(maskLen);
    return XmlEscaper.escapeXml(masked);
  }

  @Function
  public static String fmtDays(RequestContext ctx, Object value, Object zoneId) {
    if (value == null) {
      return null;
    }
    var fmtValue = FnUtils.formatDays(ctx, value, (String) zoneId);
    return XmlEscaper.escapeXml(fmtValue);
  }
}
