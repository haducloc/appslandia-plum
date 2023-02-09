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
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.appslandia.common.utils.StringUtils;

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

    public static void attribute(Writer out, String name, String value) throws IOException {
	out.write(' ');
	out.write(name);
	out.write("=\"");
	if (value != null) {
	    out.write(value);
	}
	out.write('"');
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

    public static String buildId(String fieldName) {
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

    static final String[] BASIC_HTML_TAGS = new String[] { "a", "b", "blockquote", "br", "cite", "code", "dd", "dl", "dt", "em", "i", "li", "ol", "p", "pre", "q", "small", "span",
	    "strike", "strong", "sub", "sup", "u", "ul", "img" };

    static String buildBasicHtmlTagsPattern(String[] tags) {
	StringBuilder sb = new StringBuilder(tags.length * 40);
	for (String tag : tags) {
	    if (sb.length() > 0) {
		sb.append("|");
	    }
	    sb.append("<").append(tag).append("\\s*>").append("|<").append(tag).append("\\s+[^<>]+\\s*>").append("|</\\s*").append(tag).append("\\s*>");
	}
	return sb.toString();
    }

    static final Pattern BASIC_HTML_TAGS_PATTERN = Pattern.compile(buildBasicHtmlTagsPattern(BASIC_HTML_TAGS));

    public static void processHtml(Writer out, String str) throws IOException {
	processHtml(out, str, (ct) -> !StringUtils.isNullOrBlank(ct) ? XmlEscaper.escapeXmlContent(ct) : ct);
    }

    public static void processHtml(Writer out, String str, Function<String, String> textContentProcessor) throws IOException {
	if (str == null) {
	    return;
	}
	Matcher matcher = BASIC_HTML_TAGS_PATTERN.matcher(str);
	int prevEnd = 0;

	while (matcher.find()) {

	    // Text Content
	    if (prevEnd == 0) {
		out.write(textContentProcessor.apply(str.substring(0, matcher.start())));
	    } else {
		out.write(textContentProcessor.apply(str.substring(prevEnd, matcher.start())));
	    }

	    // HTML Tags
	    out.write(matcher.group());
	    prevEnd = matcher.end();
	}

	if (prevEnd < str.length()) {
	    out.write(textContentProcessor.apply(str.substring(prevEnd)));
	}
    }
}
