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

package com.appslandia.plum.jsptags;

import org.owasp.encoder.Encode;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class OwaspFunctions {

    @Function(description = "org.owasp.encoder.Encode.forXmlComment")
    public static String escXmlComment(String value) {
	return Encode.forXmlComment(value);
    }

    @Function(description = "org.owasp.encoder.Encode.forJavaScript")
    public static String escJS(String value) {
	return Encode.forJavaScript(value);
    }

    @Function(description = "org.owasp.encoder.Encode.forJavaScriptAttribute")
    public static String escJSAttr(String value) {
	return Encode.forJavaScriptAttribute(value);
    }

    @Function(description = "org.owasp.encoder.Encode.forJavaScriptBlock")
    public static String escJSBlock(String value) {
	return Encode.forJavaScriptBlock(value);
    }

    @Function(description = "org.owasp.encoder.Encode.forJavaScriptSource")
    public static String escJSSource(String value) {
	return Encode.forJavaScriptSource(value);
    }

    @Function(description = "org.owasp.encoder.Encode.forCssString")
    public static String escCssString(String value) {
	return Encode.forCssString(value);
    }

    @Function(description = "org.owasp.encoder.Encode.forCssUrl")
    public static String escCssUrl(String value) {
	return Encode.forCssUrl(value);
    }
}
