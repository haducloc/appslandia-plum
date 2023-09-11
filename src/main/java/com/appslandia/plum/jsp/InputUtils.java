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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.appslandia.plum.base.BrowserFeatures;
import com.appslandia.plum.base.RequestContext;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class InputUtils {

    static final Map<String, Integer> TYPE_FEATURES;

    static {
	Map<String, Integer> map = new HashMap<>();

	map.put("number", BrowserFeatures.INPUT_NUMBER);
	map.put("range", BrowserFeatures.INPUT_RANGE);

	map.put("date", BrowserFeatures.INPUT_DATE);
	map.put("time", BrowserFeatures.INPUT_TIME);
	map.put("datetime-local", BrowserFeatures.INPUT_DATETIME_LOCAL);

	map.put("month", BrowserFeatures.INPUT_MONTH);
	map.put("week", BrowserFeatures.INPUT_WEEK);

	TYPE_FEATURES = Collections.unmodifiableMap(map);
    }

    public static Integer getFeature(String inputType) {
	return TYPE_FEATURES.get(inputType);
    }

    public static boolean willLocalize(RequestContext context, String converter, String type) {
	if ("hidden".equals(type)) {
	    return false;
	}

	// BrowserFeature unparsed
	Integer browserFeatures = context.getBrowserFeatures();
	if (browserFeatures == null) {
	    return true;
	}

	Integer feature = getFeature(type);
	if (feature == null) {
	    return true;
	}

	// HTML5 types
	return (browserFeatures & feature) != feature;
    }
}
