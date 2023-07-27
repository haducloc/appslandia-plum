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

package com.appslandia.plum.pebble;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class PebbleUtils {

    public static String[] getHeaderValues(HttpServletRequest request, String headerName) {
	Enumeration<String> headerValues = request.getHeaders(headerName);
	return toArray(headerValues);
    }

    public static String[] getRequestAttrNames(HttpServletRequest request) {
	return toArray(request.getAttributeNames());
    }

    public static String[] getHeaderNames(HttpServletRequest request) {
	return toArray(request.getHeaderNames());
    }

    private static String[] toArray(Enumeration<String> enumer) {
	if (enumer == null) {
	    return null;
	}

	List<String> values = new ArrayList<>();
	while (enumer.hasMoreElements()) {
	    values.add(enumer.nextElement());
	}
	return values.toArray(new String[values.size()]);
    }
}
