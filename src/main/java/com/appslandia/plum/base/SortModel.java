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
public class SortModel extends HashMap<String, SortBy> {
    private static final long serialVersionUID = 1L;

    public static final String REQUEST_ATTRIBUTE_ID = "sortModel";

    private SortBy current;
    private String defSortBy;

    public SortModel set(String... sortBys) {
	for (String sortBy : sortBys) {
	    this.put(sortBy, new SortBy(sortBy, true));
	}
	return this;
    }

    public SortModel set(String sortBy, boolean sortAsc) {
	this.put(sortBy, new SortBy(sortBy, sortAsc));
	return this;
    }

    public SortModel setDefault(String defSortBy) {
	this.defSortBy = defSortBy;
	return this;
    }

    public void setCurrent(SortBy sortBy) {
	SortBy curSb = null;
	if (sortBy.getSortBy() != null) {
	    curSb = this.get(sortBy.getSortBy());

	    if ((curSb != null) && (sortBy.getSortAsc() != null)) {
		curSb.setSortAsc(sortBy.getSortAsc());
	    }
	}
	if (curSb == null) {
	    curSb = AssertUtils.assertNotNull(this.get(this.defSortBy));
	}
	this.current = curSb;
    }

    public String getSortBy() {
	return AssertUtils.assertNotNull(this.current).getSortBy();
    }

    public boolean isSortAsc() {
	return AssertUtils.assertNotNull(this.current).getSortAsc();
    }
}
