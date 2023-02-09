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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.appslandia.common.base.Config;
import com.appslandia.common.base.MapWrapper;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class PrefCookie extends MapWrapper<String, String> implements Config, Cloneable {
    private static final long serialVersionUID = 1L;

    public static final String REQUEST_ATTRIBUTE_ID = "prefCookie";
    public static final String PARAM_LANGUAGE = "language";

    public static final PrefCookie EMPTY = new PrefCookie(Collections.emptyMap());

    public PrefCookie() {
	super(new HashMap<>());
    }

    public PrefCookie(Map<String, String> newMap) {
	super(newMap);
    }

    @Override
    public String getString(String key) {
	return this.map.get(key);
    }

    public PrefCookie set(String key, String value) {
	this.map.put(key, value);
	return this;
    }

    public PrefCookie set(String key, boolean value) {
	this.map.put(key, Boolean.toString(value));
	return this;
    }

    public PrefCookie set(String key, int value) {
	this.map.put(key, Integer.toString(value));
	return this;
    }

    public PrefCookie set(String key, long value) {
	this.map.put(key, Long.toString(value));
	return this;
    }

    public PrefCookie set(String key, float value) {
	this.map.put(key, Float.toString(value));
	return this;
    }

    public PrefCookie set(String key, double value) {
	this.map.put(key, Double.toString(value));
	return this;
    }

    @Override
    public PrefCookie clone() {
	if (this == EMPTY) {
	    return new PrefCookie();
	}

	return new PrefCookie(new HashMap<>(this.map));
    }
}
