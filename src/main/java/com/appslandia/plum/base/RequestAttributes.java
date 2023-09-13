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

package com.appslandia.plum.base;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import jakarta.servlet.http.HttpServletRequest;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class RequestAttributes {

    final Map<String, Object> storedAttributes = new HashMap<>();

    public RequestAttributes(HttpServletRequest request) {
	this(request, (attr) -> true);
    }

    public RequestAttributes(HttpServletRequest request, Function<String, Boolean> storeSelector) {
	Enumeration<String> attributes = request.getAttributeNames();
	while (attributes.hasMoreElements()) {
	    String attribute = attributes.nextElement();

	    if (storeSelector.apply(attribute)) {
		this.storedAttributes.put(attribute, request.getAttribute(attribute));
	    }
	}
    }

    public void restore(HttpServletRequest request) {
	// Remove new attributes added
	Enumeration<String> attributes = request.getAttributeNames();
	while (attributes.hasMoreElements()) {
	    String attribute = attributes.nextElement();

	    if (!this.storedAttributes.containsKey(attribute)) {
		request.removeAttribute(attribute);
	    }
	}

	// Restore attribute values
	for (Entry<String, Object> attribute : this.storedAttributes.entrySet()) {
	    request.setAttribute(attribute.getKey(), attribute.getValue());
	}
    }
}
