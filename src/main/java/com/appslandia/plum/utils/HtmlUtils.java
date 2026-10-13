// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.utils;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.function.Function;

import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.XmlEscaper;

/**
 *
 * @author Loc Ha
 *
 */
public class HtmlUtils {

  public static void hidden(Writer out) throws IOException {
    out.write(" hidden=\"hidden\"");
  }

  public static void disabled(Writer out) throws IOException {
    out.write(" disabled=\"disabled\"");
  }

  public static void readonly(Writer out) throws IOException {
    out.write(" readonly=\"readonly\"");
  }

  public static void required(Writer out) throws IOException {
    out.write(" required=\"required\"");
  }

  public static void autofocus(Writer out) throws IOException {
    out.write(" autofocus=\"autofocus\"");
  }

  public static void checked(Writer out) throws IOException {
    out.write(" checked=\"checked\"");
  }

  public static void selected(Writer out) throws IOException {
    out.write(" selected=\"selected\"");
  }

  public static void multiple(Writer out) throws IOException {
    out.write(" multiple=\"multiple\"");
  }

  public static void novalidate(Writer out) throws IOException {
    out.write(" novalidate=\"novalidate\"");
  }

  public static void escAttribute(Writer out, String name, String value) throws IOException {
    Arguments.notNull(value);

    out.write(' ');
    out.write(name);
    out.write("=\"");
    XmlEscaper.escapeXml(out, value.toString());
    out.write('"');
  }

  public static void escAttributes(Writer out, Map<String, Object> attributes, Function<String, Boolean> excludeFilter)
      throws IOException {
    for (Map.Entry<String, Object> attr : attributes.entrySet()) {
      if ((attr.getValue() == null) || (excludeFilter != null && excludeFilter.apply(attr.getKey()))) {
        continue;
      }

      out.write(' ');
      out.write(attr.getKey());
      out.write("=\"");
      XmlEscaper.escapeXml(out, attr.getValue().toString());
      out.write('"');
    }
  }
}
