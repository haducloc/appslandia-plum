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
import java.io.UncheckedIOException;
import java.io.Writer;

import com.appslandia.common.base.StringWriter;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class XmlEscaper {

    private static final int HIGHEST_SPECIAL = '>';

    private static char[][] ESCAPE_XML_CONTENT = new char[HIGHEST_SPECIAL + 1][];
    static {
	ESCAPE_XML_CONTENT['&'] = "&amp;".toCharArray();
	ESCAPE_XML_CONTENT['<'] = "&lt;".toCharArray();
	ESCAPE_XML_CONTENT['>'] = "&gt;".toCharArray();
    }
    private static char[][] ESCAPE_XML = new char[HIGHEST_SPECIAL + 1][];
    static {
	ESCAPE_XML['&'] = "&amp;".toCharArray();
	ESCAPE_XML['<'] = "&lt;".toCharArray();
	ESCAPE_XML['>'] = "&gt;".toCharArray();
	ESCAPE_XML['"'] = "&#34;".toCharArray();
	ESCAPE_XML['\''] = "&#39;".toCharArray();
    }

    public static void escapeXmlContent(Writer out, String s) throws IOException {
	writeEscapeXml(out, s, ESCAPE_XML_CONTENT);
    }

    public static void escapeXml(Writer out, String s) throws IOException {
	writeEscapeXml(out, s, ESCAPE_XML);
    }

    public static String escapeXmlContent(String s) {
	if (s == null) {
	    return null;
	}
	try (StringWriter out = new StringWriter((int) (s.length() * 1.25f))) {
	    writeEscapeXml(out, s, ESCAPE_XML_CONTENT);
	    return out.toString();

	} catch (IOException ex) {
	    throw new UncheckedIOException(ex);
	}
    }

    public static String escapeXml(String s) {
	if (s == null) {
	    return null;
	}
	try (StringWriter out = new StringWriter((int) (s.length() * 1.25f))) {
	    writeEscapeXml(out, s, ESCAPE_XML);
	    return out.toString();

	} catch (IOException ex) {
	    throw new UncheckedIOException(ex);
	}
    }

    static void writeEscapeXml(Writer out, String s, char[][] escapeXml) throws IOException {
	int start = 0;
	char[] srcChars = s.toCharArray();
	int length = s.length();

	for (int i = 0; i < length; i++) {
	    char c = srcChars[i];
	    if (c <= HIGHEST_SPECIAL) {
		char[] escaped = escapeXml[c];
		if (escaped != null) {
		    // add un_escaped portion
		    if (start < i) {
			out.write(srcChars, start, i - start);
		    }
		    // add escaped
		    out.write(escaped);
		    start = i + 1;
		}
	    }
	}
	// add rest of un_escaped portion
	if (start < length) {
	    out.write(srcChars, start, length - start);
	}
    }
}
