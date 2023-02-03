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

import com.appslandia.common.base.InitializeObject;
import com.appslandia.common.utils.AssertUtils;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class SortConfig extends InitializeObject {

    private Map<String, Boolean> sortBys = new HashMap<>();
    private String sortDefault;

    @Override
    protected void init() throws Exception {
	AssertUtils.assertNotNull(this.sortDefault);

	this.sortBys = Collections.unmodifiableMap(this.sortBys);
    }

    public SortConfig asc(String... ascFields) {
	assertNotInitialized();

	for (String sortBy : ascFields) {
	    this.sortBys.put(sortBy, true);
	}
	return this;
    }

    public SortConfig desc(String... descFields) {
	assertNotInitialized();

	for (String sortBy : descFields) {
	    this.sortBys.put(sortBy, false);
	}
	return this;
    }

    public SortConfig sortDefault(String sortDefault) {
	assertNotInitialized();

	AssertUtils.assertTrue(this.sortBys.containsKey(sortDefault));
	this.sortDefault = sortDefault;
	return this;
    }

    public String sortDefault() {
	initialize();
	return this.sortDefault;
    }

    public Boolean sortAsc(String fieldName) {
	initialize();
	return this.sortBys.get(fieldName);
    }

    public boolean contains(String fieldName) {
	initialize();
	return this.sortBys.containsKey(fieldName);
    }
}
