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

import java.util.HashMap;

import com.appslandia.common.utils.AssertUtils;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class SortModel extends HashMap<String, Boolean> {
    private static final long serialVersionUID = 1L;

    public static final String REQUEST_ATTRIBUTE_ID = "sortModel";

    private String defBy;
    private String sortBy;
    private Boolean sortAsc;

    public SortModel asc(String... fieldNames) {
	for (String sortBy : fieldNames) {
	    this.put(sortBy, true);
	}
	return this;
    }

    public SortModel desc(String... fieldNames) {
	for (String sortBy : fieldNames) {
	    this.put(sortBy, false);
	}
	return this;
    }

    public SortModel defBy(String defBy) {
	AssertUtils.assertTrue(this.containsKey(defBy));
	this.defBy = defBy;
	return this;
    }

    public void sortBy(String sortBy, Boolean sortAsc) {
	this.sortBy = this.containsKey(sortBy) ? sortBy : this.defBy;
	this.sortAsc = (sortAsc != null) ? sortAsc : this.get(this.sortBy);
    }

    public String sortBy() {
	return AssertUtils.assertStateNotNull(this.sortBy);
    }

    public boolean sortAsc() {
	return AssertUtils.assertStateNotNull(this.sortAsc);
    }

    public Boolean sortAsc(String fieldName) {
	return this.sortBy().equals(fieldName) ? this.sortAsc() : null;
    }

    public Boolean flipAsc(String fieldName) {
	return this.sortBy().equals(fieldName) ? !this.sortAsc() : null;
    }
}
