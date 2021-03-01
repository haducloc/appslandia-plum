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
public class SortBag extends HashMap<String, SortBy> {
	private static final long serialVersionUID = 1L;

	public static final String REQUEST_ATTRIBUTE_ID = "sortBag";

	private SortBy current;

	public SortBag put(String... sortBys) {
		for (String sortBy : sortBys) {
			this.put(sortBy, new SortBy(sortBy, true));
		}
		return this;
	}

	public SortBag put(String sortBy, boolean sortAsc) {
		this.put(sortBy, new SortBy(sortBy, sortAsc));
		return this;
	}

	public void setCurrent(String sortBy, Boolean sortAsc, String defSortBy) {
		SortBy curSortBy = null;
		if (sortBy != null) {
			curSortBy = this.get(sortBy);
			if ((curSortBy != null) && (sortAsc != null)) {
				curSortBy.setSortAsc(sortAsc);
			}
		}
		if (curSortBy == null) {
			curSortBy = AssertUtils.assertNotNull(this.get(defSortBy));
		}
		this.current = curSortBy;
	}

	public String getSortBy() {
		return AssertUtils.assertNotNull(this.current).getSortBy();
	}

	public boolean isSortAsc() {
		return AssertUtils.assertNotNull(this.current).isSortAsc();
	}

	public boolean isSortDesc() {
		return AssertUtils.assertNotNull(this.current).isSortDesc();
	}
}
