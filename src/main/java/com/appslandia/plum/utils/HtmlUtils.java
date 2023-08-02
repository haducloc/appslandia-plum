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

package com.appslandia.plum.utils;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import com.appslandia.common.utils.XmlEscaper;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
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

    public static void hardWrap(Writer out) throws IOException {
	out.write(" wrap=\"hard\"");
    }

    public static void multiple(Writer out) throws IOException {
	out.write(" multiple=\"multiple\"");
    }

    public static void novalidate(Writer out) throws IOException {
	out.write(" novalidate=\"novalidate\"");
    }

    public static void escAttribute(Writer out, String name, String value) throws IOException {
	out.write(' ');
	out.write(name);
	out.write("=\"");
	if (value != null) {
	    XmlEscaper.escapeXml(out, value);
	}
	out.write('"');
    }

    public static String toTagId(String fieldName) {
	int len = fieldName.length();
	int i = -1;
	char buf[] = new char[fieldName.length()];

	while (i < len - 1) {
	    i++;
	    char c = fieldName.charAt(i);
	    buf[i] = ((c == '.') || (c == '[') || (c == ']')) ? ('_') : c;
	}
	return new String(buf);
    }

    public static void writeAttributes(Writer out, Map<String, Object> attributes) throws IOException {
	for (Map.Entry<String, Object> attr : attributes.entrySet()) {
	    if (attr.getValue() == null) {
		continue;
	    }

	    out.write(' ');
	    out.write(attr.getKey());
	    out.write("=\"");
	    XmlEscaper.escapeXml(out, attr.getValue().toString());
	    out.write('"');
	}
    }

    public static void main(String[] args) {

	System.out.println(XmlEscaper.escapeXml("<my_id>"));
    }
}
