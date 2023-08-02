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

package com.appslandia.plum.jsp;

import com.appslandia.plum.base.BrowserFeatures;
import com.appslandia.plum.base.RequestContext;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class TextBoxUtils {

    public static String formatValue(RequestContext requestContext, String type, Object value, String converter) {
	if (value == null) {
	    return null;
	}
	if (value.getClass() == String.class) {
	    return (String) value;
	}

	// hidden
	if ("hidden".equals(type)) {
	    return requestContext.fmt(value, converter, false);
	}

	// text
	if ("text".equals(type)) {
	    return requestContext.fmt(value, converter, true);
	}

	// number
	if ("number".equals(type)) {
	    if (requestContext.getBrowserFeatures() == null) {
		return requestContext.fmt(value, converter, false);
	    } else {
		return requestContext.fmt(value, converter, (requestContext.getBrowserFeatures() & BrowserFeatures.INPUT_NUMBER) != BrowserFeatures.INPUT_NUMBER);
	    }
	}

	// range
	if ("range".equals(type)) {
	    if (requestContext.getBrowserFeatures() == null) {
		return requestContext.fmt(value, converter, false);
	    } else {
		return requestContext.fmt(value, converter, (requestContext.getBrowserFeatures() & BrowserFeatures.INPUT_RANGE) != BrowserFeatures.INPUT_RANGE);
	    }
	}

	// date
	if ("date".equals(type)) {
	    if (requestContext.getBrowserFeatures() == null) {
		return requestContext.fmt(value, converter, false);
	    } else {
		return requestContext.fmt(value, converter, (requestContext.getBrowserFeatures() & BrowserFeatures.INPUT_DATE) != BrowserFeatures.INPUT_DATE);
	    }
	}

	// time
	if ("time".equals(type)) {
	    if (requestContext.getBrowserFeatures() == null) {
		return requestContext.fmt(value, converter, false);
	    } else {
		return requestContext.fmt(value, converter, (requestContext.getBrowserFeatures() & BrowserFeatures.INPUT_TIME) != BrowserFeatures.INPUT_TIME);
	    }
	}

	// datetime-local
	if ("datetime-local".equals(type)) {
	    if (requestContext.getBrowserFeatures() == null) {
		return requestContext.fmt(value, converter, false);
	    } else {
		return requestContext.fmt(value, converter, (requestContext.getBrowserFeatures() & BrowserFeatures.INPUT_DATETIME_LOCAL) != BrowserFeatures.INPUT_DATETIME_LOCAL);
	    }
	}

	// month
	if ("month".equals(type)) {
	    if (requestContext.getBrowserFeatures() == null) {
		return requestContext.fmt(value, converter, false);
	    } else {
		return requestContext.fmt(value, converter, (requestContext.getBrowserFeatures() & BrowserFeatures.INPUT_MONTH) != BrowserFeatures.INPUT_MONTH);
	    }
	}

	// week
	if ("week".equals(type)) {
	    if (requestContext.getBrowserFeatures() == null) {
		return requestContext.fmt(value, converter, false);
	    } else {
		return requestContext.fmt(value, converter, (requestContext.getBrowserFeatures() & BrowserFeatures.INPUT_WEEK) != BrowserFeatures.INPUT_WEEK);
	    }
	}

	// Others: localize = true
	return requestContext.fmt(value, converter, true);
    }
}
